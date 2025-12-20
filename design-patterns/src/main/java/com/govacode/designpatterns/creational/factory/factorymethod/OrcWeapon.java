package com.govacode.designpatterns.creational.factory.factorymethod;

/**
 * 兽人武器
 *
 * @author gova
 */
public class OrcWeapon implements Weapon {

    private final WeaponType weaponType;

    public OrcWeapon(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    @Override
    public WeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public String toString() {
        return "OrcWeapon{" +
                "weaponType=" + weaponType +
                '}';
    }
}
