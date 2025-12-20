package com.govacode.designpatterns.behavoioral.memento.whitebox;

import lombok.extern.slf4j.Slf4j;

/**
 * 黑箱备忘录测试
 *
 * @author gova
 */
@Slf4j
public class Client {

    public static void main(String[] args) {
        // 创建状态管理者
        RoleStateCaretaker caretaker = new RoleStateCaretaker();

        GameRole gameRole = new GameRole();
        gameRole.initState();
        gameRole.stateDisplay();

        for (int i = 0; i < 5; i++) {
            log.info("角色开始攻击...");
            // 攻击前保存当前状态
            caretaker.pushState(gameRole.saveState());
            gameRole.fight();
            gameRole.stateDisplay();
        }

        log.info("角色恢复中...");
        while (caretaker.canUndo()) {
            gameRole.recoverState(caretaker.popState());
            gameRole.stateDisplay();
        }
    }
}
