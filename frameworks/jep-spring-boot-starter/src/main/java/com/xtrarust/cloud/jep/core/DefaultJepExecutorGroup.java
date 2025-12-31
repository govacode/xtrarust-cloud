package com.xtrarust.cloud.jep.core;

import java.util.concurrent.ThreadFactory;

public class DefaultJepExecutorGroup extends MultithreadJepExecutorGroup {

    public DefaultJepExecutorGroup(boolean useSubInterpreter, int nThreads) {
        super(useSubInterpreter, nThreads);
    }

    public DefaultJepExecutorGroup(boolean useSubInterpreter, int nThreads, ThreadFactory threadFactory) {
        super(useSubInterpreter, nThreads, threadFactory);
    }

    @Override
    protected JepExecutor newChild(boolean useSubInterpreter, ThreadFactory threadFactory) throws Exception {
        return new SingleThreadJepExecutor(this, useSubInterpreter, threadFactory);
    }
}
