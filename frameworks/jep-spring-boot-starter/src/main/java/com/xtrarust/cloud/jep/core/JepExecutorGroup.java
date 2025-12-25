package com.xtrarust.cloud.jep.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface JepExecutorGroup {

    JepExecutor next();

    <T> CompletableFuture<T> submit(PythonTask<T> pythonTask);

    void shutdownGracefully();

    void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);
}
