package com.xtrarust.cloud.jep.core;

import com.xtrarust.cloud.jep.config.JepProperties;
import jep.JepConfig;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;

@CommonsLog
public class JepTemplate implements InitializingBean, DisposableBean {

    private final JepExecutorGroup jepExecutorGroup;

    public JepTemplate(JepProperties properties) {
        boolean useSubInterpreter = properties.isUseSubInterpreter();
        JepConfig config = null;
        if (useSubInterpreter) {
            config = new JepConfig();
            if (!CollectionUtils.isEmpty(properties.getIncludePaths())) {
                properties.getIncludePaths().stream()
                        .filter(StringUtils::hasText)
                        .forEach(config::addIncludePaths);
            }
            if (!CollectionUtils.isEmpty(properties.getSharedModules())) {
                properties.getSharedModules().stream()
                        .filter(StringUtils::hasText)
                        .forEach(config::addSharedModules);
            }
        }
        this.jepExecutorGroup = new DefaultJepExecutorGroup(useSubInterpreter, config, properties.getThreads());
    }

    public <T> CompletableFuture<T> submit(PythonTask<T> pythonTask) {
        return jepExecutorGroup.submit(pythonTask);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.jepExecutorGroup.submit(new PrintPythonInfoTask());
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void destroy() throws Exception {
        this.jepExecutorGroup.shutdownGracefully();
        log.info("Jep executor group graceful shutdown complete");
    }
}
