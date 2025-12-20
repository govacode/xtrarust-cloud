package com.govacode.designpatterns.creational.builder.v2;

/**
 * 皮肤类
 *
 * @author gova
 */
public class Skin {

    private String name;

    private String skinImage;

    public Skin(String name, String skinImage) {
        this.name = name;
        this.skinImage = skinImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkinImage() {
        return skinImage;
    }

    public void setSkinImage(String skinImage) {
        this.skinImage = skinImage;
    }

    @Override
    public String toString() {
        return "Skin{" +
                "name='" + name + '\'' +
                ", skinImage='" + skinImage + '\'' +
                '}';
    }
}
