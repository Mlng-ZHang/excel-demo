package com.zm.excel.demo.annotation;

import java.lang.annotation.*;

/**
 * 借鉴参考easyexcel
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DateTimeFormat {

    String value() default "";
}
