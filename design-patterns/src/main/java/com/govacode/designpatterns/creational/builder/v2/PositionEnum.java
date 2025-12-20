package com.govacode.designpatterns.creational.builder.v2;

/**
 * 英雄分路枚举类
 *
 * @author gova
 */
public enum PositionEnum {

    TOP("上路"),
    MIDDLE("中路"),
    BOTTOM("下路"),
    JUNGLE("打野");

    private final String title;

    PositionEnum(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
