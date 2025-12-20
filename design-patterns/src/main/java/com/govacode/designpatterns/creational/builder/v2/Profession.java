package com.govacode.designpatterns.creational.builder.v2;

/**
 * 英雄职业枚举类
 *
 * @author gova
 */
public enum Profession {

    WARRIOR("战士"),
    MAGE("法师"),
    TANK("坦克"),
    ASSASSIN("刺客"),
    SHOOTER("射手"),
    AUXILIARY("辅助");

    private final String title;

    Profession(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
