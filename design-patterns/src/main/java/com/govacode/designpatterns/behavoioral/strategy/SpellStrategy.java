package com.govacode.designpatterns.behavoioral.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 魔法咒语屠龙策略
 *
 * @author gova
 */
@Slf4j
public class SpellStrategy implements DragonSlayingStrategy {

    @Override
    public void execute() {
        log.info("You cast the spell of disintegration and the dragon vaporizes in a pile of dust!");
    }
}
