package com.xtrarust.cloud.jep.core;

public class PythonTaskFailedException extends RuntimeException {

    public PythonTaskFailedException(String message) {
        super(message);
    }

    public PythonTaskFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
