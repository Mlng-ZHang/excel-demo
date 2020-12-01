package com.zm.excel.demo.dto;

import com.zm.excel.demo.annotation.ColumnWidth;
import com.zm.excel.demo.annotation.DateTimeFormat;
import com.zm.excel.demo.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ColumnWidth(18)
public class ExcelData {

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Integer id;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名")
    private String name;

    /**
     * 年龄
     */
    @ExcelProperty(value = "年龄")
    private Integer age;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
