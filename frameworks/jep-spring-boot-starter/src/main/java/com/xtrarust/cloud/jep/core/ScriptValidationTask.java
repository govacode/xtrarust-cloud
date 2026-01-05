package com.xtrarust.cloud.jep.core;

import jep.Interpreter;
import org.springframework.util.StringUtils;

import java.util.Map;

import static com.xtrarust.cloud.jep.core.SingleThreadJepExecutor.VALIDATE_FN;

public final class ScriptValidationTask implements PythonTask<ScriptValidationResult> {

    private final String script;

    public ScriptValidationTask(String script) {
        this.script = script;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ScriptValidationResult run(Interpreter interpreter) throws Exception {
        if (!StringUtils.hasText(this.script)) {
            return ScriptValidationResult.pass();
        }
        Map<String, Object> map = (Map<String, Object>) interpreter.invoke(VALIDATE_FN, script);
        if (map == null || map.isEmpty()) {
            return ScriptValidationResult.pass();
        }
        ScriptValidationResult.Error error = new ScriptValidationResult.Error();
        error.setLineno((Number) map.get("lineno"));
        error.setOffset((Number) map.get("offset"));
        error.setMsg((String) map.get("msg"));
        error.setText((String) map.get("text"));
        return ScriptValidationResult.error(error);
    }
}
