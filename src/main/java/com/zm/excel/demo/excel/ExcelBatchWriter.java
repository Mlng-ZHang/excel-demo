package com.zm.excel.demo.excel;

import com.zm.excel.demo.annotation.ColumnWidth;
import com.zm.excel.demo.annotation.DateTimeFormat;
import com.zm.excel.demo.annotation.ExcelProperty;
import com.zm.excel.demo.dto.ExcelRecord;
import com.zm.excel.demo.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ExcelBatchWriter< E:筛选记录的唯一id类型,T:查询结果的返回实体类型>
 * @Author: zhangming
 * @Date 2020/11/13 9:12
 * @Description:
 */
@Slf4j
public abstract class ExcelBatchWriter<E,T> {

    /**
     * 默认每个sheet页记录数50w
     */
    public static final int DEFAULT_BATCH_SIZE = 500000;
    /**
     * 保存sheet
     */
    private final List<Sheet> sheets = new ArrayList<>();
    /**
     * 表头
     */
    private final List<String> headers = new ArrayList<>();

    /**
     * 实体字段名
     */
    private final List<Field> fields = new ArrayList<>();
    /**
     * 线程池
     */
    private final ThreadPoolTaskExecutor executor;
    /**
     * sheet名称
     */
    private final String sheetName;
    /**
     * 输出流
     */
    private final OutputStream outputStream;
    /**
     * 列宽
     */
    private Integer columnWidth = -1;
    /**
     * excel工作簿
     */
    private Workbook workbook;
    /**
     * header头样式
     */
    private CellStyle headerStyle;
    /**
     * 内容样式
     */
    private CellStyle contentStyle;
    /**
     * sheet页数
     */
    private int sheetIndex;


    /**
     * ExcelBatchWriter 构造器
     * @param executor 线程池
     * @param sheetName sheet页前缀名称，以sheet-index展示
     * @param clz 实体类型class
     * @param outputStream 输出流
     */
    public ExcelBatchWriter(ThreadPoolTaskExecutor executor, String sheetName, Class<T> clz, OutputStream outputStream){
        this.executor = executor;
        this.sheetIndex = 1;
        this.sheetName = sheetName;
        this.outputStream = outputStream;
        init(clz);
    }

    /**
     * 初始化
     * @param clz
     */
    private void init(Class<T> clz){
        this.workbook = new SXSSFWorkbook(-1);
        this.headerStyle = getHeaderCellStyle();
        this.contentStyle = getContentCellStyle();
        Class tempClass = clz;
        // 设置列宽
        ColumnWidth columnWidth = clz.getAnnotation(ColumnWidth.class);
        if(columnWidth != null){
            this.columnWidth = columnWidth.value();
        }
        List<Field> tempFieldList = new ArrayList<>();
        while (tempClass != null){
            Collections.addAll(tempFieldList, tempClass.getDeclaredFields());
            tempClass = tempClass.getSuperclass();
        }
        for (Field field : tempFieldList) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            headers.addAll(Arrays.asList(excelProperty.value()));
            fields.add(field);
        }
    }

    /**
     * 准备数据
     */
    public void prepareData() throws IOException {
        this.prepareData(DEFAULT_BATCH_SIZE,null);
    }

    /**
     * 准备数据
     * @param queryParam
     */
    public void prepareData(Object queryParam) throws IOException {
        this.prepareData(DEFAULT_BATCH_SIZE, queryParam);
    }

    /**
     * 准备数据
     * @param batchSize
     * @param queryParam
     */
    public void prepareData(int batchSize, Object queryParam) throws IOException {
        // 获取记录总数
        long total = getTotal(queryParam);
        if(total == 0){
            // 当记录为空的时候，需要返回空文件
            Sheet noContentSheet = createSheet();
            ((SXSSFSheet) noContentSheet).flushRows();
            return;
        }
        int batchCount = (int) Math.ceil((double) (total) / batchSize);
        // 提前创建好sheet,让前端更快接收到excel头文件流
        for (int i = 0; i < batchCount; i++ ){
            sheets.add(createSheet());
        }
        executor.execute(()->{
            Sheet first = sheets.get(0);
            ExcelRecord<E> record = getMinMaxId(null, batchSize, queryParam);
            E startIndex = record.getMinId();
            E endIndex = record.getMaxId();
            writeSheetData(first, startIndex, endIndex, queryParam);
            for (int i = 1; i < sheets.size();i++){
                ExcelRecord<E> nextRecord = getMinMaxId(endIndex, batchSize, queryParam);
                writeSheetData(sheets.get(i), nextRecord.getMinId(), nextRecord.getMaxId(), queryParam);
            }
        });
    }

    /**
     * 写sheet数据
     * @param minId
     * @param maxId
     */
    private void writeSheetData(Sheet sheet, E minId, E maxId, Object queryParam){
        executor.execute(()->{
            try {
                log.info("query data starting...");
                List<T> datas = getData(minId, maxId, queryParam);
                log.info("query data finished...");
                createRow(sheet, datas);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 触发写入流操作
     */
    public void write() throws IOException{
        this.workbook.multiplyWrite(this.outputStream);
    }

    /**
     * 需要实现获取批次数据
     * @param minId
     * @param maxId
     * @param queryParam
     * @return
     */
    protected abstract List<T> getData(E minId, E maxId, Object queryParam);

    /**
     * 获取最大、最小id
     * @param minId
     * @param batchSize
     * @param queryParam
     * @return
     */
    protected abstract ExcelRecord<E> getMinMaxId(E minId, Integer batchSize, Object queryParam);

    /**
     * 获取查询记录总数
     * @param queryParam
     * @return
     */
    protected abstract Long getTotal(Object queryParam);

    /**
     * 创建
     * @return
     */
    private Sheet createSheet(){
        Sheet sheet = workbook.createSheet(sheetName + "-" + sheetIndex++);
        sheet.setDefaultColumnWidth(this.columnWidth);
        createHeader(sheet);
        return sheet;
    }

    /**
     * 创建表头
     */
    private void createHeader(Sheet sheet) {
        //输出表头
        Row row = sheet.createRow(0);
        Cell cell;
        for (int i=0;i < headers.size();i++ ){
            cell = row.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(headers.get(i));
        }
    }

    /**
     * 创建行数据
     */
    private void createRow(Sheet sheet, List<T> datas) throws IOException {
        int rownum = 1;
        for (T data :datas){
            Row row = sheet.createRow(rownum++);
            int cellNum = 0;
            Map<String, Object> beanMap = BeanUtils.beanToMap(data);
            for (Field field : fields){
                Cell cell = row.createCell(cellNum++);
                cell.setCellStyle(contentStyle);
                Object value = beanMap.get(field.getName());
                if(value instanceof Date){
                    DateTimeFormat format = field.getAnnotation(DateTimeFormat.class);
                    if(format != null){
                        cell.setCellValue(new SimpleDateFormat(format.value()).format(value));
                    }
                }else {
                    cell.setCellValue(String.valueOf(value));
                }
            }
            if (rownum % 100 == 0) {
                // 每100行，刷到临时文件中
                ((SXSSFSheet)sheet).flushRows(100);
            }
        }
        ((SXSSFSheet)sheet).flushRows();
    }

    /**
     * 关闭流
     */
    public void finished(){
        if(workbook instanceof SXSSFWorkbook){
            while (!((SXSSFWorkbook) workbook).isFinished()){
            }
            ((SXSSFWorkbook) workbook).dispose();
        }
    }

    /**
     * 获取内容格式
     * @return
     */
    private CellStyle getContentCellStyle(){
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(workbook.createFont());
        return cellStyle;
    }

    /**
     * 获取表头格式
     * @return
     */
    private CellStyle getHeaderCellStyle(){
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setLocked(true);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)14);
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }
}
