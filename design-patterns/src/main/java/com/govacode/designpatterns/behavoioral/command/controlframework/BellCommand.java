package com.govacode.designpatterns.behavoioral.command.controlframework;

/**
 * 响铃命令
 *
 * @author gova
 */
public class BellCommand extends AbstractCommand {

    private final Hardware hardware;

    public BellCommand(long delayTimeInMillisSecond, Hardware hardware) {
        super(delayTimeInMillisSecond);
        this.hardware = hardware;
    }

    @Override
    public void execute() {
        hardware.bell();
        // 重复执行
        controller.addCommand(new BellCommand(delayTime.toMillis(), hardware));
    }
}
