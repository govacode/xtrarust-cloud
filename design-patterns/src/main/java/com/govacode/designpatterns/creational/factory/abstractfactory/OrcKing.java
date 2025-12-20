package com.govacode.designpatterns.creational.factory.abstractfactory;

public class OrcKing implements King {

    public static final String DESCRIPTION = "兽人军队";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
