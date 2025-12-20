package com.govacode.designpatterns.structural.composite.reference;

import com.google.gson.*;

import java.lang.reflect.Type;

public class NodeDeserializer implements JsonDeserializer<Node> {

    @Override
    public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        String name = jsonObject.get("name").getAsString();

        if ("LeafNode".equals(type)) {
            return new LeafNode(name);
        } else if ("CompositeNode".equals(type)) {
            CompositeNode compositeNode = new CompositeNode(name);
            JsonArray childrenArray = jsonObject.getAsJsonArray("children");
            for (JsonElement childElement : childrenArray) {
                Node childNode = context.deserialize(childElement, Node.class);
                compositeNode.add(childNode);
            }
            return compositeNode;
        }
        throw new JsonParseException("Unknown type: " + type);
    }
}
