package com.xtrarust.cloud.jep.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = "jep")
public class JepProperties {

    /**
     * 是否开启 JEP 自动配置
     */
    private boolean enabled = false;

    /**
     * 是否使用 SubInterpreter
     */
    private boolean useSubInterpreter = false;

    /**
     * 执行线程数
     */
    private int threads = 1;

    /**
     * Python 模块搜索路径 (sys.path)
     */
    private List<String> includePaths = new ArrayList<>();

    /**
     * SubInterpreter 共享模块（如numpy）
     */
    private Set<String> sharedModules = new LinkedHashSet<>();
}
