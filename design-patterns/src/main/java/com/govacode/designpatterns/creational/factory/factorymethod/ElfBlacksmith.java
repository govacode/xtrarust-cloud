package com.govacode.designpatterns.creational.factory.factorymethod;

/**
 * 精灵铁匠（精灵制造武器的工厂）
 *
 * @author gova
 */
public class ElfBlacksmith implements Blacksmith {

    @Override
    public Weapon manufactureWeapon(WeaponType weaponType) {
        System.out.println("精灵铁匠开始制造" + weaponType + "类型的武器");
        return new ElfWeapon(weaponType);
    }
}
