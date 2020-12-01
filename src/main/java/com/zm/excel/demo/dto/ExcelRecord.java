package com.zm.excel.demo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * ExcelRecord
 * @Author: zhangming
 * @Date 2020/11/12 17:24
 * @Description:
 */
@Data
public class ExcelRecord<E> implements Serializable {
    private static final long serialVersionUID = -7303036218518657604L;
    /**
     * 最小id
     */
    private E minId;
    /**
     * 最大id
     */
    private E maxId;
}
