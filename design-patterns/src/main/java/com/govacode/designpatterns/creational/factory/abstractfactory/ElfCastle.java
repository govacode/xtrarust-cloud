package com.govacode.designpatterns.creational.factory.abstractfactory;

public class ElfCastle implements Castle {

    public static final String DESCRIPTION = "精灵城堡";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
