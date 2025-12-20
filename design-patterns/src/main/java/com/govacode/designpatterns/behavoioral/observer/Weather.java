package com.govacode.designpatterns.behavoioral.observer;

import java.util.ArrayList;
import java.util.List;

public class Weather implements WeatherSubject {

    private WeatherType currentWeather;

    private final List<WeatherObserver> observers;

    public Weather() {
        currentWeather = WeatherType.SUNNY;
        observers = new ArrayList<>();
    }

    public void weatherChange(WeatherType weather) {
        this.currentWeather = weather;
        notifyObservers();
    }

    @Override
    public void addObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(WeatherObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(observer -> observer.onUpdate(currentWeather));
    }
}
