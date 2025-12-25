package com.xtrarust.cloud.jep.core;

import java.util.concurrent.TimeUnit;

public interface JepExecutor extends JepExecutorGroup {

    JepExecutorGroup parent();

    boolean isShutdown();

    boolean isTerminated();

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
}
