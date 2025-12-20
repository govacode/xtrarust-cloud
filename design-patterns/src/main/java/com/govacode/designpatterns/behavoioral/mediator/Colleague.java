package com.govacode.designpatterns.behavoioral.mediator;

/**
 *
 *
 * @author gova
 */
public interface Colleague {

    void receive(String message);

    void send(String message);

    void setMediator(Mediator mediator);
}
