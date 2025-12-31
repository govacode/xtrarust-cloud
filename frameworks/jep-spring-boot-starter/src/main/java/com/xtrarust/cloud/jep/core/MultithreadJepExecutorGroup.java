package com.xtrarust.cloud.jep.core;

import jep.JepConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.xtrarust.cloud.jep.core.AbstractJepExecutor.DEFAULT_SHUTDOWN_QUIET_PERIOD;
import static com.xtrarust.cloud.jep.core.AbstractJepExecutor.DEFAULT_SHUTDOWN_TIMEOUT;

public abstract class MultithreadJepExecutorGroup implements JepExecutorGroup {

    private final JepExecutor[] children;

    private final JepExecutorChooserFactory.JepExecutorChooser chooser;

    protected MultithreadJepExecutorGroup(boolean useSubInterpreter, JepConfig config, int nThreads) {
        this(useSubInterpreter, config, nThreads, null);
    }

    protected MultithreadJepExecutorGroup(boolean useSubInterpreter, JepConfig config, int nThreads, ThreadFactory threadFactory) {
        this(useSubInterpreter, config, nThreads, threadFactory, DefaultJepExecutorChooserFactory.INSTANCE);
    }

    protected MultithreadJepExecutorGroup(boolean useSubInterpreter, JepConfig config, int nThreads, ThreadFactory threadFactory, JepExecutorChooserFactory chooserFactory) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException("nThreads must be greater than 0");
        }
        if (threadFactory == null) {
            threadFactory = newDefaultThreadFactory();
        }
        this.children = new JepExecutor[nThreads];
        for (int i = 0; i < nThreads; i ++) {
            boolean success = false;
            try {
                children[i] = newChild(useSubInterpreter, config, threadFactory);
                success = true;
            } catch (Exception e) {
                // TODO: Think about if this is a good exception type
                throw new IllegalStateException("failed to create a child jep executor", e);
            } finally {
                if (!success) {
                    for (int j = 0; j < i; j ++) {
                        children[j].shutdownGracefully();
                    }

                    for (int j = 0; j < i; j ++) {
                        JepExecutor e = children[j];
                        try {
                            while (!e.isTerminated()) {
                                e.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException interrupted) {
                            // Let the caller handle the interruption.
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }
        this.chooser = chooserFactory.newChooser(children);
    }

    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(getClass());
    }

    protected abstract JepExecutor newChild(boolean useSubInterpreter, JepConfig config, ThreadFactory threadFactory) throws Exception;

    @Override
    public JepExecutor next() {
        return chooser.next();
    }

    @Override
    public <T> CompletableFuture<T> submit(PythonTask<T> pythonTask) {
        return next().submit(pythonTask);
    }

    @Override
    public void shutdownGracefully() {
        shutdownGracefully(DEFAULT_SHUTDOWN_QUIET_PERIOD, DEFAULT_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        for (JepExecutor l: children) {
            l.shutdownGracefully(quietPeriod, timeout, unit);
        }
    }
}
