package com.xtrarust.cloud.jep.core;

import java.util.concurrent.ThreadFactory;

public class DefaultJepExecutorGroup extends MultithreadJepExecutorGroup {

    public DefaultJepExecutorGroup(int nThreads) {
        super(nThreads);
    }

    public DefaultJepExecutorGroup(int nThreads, ThreadFactory threadFactory) {
        super(nThreads, threadFactory);
    }

    @Override
    protected JepExecutor newChild(ThreadFactory threadFactory) throws Exception {
        return new SingleThreadJepExecutor(this, threadFactory);
    }
}
