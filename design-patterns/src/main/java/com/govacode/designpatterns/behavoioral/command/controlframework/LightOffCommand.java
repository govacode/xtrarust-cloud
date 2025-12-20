package com.govacode.designpatterns.behavoioral.command.controlframework;

/**
 * 关灯命令
 *
 * @author gova
 */
public class LightOffCommand extends AbstractCommand {

    private final Hardware hardware;

    public LightOffCommand(long delayTimeInMillisSecond, Hardware hardware) {
        super(delayTimeInMillisSecond);
        this.hardware = hardware;
    }

    @Override
    public void execute() {
        hardware.lightOff();
    }
}
