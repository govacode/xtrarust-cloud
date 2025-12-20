package com.govacode.designpatterns.structural.composite.reference;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CompositePatternDemo {

    public static void main(String[] args) throws JsonProcessingException {

        CompositeNode root = new CompositeNode("Root");
        CompositeNode child1 = new CompositeNode("Child 1");
        CompositeNode child2 = new CompositeNode("Child 2");
        CompositeNode child3 = new CompositeNode("Child 3");

        CompositeNode child4 = new CompositeNode("Child 4");

        LeafNode leaf1 = new LeafNode("Leaf 1");
        LeafNode leaf2 = new LeafNode("Leaf 2");
        LeafNode leaf3 = new LeafNode("Leaf 3");
        LeafNode leaf4 = new LeafNode("Leaf 4");

        root.add(child1);
        root.add(child2);
        child1.add(child3);
        child3.add(leaf1);
        child3.add(leaf2);
        child3.add(leaf4);

        child2.add(leaf3);

        child2.add(child4);
        child4.add(leaf4);

        // 显示树结构
        root.display(0);

        // 从底层向上遍历
        System.out.println("\n从底层向上遍历:");
        List<String> result = root.traverseLayerByLayer();
        for (int i = result.size() - 1; i >= 0; i--) {
            System.out.println(result.get(i));
        }

        System.out.println("-------------");

// JSON 序列化
        String jsonOutput = JSON.toJSONString(root, SerializerFeature.DisableCircularReferenceDetect);
        System.out.println("JSON 序列化结果:");
        System.out.println(jsonOutput);

        // JSON 反序列化
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonOutput);
        CompositeNode root2 = constructComposite(jsonNode);
        root2.display(0);

        System.out.println("\n从底层向上遍历:");
        List<String> result2 = root2.traverseLayerByLayer();
        for (int i = result2.size() - 1; i >= 0; i--) {
            System.out.println(result2.get(i));
        }
    }

    private static CompositeNode constructComposite(JsonNode node) {
        String name = node.get("name").asText();
        CompositeNode composite = new CompositeNode(name);

        if (node.has("children")) {
            Iterator<JsonNode> elements = node.get("children").elements();
            while (elements.hasNext()) {
                JsonNode childNode = elements.next();
                if (childNode.has("children")) {
                    composite.add(constructComposite(childNode)); // 递归构造子组合
                } else {
                    composite.add(new LeafNode(childNode.get("name").asText())); // 添加叶子节点
                }
            }
        }

        return composite;
    }
}
