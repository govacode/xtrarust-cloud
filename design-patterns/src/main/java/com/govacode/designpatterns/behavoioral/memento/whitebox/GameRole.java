package com.govacode.designpatterns.behavoioral.memento.whitebox;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 游戏角色类
 *
 * @author gova
 */
@Slf4j
public class GameRole {

    private int vit; //生命力
    private int atk; //攻击力
    private int def; //防御力

    //初始化状态
    public void initState() {
        this.vit = 100;
        this.atk = 100;
        this.def = 100;
    }

    //战斗
    public void fight() {
        this.vit -= 10;
        this.atk -= 10;
        this.def -= 10;
    }

    //保存角色状态
    public Memento saveState() {
        return new RoleStateMemento(vit, atk, def);
    }

    //恢复角色状态
    public void recoverState(Memento memento) {
        RoleStateMemento roleStateMemento = (RoleStateMemento) memento;
        this.vit = roleStateMemento.getVit();
        this.atk = roleStateMemento.getAtk();
        this.def = roleStateMemento.getDef();
    }

    public void stateDisplay() {
        log.info("角色生命力：{}", vit);
        log.info("角色攻击力：{}", atk);
        log.info("角色防御力：{}", def);
    }

    /**
     * 游戏状态存储类(备忘录类 私有静态内部类不对外暴露)
     */
    @Data
    private static class RoleStateMemento implements Memento {

        private int vit;
        private int atk;
        private int def;

        public RoleStateMemento(int vit, int atk, int def) {
            this.vit = vit;
            this.atk = atk;
            this.def = def;
        }
    }
}
