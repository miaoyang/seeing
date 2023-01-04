package com.ym.seeing.api.annotation;

import java.lang.annotation.*;

/**
 * @author yangmiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    /**
     * 方法名称
     * @return
     */
    String value() default "";

    /**
     * 更多的信息
     * @return
     */
    String message() default "";
}
