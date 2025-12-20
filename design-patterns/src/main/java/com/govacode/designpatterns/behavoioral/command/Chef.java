package com.govacode.designpatterns.behavoioral.command;

import lombok.extern.slf4j.Slf4j;

/**
 * 厨师类
 *
 * @author gova
 */
@Slf4j
public class Chef {

    public void makeFood(String name, int num) {
        log.info("{}份{}", num, name);
    }
}
