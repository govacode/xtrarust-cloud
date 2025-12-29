package com.xtrarust.cloud.jep.core;

import jep.Interpreter;
import org.springframework.util.Assert;

import java.util.Map;

public final class ScriptValidationTask implements PythonTask<ScriptValidationResult> {

    private static final String VALIDATION_SCRIPT = """
            def validate_script(code):
                try:
                    compile(code, '<string>', 'exec')
                    return None
                except SyntaxError as e:
                    return {
                        "lineno": e.lineno,
                        "offset": e.offset,
                        "msg": e.msg,
                        "text": e.text.strip() if e.text else ""
                    }
            """;

    private final String script;

    public ScriptValidationTask(String script) {
        Assert.notNull(script, "script must not be null");
        this.script = script;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ScriptValidationResult run(Interpreter interpreter) throws Exception {
        interpreter.exec(VALIDATION_SCRIPT);
        Map<String, Object> map = (Map<String, Object>) interpreter.invoke("validate_script", script);
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
