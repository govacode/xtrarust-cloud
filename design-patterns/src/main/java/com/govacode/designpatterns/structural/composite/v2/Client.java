package com.govacode.designpatterns.structural.composite.v2;

public class Client {

    public static void main(String[] args) {
        Component root = new Composite("手机");
        Component c1 = new Composite("小米手机");
        Component c2 = new Composite("红米Redmi");

        Component leaf1 = new Leaf("小米mix3");
        Component leaf2 = new Leaf("小米8");
        Component leaf3 = new Leaf("小米Play");
        Component leaf4 = new Leaf("红米Note7");
        Component leaf5 = new Leaf("红米6 Pro");

        root.addChild(c1);
        root.addChild(c2);
        c1.addChild(leaf1);
        c1.addChild(leaf2);
        c1.addChild(leaf3);
        c2.addChild(leaf4);
        c2.addChild(leaf5);

        System.out.println("root isRoot ? " + root.isRoot());
        System.out.println("c1 isRoot ? " + c1.isRoot());
        root.print("");
        root.removeChild(c1);
        root.print("");
    }
}
