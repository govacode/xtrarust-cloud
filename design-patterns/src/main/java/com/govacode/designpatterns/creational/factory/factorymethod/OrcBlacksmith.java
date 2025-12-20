package com.govacode.designpatterns.creational.factory.factorymethod;

/**
 * 兽人铁匠（兽人制造武器的工厂类）
 *
 * @author gova
 */
public class OrcBlacksmith implements Blacksmith {

    @Override
    public Weapon manufactureWeapon(WeaponType weaponType) {
        System.out.println("兽人铁匠开始制造" + weaponType + "类型的武器");
        return new OrcWeapon(weaponType);
    }
}
