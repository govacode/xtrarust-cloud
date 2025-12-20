package com.govacode.designpatterns.behavoioral.state;

public class Mammoth {

    private State state;

    public Mammoth() {
        this.state = new PeacefulState(this);
    }

    public void changeStateTo(State state) {
        this.state = state;
        this.state.onEnterState();
    }

    public void timePasses() {
        if (state.getClass().equals(PeacefulState.class)) {
            changeStateTo(new AngryState(this));
        } else {
            changeStateTo(new PeacefulState(this));
        }
    }

    public void observe() {
        this.state.observe();
    }

    @Override
    public String toString() {
        return "Mammoth";
    }

    public static void main(String[] args) {
        Mammoth mammoth = new Mammoth();
        mammoth.observe();

        mammoth.timePasses();
        mammoth.observe();

        mammoth.timePasses();
        mammoth.observe();
    }
}
