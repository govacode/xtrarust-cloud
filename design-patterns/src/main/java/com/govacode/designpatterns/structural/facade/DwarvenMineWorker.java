package com.govacode.designpatterns.structural.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 矮人矿工抽象类
 *
 * @author fulgens
 */
public abstract class DwarvenMineWorker {

    private static final Logger log = LoggerFactory.getLogger(DwarvenMineWorker.class);

    public void goToSleep() {
        log.info("{} goes to sleep.", name());
    }

    public void wakeUp() {
        log.info("{} wakes up.", name());
    }

    public void goHome() {
        log.info("{} goes home.", name());
    }

    public void goToMine() {
        log.info("{} goes to the mine.", name());
    }

    private void action(Action action) {
        switch (action) {
            case GO_TO_SLEEP:
                goToSleep();
                break;
            case WAKE_UP:
                wakeUp();
                break;
            case GO_HOME:
                goHome();
                break;
            case GO_TO_MINE:
                goToMine();
                break;
            case WORK:
                work();
                break;
            default:
                log.info("Undefined action");
                break;
        }
    }

    /**
     * Perform actions
     */
    public void action(Action... actions) {
        for (Action action : actions) {
            action(action);
        }
    }

    public abstract void work();

    public abstract String name();

    enum Action {
        GO_TO_SLEEP, WAKE_UP, GO_HOME, GO_TO_MINE, WORK
    }
}
