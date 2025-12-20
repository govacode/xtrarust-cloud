package com.govacode.designpatterns.behavoioral.memento.blackbox;

import lombok.Data;

/**
 * 游戏状态存储类(备忘录类)
 *
 * @author gova
 */
@Data
public class RoleStateMemento {

    private int vit;
    private int atk;
    private int def;

    public RoleStateMemento(int vit, int atk, int def) {
        this.vit = vit;
        this.atk = atk;
        this.def = def;
    }
}
