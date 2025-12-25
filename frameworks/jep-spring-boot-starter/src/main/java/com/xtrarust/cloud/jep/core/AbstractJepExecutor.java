package com.xtrarust.cloud.jep.core;

import java.util.concurrent.TimeUnit;

public abstract class AbstractJepExecutor implements JepExecutor {

    static final long DEFAULT_SHUTDOWN_QUIET_PERIOD = 2;
    static final long DEFAULT_SHUTDOWN_TIMEOUT = 15;

    private final JepExecutorGroup parent;

    protected AbstractJepExecutor() {
        this(null);
    }

    protected AbstractJepExecutor(JepExecutorGroup parent) {
        this.parent = parent;
    }

    @Override
    public JepExecutorGroup parent() {
        return parent;
    }

    @Override
    public void shutdownGracefully() {
        shutdownGracefully(DEFAULT_SHUTDOWN_QUIET_PERIOD, DEFAULT_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
    }
}
