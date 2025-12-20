package com.govacode.designpatterns.creational.builder.v2;

import lombok.Getter;

import java.util.Set;

/**
 * MOBA类游戏Hero（主类是产品 静态内部类是工厂）
 *
 * @author gova
 */
@Getter
public class Hero {

    // 名称
    private final String name;

    // 职业
    private final Set<Profession> professions;

    // 分路
    private final Position position;

    // 皮肤
    private final Set<Skin> skins;

    // 构造器私有 仅能通过Builder创建
    private Hero(Builder builder) {
        this.name = builder.name;
        this.professions = builder.professions;
        this.position = builder.position;
        this.skins = builder.skins;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Hero{" +
                "name='" + name + '\'' +
                ", professions=" + professions +
                ", position=" + position +
                ", skins=" + skins +
                '}';
    }

    public static class Builder implements IBuilder<Hero> {

        private String name;

        private Set<Profession> professions;

        private Position position;

        private Set<Skin> skins;

        public Builder() {

        }

        public Builder name(String name) {
            this.name = name;
            return this; // 通过 return this 实现链式调用
        }

        public Builder professions(Set<Profession> professions) {
            this.professions = professions;
            return this;
        }

        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        public Builder skins(Set<Skin> skins) {
            this.skins = skins;
            return this;
        }

        public Hero build() {
            return new Hero(this);
        }
    }
}