package com.govacode.designpatterns.behavoioral.command.controlframework;

import java.time.Duration;
import java.time.Instant;

/**
 * 抽象命令组件
 *
 * @author gova
 */
public abstract class AbstractCommand {

    /**
     * 执行时间
     */
    private Instant executeTime;

    /**
     * 延迟执行时间
     */
    protected final Duration delayTime;

    /**
     * 控制器
     */
    protected Controller controller;

    protected AbstractCommand(long delayTimeInMillisSecond) {
        this.delayTime = Duration.ofMillis(delayTimeInMillisSecond);
        start();
    }

    /**
     * 计算执行时间以启动
     */
    public void start() { // 可以重复执行
        executeTime = Instant.now().plus(delayTime);
    }

    /**
     * 是否可以执行
     *
     * @return true可以执行
     */
    public boolean isReady() {
        return Instant.now().isAfter(executeTime);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * 抽象执行方法（子类实现）
     */
    public abstract void execute();
}
