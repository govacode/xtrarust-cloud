package com.govacode.designpatterns.creational.factory.factorymethod;

/**
 * 精灵武器
 *
 * @author gova
 */
public class ElfWeapon implements Weapon {

    private final WeaponType weaponType;

    public ElfWeapon(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    @Override
    public WeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public String toString() {
        return "ElfWeapon{" +
                "weaponType=" + weaponType +
                '}';
    }
}
