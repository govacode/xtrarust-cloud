package com.govacode.designpatterns.creational.factory.abstractfactory;

/**
 * 兽人王国工厂类
 *
 * @author fulgens
 */
public class OrcKingdomFactory implements KingdomFactory {
    @Override
    public King createKing() {
        return new OrcKing();
    }

    @Override
    public Castle createCastle() {
        return new OrcCastle();
    }

    @Override
    public Army createArmy() {
        return new OrcArmy();
    }
}
