package com.govacode.designpatterns.behavoioral.command.controlframework;

import java.util.List;

/**
 * 参考自 On Java Chapter11
 *
 * @author gova
 */
public class Client {

    public static void main(String[] args) {
        Controller controller = new Controller();
        Hardware hardware = new Hardware();
        // 每秒响铃1次
        controller.addCommand(new BellCommand(1_000, hardware));
        // 10ms后开灯
        controller.addCommand(new LightOnCommand(10, hardware));
        // 5s后关灯
        controller.addCommand(new LightOffCommand(5_000, hardware));

        // 11s后系统重启
        RestartCommand restartCommand = new RestartCommand(11_000, List.of(
                new BellCommand(1_000, hardware),
                new LightOnCommand(10, hardware)
        ));
        controller.addCommand(restartCommand);
        // 30s后系统停止运行
        controller.addCommand(new TerminateCommand(30_000));

        // 启动控制线程
        new Thread(controller, "system control").start();
    }
}
