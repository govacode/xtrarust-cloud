package com.xtrarust.cloud.jep.core;

import jep.Interpreter;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class JepTemplate {

    private final GenericObjectPool<Interpreter> pool;

    public JepTemplate(GenericObjectPool<Interpreter> pool) {
        this.pool = pool;
    }

    public <T> T execute(PythonTask<T> task) {
        Interpreter interpreter = null;
        try {
            interpreter = pool.borrowObject();
            return task.run(interpreter);
        } catch (Exception e) {
            throw new RuntimeException("Python execution failed", e);
        } finally {
            if (interpreter != null) {
                try {
                    interpreter.exec("import gc; globals().clear(); gc.collect()");
                    pool.returnObject(interpreter);
                } catch (Exception e) {
                    try {
                        pool.invalidateObject(interpreter);
                    } catch (Exception ex) {
                        // ignore
                    }
                }
            }
        }
    }
}
