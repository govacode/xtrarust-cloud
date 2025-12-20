package com.govacode.designpatterns.creational.factory.factorymethod;

/**
 * 工厂方法模式符合开闭原则，但当产品过多时容易产生类爆炸
 *
 * @author gova
 */
public class Client {

    public static void main(String[] args) {
        Blacksmith blacksmith = new ElfBlacksmith();
        Weapon weapon = blacksmith.manufactureWeapon(WeaponType.SHORT_SWORD);
        System.out.println(weapon);

        blacksmith = new OrcBlacksmith();
        weapon = blacksmith.manufactureWeapon(WeaponType.AXE);
        System.out.println(weapon);
    }
}
