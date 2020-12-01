package com.zm.excel.demo.annotation;

import java.lang.annotation.*;

/**
 * Set the width of the table
 *
 * @author Jiaju Zhuang
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ColumnWidth {

    /**
     * Column width
     * <p>
     * -1 means the default column width is used
     *
     * @return Column width
     */
    int value() default -1;
}
