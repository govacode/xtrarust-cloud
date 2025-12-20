package com.govacode.designpatterns.behavoioral.interpreter;

public class PlusExpression extends AbstractExpression {

    private final AbstractExpression left;

    private final AbstractExpression right;

    public PlusExpression(AbstractExpression left, AbstractExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret(EvalContext ctx) {
        return left.interpret(ctx) + right.interpret(ctx);
    }
}
