package com.govacode.designpatterns.creational.factory.abstractfactory;

public class ElfKing implements King {

    public static final String DESCRIPTION = "精灵王";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
