package com.govacode.designpatterns.behavoioral.visitor;

import java.util.List;

/**
 * 军队单位抽象类（抽象元素类）
 *
 * @author gova
 */
public abstract class ArmyUnit {

    private final List<ArmyUnit> units;

    protected ArmyUnit(ArmyUnit... units) {
        this.units = List.of(units);
    }

    public void accept(UnitVisitor visitor) {
        units.forEach(unit -> unit.accept(visitor));
    }
}
