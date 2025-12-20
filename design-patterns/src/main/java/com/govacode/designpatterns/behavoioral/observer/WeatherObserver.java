package com.govacode.designpatterns.behavoioral.observer;

/**
 * 天气观察者接口
 *
 * @author gova
 */
public interface WeatherObserver {

    void onUpdate(WeatherType weather);
}
