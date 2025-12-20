package com.govacode.designpatterns.behavoioral.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 近战屠龙策略
 *
 * @author gova
 */
@Slf4j
public class MeleeStrategy implements DragonSlayingStrategy {

    @Override
    public void execute() {
        log.info("With your Excalibur you sever the dragon's head!");
    }
}
