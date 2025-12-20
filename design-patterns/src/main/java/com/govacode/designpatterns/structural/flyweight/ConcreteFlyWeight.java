package com.govacode.designpatterns.structural.flyweight;

public class ConcreteFlyWeight implements FlyWeight {

    // 内部状态
    private String intrinsicState;

    // 外部状态
    private String extrinsicState;

    public ConcreteFlyWeight(String intrinsicState) {
        this.intrinsicState = intrinsicState;
    }

    @Override
    public void operation(String extrinsicState) {
        this.extrinsicState = extrinsicState;
    }
}
