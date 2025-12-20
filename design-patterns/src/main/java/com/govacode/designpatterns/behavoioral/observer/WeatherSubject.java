package com.govacode.designpatterns.behavoioral.observer;

/**
 * 天气主题接口
 *
 * @author gova
 */
public interface WeatherSubject {

    /**
     * 添加观察者
     *
     * @param observer the observer
     */
    void addObserver(WeatherObserver observer);

    /**
     * 删除观察者
     * @param observer the observer
     */
    void removeObserver(WeatherObserver observer);

    /**
     * 通知观察者
     */
    void notifyObservers();
}
