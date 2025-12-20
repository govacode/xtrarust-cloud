package com.govacode.designpatterns.structural.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 矮人金矿外观类
 *
 * @author fulgens
 */
public class DwarvenGoldmineFacade {

    private final List<DwarvenMineWorker> workers = new ArrayList<>();

    public DwarvenGoldmineFacade() {
        workers.add(new DwarvenGoldDigger());
        workers.add(new DwarvenCartOperator());
        workers.add(new DwarvenTunnelDigger());
    }

    public void startNewDay() {
        makeActions(workers, DwarvenMineWorker.Action.WAKE_UP, DwarvenMineWorker.Action.GO_TO_MINE);
    }

    public void digOutGold() {
        makeActions(workers, DwarvenMineWorker.Action.WORK);
    }

    public void endDay() {
        makeActions(workers, DwarvenMineWorker.Action.GO_HOME, DwarvenMineWorker.Action.GO_TO_SLEEP);
    }

    private static void makeActions(Collection<DwarvenMineWorker> workers,
                                    DwarvenMineWorker.Action... actions) {
        for (DwarvenMineWorker worker : workers) {
            worker.action(actions);
        }
    }
}
