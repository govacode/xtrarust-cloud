package com.govacode.designpatterns.creational.factory.abstractfactory;

public class OrcCastle implements Castle {

    public static final String DESCRIPTION = "兽人城堡";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
