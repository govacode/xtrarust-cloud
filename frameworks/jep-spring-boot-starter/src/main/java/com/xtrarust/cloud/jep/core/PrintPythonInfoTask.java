package com.xtrarust.cloud.jep.core;

import jep.Interpreter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@CommonsLog
public final class PrintPythonInfoTask implements PythonTask<Void> {

    @Override
    public Void run(Interpreter interpreter) throws Exception {
        String version = (String) interpreter.getValue("sys.version");
        if (StringUtils.hasText(version)) {
            log.info("Python version: " + version);
        }
        String pythonHome = (String) interpreter.getValue("sys.prefix");
        if (StringUtils.hasText(pythonHome)) {
            log.info("Python home: " + pythonHome);
        }

        @SuppressWarnings("unchecked")
        List<String> paths = (List<String>) interpreter.getValue("sys.path");
        if (!CollectionUtils.isEmpty(paths)) {
            log.info("Python search paths: " + Arrays.toString(paths.toArray()));
        }
        return null;
    }
}
