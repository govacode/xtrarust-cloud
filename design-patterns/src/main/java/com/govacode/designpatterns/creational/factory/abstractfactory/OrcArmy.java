package com.govacode.designpatterns.creational.factory.abstractfactory;

public class OrcArmy implements Army {

    public static final String DESCRIPTION = "兽人军队";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}