package com.govacode.designpatterns.behavoioral.templatemethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class StealingMethod implements Stealing {

    // 确定目标
    protected abstract String pickTarget();

    // 迷惑目标
    protected abstract void confuseTarget(String target);

    // 偷东西
    protected abstract void stealTheItem(String target);

    // 模板方法：偷
    // 模板方法中除了定义抽象方法外也可定义钩子方法，子类可以选择重写或直接使用父类方法
    @Override
    public final void steal() {
        var target = pickTarget();
        log.info("The target has been chosen as {}.", target);
        confuseTarget(target);
        stealTheItem(target);
    }
}
