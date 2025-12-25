package com.xtrarust.cloud.jep.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultJepExecutorChooserFactory implements JepExecutorChooserFactory {

    public static final DefaultJepExecutorChooserFactory INSTANCE = new DefaultJepExecutorChooserFactory();

    private DefaultJepExecutorChooserFactory() {
    }

    @Override
    public JepExecutorChooser newChooser(JepExecutor[] executors) {
        if (isPowerOfTwo(executors.length)) {
            return new PowerOfTwoJepExecutorChooser(executors);
        } else {
            return new GenericJepExecutorChooser(executors);
        }
    }

    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    private static final class PowerOfTwoJepExecutorChooser implements JepExecutorChooser {
        private final AtomicInteger idx = new AtomicInteger();
        private final JepExecutor[] executors;

        PowerOfTwoJepExecutorChooser(JepExecutor[] executors) {
            this.executors = executors;
        }

        @Override
        public JepExecutor next() {
            return executors[idx.getAndIncrement() & executors.length - 1];
        }
    }

    private static final class GenericJepExecutorChooser implements JepExecutorChooser {
        // Use a 'long' counter to avoid non-round-robin behaviour at the 32-bit overflow boundary.
        // The 64-bit long solves this by placing the overflow so far into the future, that no system
        // will encounter this in practice.
        private final AtomicLong idx = new AtomicLong();
        private final JepExecutor[] executors;

        GenericJepExecutorChooser(JepExecutor[] executors) {
            this.executors = executors;
        }

        @Override
        public JepExecutor next() {
            return executors[(int) Math.abs(idx.getAndIncrement() % executors.length)];
        }
    }
}
