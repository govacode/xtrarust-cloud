package com.govacode.designpatterns.structural.composite.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合对象
 *
 * @author gova
 */
public class Composite extends Component {

    public Composite(String name) {
        super(name);
    }

    private List<Component> children;

    @Override
    public void print(String preStr) {
        System.out.println(preStr + "+" + getName());
        if (children != null && children.size() > 0) {
            preStr += "    ";
            for (Component child : children) {
                child.print(preStr);
            }
        }
    }

    @Override
    public void addChild(Component component) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(component);
        // 添加对父组件的引用
        component.setParent(this);
    }

    @Override
    public void removeChild(Component component) {
        if (children != null) {
            int i = children.indexOf(component);
            if (i != -1) {  // 子节点component存在
                if (component instanceof Composite) {
                    for (Component child : component.getChildren()) {
                        child.setParent(this);
                        this.addChild(child);
                    }
                }
                // 删除子节点
                children.remove(component);
            }
        }
    }
    
    @Override
    public Component getChild(int index) {
        Component child = null;
        if (children != null && (index >= 0 && index < children.size())) {
            child = children.get(index);
        }
        return child;
    }

    @Override
    public List<Component> getChildren() {
        return children;
    }
}
