package com.xtrarust.cloud.oss.properties;

import com.xtrarust.cloud.oss.enums.AccessPolicyType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS对象存储 配置属性
 *
 * @author gova
 */
@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * 访问端点
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String domain;

    /**
     * 文件路径前缀
     */
    private String pathPrefix;

    /**
     * Access Key
     */
    private String accessKey;

    /**
     * Secret Key
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 存储区域
     */
    private String region;

    /**
     * 是否https
     */
    private Boolean isHttps;

    /**
     * 桶权限类型(0:private 1:public 2:custom)
     *
     * @see AccessPolicyType#getType()
     */
    private String accessPolicy;

}
