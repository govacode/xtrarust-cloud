package com.govacode.designpatterns.behavoioral.command.controlframework;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 系统重启命令
 *
 * @author gova
 */
@Slf4j
public class RestartCommand extends AbstractCommand {

    private final List<AbstractCommand> restartCommands;

    public RestartCommand(long delayTimeInMillisSecond, List<AbstractCommand> commands) {
        super(delayTimeInMillisSecond);
        restartCommands = commands;
    }

    @Override
    public void execute() {
        log.info("restarting system");
        for (AbstractCommand restartCommand : restartCommands) {
            restartCommand.start();
            controller.addCommand(restartCommand);
        }
    }
}
