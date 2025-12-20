package com.govacode.designpatterns.creational.factory.abstractfactory;

/**
 * 精灵王国工厂类
 *
 * @author gova
 */
public class ElfKingdomFactory implements KingdomFactory {

    @Override
    public King createKing() {
        return new ElfKing();
    }

    @Override
    public Castle createCastle() {
        return new ElfCastle();
    }

    @Override
    public Army createArmy() {
        return new ElfArmy();
    }
}
