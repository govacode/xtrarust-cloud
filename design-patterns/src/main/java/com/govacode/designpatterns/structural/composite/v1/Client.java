package com.govacode.designpatterns.structural.composite.v1;

public class Client {

    public static void main(String[] args) {
        Component root = new Composite("家用电器");

        Composite tv = new Composite("电视");
        tv.addChild(new Leaf("4K超清电视"));
        tv.addChild(new Leaf("全面屏电视"));
        root.addChild(tv);

        Composite airConditioner = new Composite("空调");
        airConditioner.addChild(new Leaf("变频空调"));
        airConditioner.addChild(new Leaf("中央空调"));
        root.addChild(airConditioner);

        root.addChild(new Leaf("冰箱"));

        root.print("");
    }
}
