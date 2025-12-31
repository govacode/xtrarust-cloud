package com.xtrarust.cloud.jep.core;

import jep.JepConfig;

import java.util.concurrent.ThreadFactory;

public class DefaultJepExecutorGroup extends MultithreadJepExecutorGroup {

    public DefaultJepExecutorGroup(boolean useSubInterpreter, JepConfig config, int nThreads) {
        super(useSubInterpreter, config, nThreads);
    }

    public DefaultJepExecutorGroup(boolean useSubInterpreter, JepConfig config, int nThreads, ThreadFactory threadFactory) {
        super(useSubInterpreter, config, nThreads, threadFactory);
    }

    @Override
    protected JepExecutor newChild(boolean useSubInterpreter, JepConfig config, ThreadFactory threadFactory) throws Exception {
        return new SingleThreadJepExecutor(this, useSubInterpreter, config, threadFactory);
    }
}
