package com.govacode.designpatterns.behavoioral.command;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 服务员类（命令调用者/请求者 invoker）
 *
 * @author gova
 */
@Slf4j
public class Waiter {

    // 可以持有一组命令
    private List<Command> commands = new ArrayList<>();

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void submitOrder() {
        log.info("顾客下单");
        commands.stream()
                .filter(Objects::nonNull)
                .forEach(Command::execute);
    }
}
