package com.govacode.designpatterns.behavoioral.memento.whitebox;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 角色状态管理者类（使用栈保存）
 *
 * @author gova
 */
public class RoleStateCaretaker {

    private final Deque<Memento> stack = new ArrayDeque<>();

    public void pushState(Memento memento) {
        stack.push(memento);
    }

    public Memento popState() {
        return stack.pop();
    }

    public boolean canUndo() {
        return !stack.isEmpty();
    }
}
