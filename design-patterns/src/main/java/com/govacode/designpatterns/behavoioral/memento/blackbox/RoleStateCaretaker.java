package com.govacode.designpatterns.behavoioral.memento.blackbox;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 角色状态管理者类（使用栈保存）
 *
 * @author gova
 */
public class RoleStateCaretaker {

    private final Deque<RoleStateMemento> stack = new ArrayDeque<>();

    public void pushState(RoleStateMemento memento) {
        stack.push(memento);
    }

    public RoleStateMemento popState() {
        return stack.pop();
    }

    public boolean canUndo() {
        return !stack.isEmpty();
    }
}
