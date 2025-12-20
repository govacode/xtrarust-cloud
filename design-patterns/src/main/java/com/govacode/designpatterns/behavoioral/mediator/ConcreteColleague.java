package com.govacode.designpatterns.behavoioral.mediator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcreteColleague extends AbstractColleague {

    private final String name;

    public ConcreteColleague(String name) {
        this.name = name;
    }

    @Override
    public void receive(String message) {
        log.info("{} receive message: {}", name, message);
    }

    @Override
    public void send(String message) {
        mediator.relay(this, message);
    }
}
