package com.govacode.designpatterns.structural.composite.reference;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LeafNode extends Node {

    public LeafNode(String name) {
        super(name);
    }

    @Override
    public void add(Node node) {
        throw new UnsupportedOperationException("Cannot add to a leaf node");
    }

    @Override
    public void remove(Node node) {
        throw new UnsupportedOperationException("Cannot remove from a leaf node");
    }

    @Override
    public void display(int depth) {
        System.out.println(" ".repeat(depth) + name);
    }

}
