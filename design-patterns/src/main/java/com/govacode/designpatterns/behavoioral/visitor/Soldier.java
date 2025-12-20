package com.govacode.designpatterns.behavoioral.visitor;

/**
 * 士兵（具体元素类）
 *
 * @author gova
 */
public class Soldier extends ArmyUnit {

    public Soldier(ArmyUnit... units) {
        super(units);
    }

    @Override
    public void accept(UnitVisitor visitor) {
        visitor.visitSoldier(this);
        super.accept(visitor);
    }

    @Override
    public String toString() {
        return "soldier";
    }
}
