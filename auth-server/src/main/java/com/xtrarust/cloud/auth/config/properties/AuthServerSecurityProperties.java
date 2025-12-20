package com.xtrarust.cloud.auth.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "spring.security")
public class AuthServerSecurityProperties {

    private List<String> ignoreUriList = Collections.emptyList();

    private Captcha captcha = new Captcha();

    @Data
    public static class Captcha {

        /**
         * 认证时是否启用图形验证码
         */
        private Boolean enable = true;

        /**
         * 验证码的过期时间
         */
        @NotNull(message = "验证码的过期时间不为空")
        private Duration timeout;

        /**
         * 验证码的高度
         */
        @NotNull(message = "验证码的高度不能为空")
        private Integer height;

        /**
         * 验证码的宽度
         */
        @NotNull(message = "验证码的宽度不能为空")
        private Integer width;
    }
}
