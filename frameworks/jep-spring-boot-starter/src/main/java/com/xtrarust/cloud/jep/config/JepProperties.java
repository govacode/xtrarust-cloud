package com.xtrarust.cloud.jep.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "jep")
public class JepProperties {

    /**
     * 是否开启 JEP 自动配置
     */
    private boolean enabled = false;

    /**
     * Python 模块搜索路径 (sys.path)
     */
    private List<String> includePaths = new ArrayList<>();

    /**
     * 执行线程数
     */
    private int threads = 1;
}
