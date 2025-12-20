package com.govacode.designpatterns.behavoioral.visitor;

/**
 * 士官（具体元素类）
 *
 * @author gova
 */
public class Sergeant extends ArmyUnit {

    public Sergeant(ArmyUnit... units) {
        super(units);
    }

    @Override
    public void accept(UnitVisitor visitor) {
        visitor.visitSergeant(this);
        super.accept(visitor);
    }

    @Override
    public String toString() {
        return "sergeant";
    }
}
