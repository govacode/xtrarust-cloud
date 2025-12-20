package com.govacode.designpatterns.behavoioral.command;

/**
 * 命令接口
 *
 * @author gova
 */
public interface Command {

    void execute();

    default boolean isReady() {
        return false;
    }
}
