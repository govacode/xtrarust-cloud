package com.govacode.designpatterns.behavoioral.observer;

import lombok.extern.slf4j.Slf4j;

/**
 * 霍比特人（具体观察者）
 *
 * @author gova
 */
@Slf4j
public class Hobbits implements WeatherObserver {

    @Override
    public void onUpdate(WeatherType weather) {
        log.info("The hobbits are facing {} weather now", weather.getDescription());
    }
}
