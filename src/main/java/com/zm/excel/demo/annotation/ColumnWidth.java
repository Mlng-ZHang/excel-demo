package com.zm.excel.demo.annotation;

import java.lang.annotation.*;

/**
 * 借鉴参考easyexcel
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ColumnWidth {
    int value() default -1;
}
