package com.xtrarust.cloud.web.doc;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * swagger 配置属性
 *
 * @author gova
 */
@Data
@ConfigurationProperties(prefix = "springdoc")
public class SpringDocProperties {

    /**
     * 文档基本信息
     */
    private InfoProperties info = new InfoProperties();

    /**
     * OAuth2信息
     */
    private OAuth2 oAuth2 = new OAuth2();

    /**
     * 文档的基础属性信息
     *
     * @see io.swagger.v3.oas.models.info.Info
     */
    @Data
    public static class InfoProperties {

        /**
         * 标题
         */
        private String title;

        /**
         * 描述
         */
        private String description;

        /**
         * 联系人信息
         */
        @NestedConfigurationProperty
        private Contact contact;

        /**
         * 许可证
         */
        @NestedConfigurationProperty
        private License license;

        /**
         * 版本
         */
        private String version;
    }

    /**
     * OAuth2信息
     */
    @Data
    public static class OAuth2 {

        private String tokenUrl;

        private List<String> scopes;
    }

}
