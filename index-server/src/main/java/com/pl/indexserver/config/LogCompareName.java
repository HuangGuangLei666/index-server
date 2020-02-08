package com.pl.indexserver.config;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface LogCompareName {
    /**
     * 字段名称
     *
     * @return
     */
    String name() default "";

    /**
     * 是否关键词比较
     *
     * @return
     */
    boolean isKeywordCompare() default false;

    /**
     * 分隔符
     *
     * @return
     */
    String separator() default "&";


}
