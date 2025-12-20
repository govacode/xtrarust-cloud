package com.xtrarust.cloud.web.desensitize.regex.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.xtrarust.cloud.web.desensitize.base.annotation.DesensitizeBy;
import com.xtrarust.cloud.web.desensitize.regex.handler.EmailDesensitizationHandler;

import java.lang.annotation.*;

/**
 * 邮箱脱敏注解
 *
 * @author gaibu
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = EmailDesensitizationHandler.class)
public @interface EmailDesensitize {

    /**
     * 匹配的正则表达式
     */
    String regex() default "(^.)[^@]*(@.*$)";

    /**
     * 替换规则，邮箱;
     *
     * 比如：example@gmail.com 脱敏之后为 e****@gmail.com
     */
    String replacer() default "$1****$2";
}
