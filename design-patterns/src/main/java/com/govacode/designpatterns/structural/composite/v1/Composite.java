package com.govacode.designpatterns.structural.composite.v1;

import java.util.ArrayList;
import java.util.List;

public class Composite extends Component {

    private final List<Component> children = new ArrayList<>();

    public Composite(String name) {
        super(name);
    }

    @Override
    public void addChild(Component child) {
        children.add(child);
    }

    @Override
    public void removeChild(Component child) {
        children.remove(child);
    }

    @Override
    public Component getChild(int index) {
        Component component = null;
        if (index >= 0 && index < children.size()) {
            component = children.get(index);
        }
        return component;
    }

    @Override
    public void print(String preStr) {
        preStr = preStr == null ? "" : preStr;
        System.out.println(preStr + "+" + getName());
        if (!children.isEmpty()) {
            preStr += "    ";
            for (Component child : children) {
                child.print(preStr);
            }
        }
    }
}
