package com.govacode.designpatterns.behavoioral.observer;

import lombok.extern.slf4j.Slf4j;

/**
 * 兽人（具体观察者）
 *
 * @author gova
 */
@Slf4j
public class Orcs implements WeatherObserver {

    @Override
    public void onUpdate(WeatherType weather) {
        log.info("The orcs are facing {} weather now", weather.getDescription());
    }
}
