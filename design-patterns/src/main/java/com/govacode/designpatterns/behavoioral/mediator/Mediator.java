package com.govacode.designpatterns.behavoioral.mediator;

public interface Mediator {

    void register(Colleague colleague);

    void relay(Colleague from, String message);
}
