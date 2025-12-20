package com.govacode.designpatterns.structural.composite.reference;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@NoArgsConstructor
public abstract class Node {

    @Getter
    @Setter
    protected String name;

    @Getter
    @Setter
    @JSONField(serialize = false)
    protected Node parent; // 记录父节点

    public Node(String name) {
        this.name = name;
    }

    public abstract void add(Node node);
    public abstract void remove(Node node);
    public abstract void display(int depth);

    // 修改为从底层遍历
    public List<String> traverseLayerByLayer() {
        List<String> result = new ArrayList<>();
        Queue<Node> queue = new ArrayDeque<>();
        queue.offer(this);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current instanceof CompositeNode) {
                CompositeNode compositeNode = (CompositeNode) current;
                for (Node child : compositeNode.children) {
                    queue.offer(child); // 先将子节点加入队列
                }
            }
            result.add(current.name); // 然后添加当前节点到结果列表
        }
        // root c1 c2 c3 l3 l4 l1 l2
        return result;
    }
}
