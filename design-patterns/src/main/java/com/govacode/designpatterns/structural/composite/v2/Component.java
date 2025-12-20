package com.govacode.designpatterns.structural.composite.v2;

import java.util.List;

/**
 * 抽象组件
 *
 * @author gova
 */
public abstract class Component {

    private final String name;

    // 父组件引用
    protected Component parent;

    protected Component(String name) {
        this.name = name;
    }

    protected Component getParent() {
        return this.parent;
    }

    protected void setParent(Component parent) {
        this.parent = parent;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    // ---------------------------以下和v1版本一致-----------------------------------
    public abstract void print(String preStr);

    public void addChild(Component component) {
        throw new UnsupportedOperationException();
    }

    public void removeChild(Component component) {
        throw new UnsupportedOperationException();
    }

    public Component getChild(int index) {
        throw new UnsupportedOperationException();
    }

    public List<Component> getChildren() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return name;
    }
}
