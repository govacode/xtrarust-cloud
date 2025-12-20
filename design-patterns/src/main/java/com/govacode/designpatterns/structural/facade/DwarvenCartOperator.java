package com.govacode.designpatterns.structural.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 矮人矿车操作员
 *
 * @author fulgens
 */
public class DwarvenCartOperator extends DwarvenMineWorker {

    private static final Logger log = LoggerFactory.getLogger(DwarvenCartOperator.class);

    @Override
    public void work() {
        log.info("{} moves gold chunks out of the mine.", name());
    }

    @Override
    public String name() {
        return "Dwarf cart operator";
    }
}
