package com.govacode.designpatterns.behavoioral.mediator;

import java.util.ArrayList;
import java.util.List;

public class ConcreteMediator implements Mediator {

    private final List<Colleague> colleagues;

    public ConcreteMediator() {
        this.colleagues = new ArrayList<>();
    }

    @Override
    public void register(Colleague colleague) {
        if (!colleagues.contains(colleague)) {
            colleagues.add(colleague);
            colleague.setMediator(this);
        }
    }

    @Override
    public void relay(Colleague from, String message) {
        for (Colleague colleague : colleagues) {
            if (!colleague.equals(from)) {
                colleague.receive(message);
            }
        }
    }
}
