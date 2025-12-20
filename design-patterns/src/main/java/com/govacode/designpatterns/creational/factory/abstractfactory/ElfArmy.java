package com.govacode.designpatterns.creational.factory.abstractfactory;

public class ElfArmy implements Army {

    public static final String DESCRIPTION = "精灵军队";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
