package com.govacode.designpatterns.behavoioral.state;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AngryState implements State {

    private final Mammoth mammoth;

    public AngryState(Mammoth mammoth) {
        this.mammoth = mammoth;
    }

    @Override
    public void onEnterState() {
        log.info("{} gets angry!", mammoth);
    }

    @Override
    public void observe() {
        log.info("{} is furious!", mammoth);
    }
}
