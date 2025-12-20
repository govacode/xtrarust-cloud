package com.govacode.designpatterns.behavoioral.visitor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommanderVisitor implements UnitVisitor {

    @Override
    public void visitSoldier(Soldier soldier) {
        // Do nothing
    }

    @Override
    public void visitSergeant(Sergeant sergeant) {
        // Do nothing
    }

    @Override
    public void visitCommander(Commander commander) {
        log.info("Good to see you {}", commander);
    }
}
