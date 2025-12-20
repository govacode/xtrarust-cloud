package com.govacode.designpatterns.structural.composite.v2;

/**
 * 叶子对象（同v1版本）
 *
 * @author gova
 */
public class Leaf extends Component {

    public Leaf(String name) {
        super(name);
    }

    @Override
    public void print(String preStr) {
        System.out.println(preStr + "-" + getName());
    }
}
