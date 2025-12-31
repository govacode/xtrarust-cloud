package com.xtrarust.cloud.jep.config;

import jep.JepConfig;
import jep.SharedInterpreter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

public class JepConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Binder binder = Binder.get(environment);
        Boolean jepEnabled = binder.bind("jep.enabled", Bindable.of(Boolean.class)).orElse(Boolean.FALSE);
        if (Boolean.FALSE.equals(jepEnabled)) {
            return;
        }
        Boolean useSubInterpreter = binder.bind("jep.use-sub-interpreter", Bindable.of(Boolean.class)).orElse(Boolean.FALSE);
        if (Boolean.TRUE.equals(useSubInterpreter)) {
            return;
        }
        List<String> includePaths = binder.bind("jep.include-paths", Bindable.listOf(String.class)).orElse(Collections.emptyList());
        JepConfig config = new JepConfig();
        if (!CollectionUtils.isEmpty(includePaths)) {
            includePaths.stream().filter(StringUtils::hasText).forEach(config::addIncludePaths);
        }
        SharedInterpreter.setConfig(config);
    }
}
