package com.govacode.designpatterns.creational.factory.factorymethod;

/**
 * 铁匠接口（武器抽象工厂）
 *
 * @author gova
 */
public interface Blacksmith {

    /**
     * 根据武器类型制造武器
     *
     * @param weaponType 武器类型
     * @return 武器
     */
    Weapon manufactureWeapon(WeaponType weaponType);

}
