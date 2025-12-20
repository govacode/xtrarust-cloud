package com.govacode.designpatterns.structural.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 矮人掘金者
 *
 * @author fulgens
 */
public class DwarvenGoldDigger extends DwarvenMineWorker {

    private static final Logger log = LoggerFactory.getLogger(DwarvenGoldDigger.class);

    @Override
    public void work() {
        log.info("{} digs for gold.", name());
    }

    @Override
    public String name() {
        return "Dwarf gold digger";
    }
}
