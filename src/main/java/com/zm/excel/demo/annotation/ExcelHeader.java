package com.zm.excel.demo.annotation;

import java.lang.annotation.*;

/**
 * 借鉴参考easyexcel
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelHeader {
    String[] value() default {""};
}
