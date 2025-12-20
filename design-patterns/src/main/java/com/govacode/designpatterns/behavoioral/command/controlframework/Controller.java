package com.govacode.designpatterns.behavoioral.command.controlframework;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制器
 *
 * @author gova
 */
public class Controller implements Runnable {

    private final List<AbstractCommand> commands;

    public Controller() {
        this.commands = new ArrayList<>();
    }

    public void addCommand(AbstractCommand command) {
        commands.add(command);
        command.setController(this);
    }

    @Override
    public void run() {
        while (commands.size() > 0) {
            for (AbstractCommand command : new ArrayList<>(commands)) {
                if (command.isReady()) {
                    command.execute();
                    commands.remove(command);
                }
            }
        }
    }
}
