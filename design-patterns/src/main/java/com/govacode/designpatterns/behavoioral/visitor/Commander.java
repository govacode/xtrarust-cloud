package com.govacode.designpatterns.behavoioral.visitor;

/**
 * 指挥官（具体元素类）
 *
 * @author gova
 */
public class Commander extends ArmyUnit {

    public Commander(ArmyUnit... units) {
        super(units);
    }

    @Override
    public void accept(UnitVisitor visitor) {
        visitor.visitCommander(this);
        super.accept(visitor);
    }

    @Override
    public String toString() {
        return "commander";
    }
}
