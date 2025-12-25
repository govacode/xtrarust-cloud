package com.xtrarust.cloud.jep.core;

public interface JepExecutorChooserFactory {

    JepExecutorChooser newChooser(JepExecutor[] executors);

    interface JepExecutorChooser {

        JepExecutor next();
    }
}
