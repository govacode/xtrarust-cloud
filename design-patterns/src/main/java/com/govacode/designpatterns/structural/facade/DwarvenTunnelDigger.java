package com.govacode.designpatterns.structural.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 矮人矿道挖掘工
 *
 * @author fulgens
 */
public class DwarvenTunnelDigger extends DwarvenMineWorker {

    private static final Logger log = LoggerFactory.getLogger(DwarvenTunnelDigger.class);

    @Override
    public void work() {
        log.info("{} creates another promising tunnel.", name());
    }

    @Override
    public String name() {
        return "Dwarven tunnel digger";
    }

}
