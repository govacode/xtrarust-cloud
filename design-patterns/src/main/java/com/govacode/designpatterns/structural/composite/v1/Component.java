package com.govacode.designpatterns.structural.composite.v1;

public abstract class Component {

    private final String name;

    protected Component(String name) {
        this.name = name;
    }

    public void addChild(Component child) {
        throw new UnsupportedOperationException();
    }

    public void removeChild(Component child) {
        throw new UnsupportedOperationException();
    }

    public Component getChild(int index) {
        throw new UnsupportedOperationException();
    }

    public abstract void print(String preStr);

    public String getName() {
        return name;
    }
}
