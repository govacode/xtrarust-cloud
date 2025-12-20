package com.govacode.designpatterns.creational.factory.factorymethod;

/**
 * 武器接口
 *
 * @author fulgens
 */
public interface Weapon {

    /**
     * 获取武器类型
     *
     * @return
     */
    WeaponType getWeaponType();
}
