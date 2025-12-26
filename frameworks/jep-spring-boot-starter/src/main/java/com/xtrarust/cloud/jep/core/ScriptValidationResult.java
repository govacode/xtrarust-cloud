package com.xtrarust.cloud.jep.core;

import lombok.Data;

@Data
public class ScriptValidationResult {

    private boolean pass;

    private Error error;

    public static ScriptValidationResult pass() {
        ScriptValidationResult result = new ScriptValidationResult();
        result.setPass(true);
        return result;
    }

    public static ScriptValidationResult error(Error error) {
        ScriptValidationResult result = new ScriptValidationResult();
        result.setPass(false);
        result.setError(error);
        return result;
    }

    @Data
    public static class Error {

        private Number lineno;

        private Number offset;

        private String msg;

        private String text;
    }
}
