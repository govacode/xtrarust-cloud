package com.govacode.designpatterns.behavoioral.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 射击屠龙策略
 *
 * @author gova
 */
@Slf4j
public class ProjectileStrategy implements DragonSlayingStrategy {

    @Override
    public void execute() {
        log.info("You shoot the dragon with the magical crossbow and it falls dead on the ground!");
    }
}
