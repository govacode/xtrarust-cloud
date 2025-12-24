package com.xtrarust.cloud.jep.core;

import jep.Interpreter;

@FunctionalInterface
public interface PythonTask<T> {

    T run(Interpreter interpreter) throws Exception;
}
