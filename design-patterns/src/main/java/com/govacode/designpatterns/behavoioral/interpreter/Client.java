package com.govacode.designpatterns.behavoioral.interpreter;

import lombok.extern.slf4j.Slf4j;

/**
 * 解释器模式测试
 *
 * @author gova
 */
@Slf4j
public class Client {

    public static void main(String[] args) {
        EvalContext ctx = new EvalContext();

        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        Variable d = new Variable("d");

        ctx.putVariable(a, 1);
        ctx.putVariable(b, 2);
        ctx.putVariable(c, 3);
        ctx.putVariable(d, 4);

        // a + b - c - d
        AbstractExpression exp = new PlusExpression(
                new Variable("a"),
                new MinusExpression(
                        new Variable("b"),
                        new PlusExpression(new Variable("c"), new Variable("d"))
                )
        );
        log.info("exp eval result: {}", exp.interpret(ctx));
    }
}
