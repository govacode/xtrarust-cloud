package com.govacode.designpatterns.behavoioral.interpreter;

public class MinusExpression extends AbstractExpression {

    private final AbstractExpression left;

    private final AbstractExpression right;

    public MinusExpression(AbstractExpression left, AbstractExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret(EvalContext ctx) {
        return left.interpret(ctx) - right.interpret(ctx);
    }
}
