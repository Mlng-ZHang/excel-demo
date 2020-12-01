package com.zm.excel.demo.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.zm.excel.demo.dto.EasyExcelData;
import com.zm.excel.demo.dto.ExcelData;
import com.zm.excel.demo.dto.ExcelRecord;
import com.zm.excel.demo.excel.ExcelBatchWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
@RequestMapping("/excel")
@Slf4j
public class ExcelController {

    @Resource(name = "excelManageThreadPool")
    private ThreadPoolTaskExecutor executor;

    @GetMapping("/test")
    public void testGet(HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        setResponse(response);
        OutputStream outputStream = response.getOutputStream();
        outputStream.flush();
        ExcelBatchWriter<Integer, ExcelData> excelBatchWriter = new ExcelBatchWriter<Integer, ExcelData>(executor,
                "分页", ExcelData.class, outputStream) {
            @Override
            protected List<ExcelData> getData(Integer minId, Integer maxId, Object queryParam) {
                Random random = new Random();
                List<ExcelData> excelDatas = new ArrayList<>();
                for (int i = minId; i < maxId; i ++){
                    ExcelData data = new ExcelData();
                    data.setId(i);
                    data.setAge(random.nextInt(80));
                    data.setName("用户_" + UUID.randomUUID().toString().replace("-",""));
                    data.setCreateTime(new Date());
                    excelDatas.add(data);
                }
                return excelDatas;
            }

            @Override
            protected ExcelRecord<Integer> getMinMaxId(Integer minId, Integer batchSize, Object queryParam) {
                if (minId == null){
                    minId = 0;
                }
                ExcelRecord<Integer> record = new ExcelRecord<>();
                record.setMinId(minId);
                record.setMaxId(minId + batchSize);
                return record;
            }

            @Override
            protected Long getTotal(Object queryParam) {
                return 1_000_000L;
            }
        };
        excelBatchWriter.prepareData();
        excelBatchWriter.write();
        excelBatchWriter.finished();
        long end = System.currentTimeMillis();
        log.info("导出时间：{} ms", (end - start));
    }

    @GetMapping("/test/easy")
    public void testEasyExcel(HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        setResponse(response);
        OutputStream outputStream = response.getOutputStream();
        outputStream.flush();
        ExcelWriter excelWriter = EasyExcel.write(outputStream).build();
        WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "分页1").head(EasyExcelData.class).build();
        WriteSheet writeSheet2 = EasyExcel.writerSheet(1, "分页2").head(EasyExcelData.class).build();
        excelWriter.write(generateData(0,500000), writeSheet1);
        excelWriter.write(generateData(500000,1000000), writeSheet2);
        excelWriter.finish();
        long end = System.currentTimeMillis();
        log.info("导出时间：{} ms", (end - start));
    }

    /**
     * 设置返回头信息
     * @param response
     * @throws IOException
     */
    private void setResponse(HttpServletResponse response) throws IOException {
        String fileName = "测试";
        try {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            log.error("文件名转换异常:{}", ExceptionUtils.getStackTrace(e));
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
    }

    /**
     * 模拟数据生产
     * @param minId
     * @param maxId
     * @return
     */
    private List<EasyExcelData> generateData(int minId, int maxId){
        Random random = new Random();
        List<EasyExcelData> dataList = new ArrayList<>();
        for (int i = minId; i < maxId; i ++){
            EasyExcelData data = new EasyExcelData();
            data.setId(i);
            data.setAge(random.nextInt(80));
            data.setName("用户_" + UUID.randomUUID().toString().replace("-",""));
            data.setCreateTime(new Date());
            dataList.add(data);
        }
        return dataList;
    }

}
