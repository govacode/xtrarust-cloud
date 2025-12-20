package com.govacode.designpatterns.creational.factory.factorymethod;

/**
 * 武器类型枚举
 *
 * @author gova
 */
public enum WeaponType {

    SHORT_SWORD("短剑"),
    SPEAR("长矛"),
    AXE("斧子"),
    UNDEFINED("");

    private final String title;

    WeaponType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
