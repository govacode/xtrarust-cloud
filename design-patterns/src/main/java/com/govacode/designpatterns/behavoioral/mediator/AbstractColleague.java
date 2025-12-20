package com.govacode.designpatterns.behavoioral.mediator;

public abstract class AbstractColleague implements Colleague {

    protected Mediator mediator;

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
