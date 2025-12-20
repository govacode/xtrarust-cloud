package com.govacode.designpatterns.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

public class FlyWeightFactory {

    private final Map<String, FlyWeight> flyWeightMap = new HashMap<>();

    private static volatile FlyWeightFactory INSTANCE = null;

    private FlyWeightFactory() {
    }

    public static FlyWeightFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (FlyWeightFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlyWeightFactory();
                }
            }
        }
        return INSTANCE;
    }

    public FlyWeight getFlyWeight(String intrinsicState) {
        synchronized (this) {
            FlyWeight flyWeight = flyWeightMap.get(intrinsicState);
            if (flyWeight == null) {
                flyWeight = new ConcreteFlyWeight(intrinsicState);
                flyWeightMap.put(intrinsicState, flyWeight);
            }
            return flyWeight;
        }
    }

}
