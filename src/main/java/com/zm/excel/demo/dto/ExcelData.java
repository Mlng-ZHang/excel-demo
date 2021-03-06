package com.zm.excel.demo.dto;

import com.zm.excel.demo.annotation.ColumnWidth;
import com.zm.excel.demo.annotation.DateTimeFormat;
import com.zm.excel.demo.annotation.ExcelHeader;
import lombok.Data;

import java.util.Date;

@Data
@ColumnWidth(18)
public class ExcelData {

    /**
     * ID
     */
    @ExcelHeader(value = "ID")
    private Integer id;

    /**
     * 姓名
     */
    @ExcelHeader(value = "姓名")
    private String name;

    /**
     * 年龄
     */
    @ExcelHeader(value = "年龄")
    private Integer age;

    /**
     * 创建时间
     */
    @ExcelHeader(value = "创建时间")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
