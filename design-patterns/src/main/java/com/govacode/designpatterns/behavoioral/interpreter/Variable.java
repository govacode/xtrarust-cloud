package com.govacode.designpatterns.behavoioral.interpreter;

import java.util.Objects;

public class Variable extends AbstractExpression {

    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int interpret(EvalContext ctx) {
        return ctx.getVal(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
