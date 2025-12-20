package com.govacode.designpatterns.behavoioral.interpreter;

import java.util.HashMap;
import java.util.Map;

public class EvalContext {

    private final Map<Variable, Integer> ctx = new HashMap<>();

    public void putVariable(Variable var, int val) {
        ctx.put(var, val);
    }

    public int getVal(Variable var) {
        Integer val = ctx.get(var);
        if (val == null) {
            throw new RuntimeException("variable is not exist");
        }
        return val;
    }
}
