package com.govacode.designpatterns.behavoioral.command.controlframework;

public class TerminateCommand extends AbstractCommand {

    public TerminateCommand(long delayTimeInMillisSecond) {
        super(delayTimeInMillisSecond);
    }

    @Override
    public void execute() {
        System.exit(0);
    }
}
