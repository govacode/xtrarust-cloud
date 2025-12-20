package com.govacode.designpatterns.behavoioral.command.controlframework;

/**
 * 开灯命令
 *
 * @author gova
 */
public class LightOnCommand extends AbstractCommand {

    private final Hardware hardware;

    public LightOnCommand(long delayTimeInMillisSecond, Hardware hardware) {
        super(delayTimeInMillisSecond);
        this.hardware = hardware;
    }

    @Override
    public void execute() {
        hardware.lightOn();
    }
}
