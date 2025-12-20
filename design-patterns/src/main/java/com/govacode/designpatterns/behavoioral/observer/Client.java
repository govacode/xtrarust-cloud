package com.govacode.designpatterns.behavoioral.observer;

/**
 * 观察者模式
 *
 * @author gova
 * @see java.util.Observable
 * @see java.util.EventListener
 * @see org.springframework.context.ApplicationListener
 */
public class Client {

    public static void main(String[] args) {
        Weather weather = new Weather();
        weather.addObserver(new Orcs());
        weather.addObserver(new Hobbits());

        weather.notifyObservers();
        weather.weatherChange(WeatherType.WINDY);
    }
}
