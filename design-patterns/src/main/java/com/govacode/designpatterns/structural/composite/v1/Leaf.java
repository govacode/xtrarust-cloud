package com.govacode.designpatterns.structural.composite.v1;

public class Leaf extends Component {

    public Leaf(String name) {
        super(name);
    }

    @Override
    public void print(String preStr) {
        System.out.println(preStr + "-" + getName());
    }
}
