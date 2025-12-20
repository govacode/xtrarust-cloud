package com.govacode.designpatterns.structural.composite.reference;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class CompositeNode extends Node {

    @Setter
    @Getter
    protected List<Node> children = new ArrayList<>();

    public CompositeNode(String name) {
        super(name);
    }

    @Override
    public void add(Node node) {
        children.add(node);
        node.parent = this;
    }

    @Override
    public void remove(Node node) {
        children.remove(node);
        node.parent = null;
    }

    @Override
    public void display(int depth) {
        System.out.println(" ".repeat(depth) + name);
        for (Node child : children) {
            child.display(depth + 2);
        }
    }
}
