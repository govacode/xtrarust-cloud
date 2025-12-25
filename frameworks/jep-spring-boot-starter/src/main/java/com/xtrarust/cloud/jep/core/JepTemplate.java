package com.xtrarust.cloud.jep.core;

import com.xtrarust.cloud.jep.config.JepProperties;
import jep.JepConfig;
import jep.SharedInterpreter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.CompletableFuture;

public class JepTemplate implements EnvironmentPostProcessor {

    private final JepProperties properties;

    private final JepExecutorGroup jepExecutorGroup;

    public JepTemplate(JepProperties properties) {
        this.properties = properties;
        this.jepExecutorGroup = new DefaultJepExecutorGroup(properties.getThreads());
    }

    public <T> CompletableFuture<T> submit(PythonTask<T> pythonTask) {
        return jepExecutorGroup.submit(pythonTask);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        JepConfig config = new JepConfig();
        if (properties.getIncludePaths() != null) {
            properties.getIncludePaths().forEach(config::addIncludePaths);
        }
        SharedInterpreter.setConfig(config);
    }
}
