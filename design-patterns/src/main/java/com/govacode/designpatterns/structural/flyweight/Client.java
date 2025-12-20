package com.govacode.designpatterns.structural.flyweight;

public class Client {

    public static void main(String[] args) {
        FlyWeightFactory factory = FlyWeightFactory.getInstance();
        FlyWeight flyWeight = factory.getFlyWeight("A");
        flyWeight.operation("extrinsicState");

        FlyWeight flyWeight2 = factory.getFlyWeight("B");
        flyWeight2.operation("extrinsicState");

        FlyWeight flyWeight3 = factory.getFlyWeight("A");
        flyWeight3.operation("extrinsicState");

        System.out.println(flyWeight == flyWeight2);
        System.out.println(flyWeight == flyWeight3);

        System.out.println("-------------Integer常见面试题-------------");
        // Integer.valueOf()方法对于[-128,127]之间的数采用IntegerCache
        Integer i1 = 59;    // Integer.valueOf(59);
        int i2 = 59;
        Integer i3 = Integer.valueOf(59);
        Integer i4 = new Integer(59);
        // 和i2基本数据类型的int比较时Integer类型的i1、i3、i4都会比较其intValue()值
        System.out.println("i1 == i2 ? " + (i1 == i2));   // true
        System.out.println("i1 == i3 ? " + (i1 == i3));   // true
        System.out.println("i3 == i4 ? " + (i3 == i4));   // false
        System.out.println("i2 == i4 ? " + (i2 == i4));   // true
    }
}
