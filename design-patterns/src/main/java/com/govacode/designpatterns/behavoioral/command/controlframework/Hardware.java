package com.govacode.designpatterns.behavoioral.command.controlframework;

import lombok.extern.slf4j.Slf4j;

/**
 * 硬件
 *
 * @author gova
 */
@Slf4j
public class Hardware {

    /**
     * 开灯
     */
    public void lightOn() {
        log.info("light on");
    }

    /**
     * 关灯
     */
    public void lightOff() {
        log.info("light off");
    }

    /**
     * 响铃
     */
    public void bell() {
        log.info("bing");
    }
}
