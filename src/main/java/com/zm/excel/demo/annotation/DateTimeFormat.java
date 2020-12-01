package com.zm.excel.demo.annotation;

import java.lang.annotation.*;

/**
 * Convert date format.
 *
 * <p>
 * write: It can be used on classes {@link java.util.Date}
 * <p>
 * read: It can be used on classes {@link String}
 *
 * @author Jiaju Zhuang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DateTimeFormat {

    /**
     *
     * Specific format reference {@link java.text.SimpleDateFormat}
     *
     * @return Format pattern
     */
    String value() default "";
}
