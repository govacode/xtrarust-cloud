package com.xtrarust.cloud.jep.core;

import com.xtrarust.cloud.jep.config.JepProperties;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.CompletableFuture;

@CommonsLog
public class JepTemplate implements InitializingBean, DisposableBean {

    private final JepExecutorGroup jepExecutorGroup;

    public JepTemplate(JepProperties properties) {
        this.jepExecutorGroup = new DefaultJepExecutorGroup(properties.getThreads());
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
