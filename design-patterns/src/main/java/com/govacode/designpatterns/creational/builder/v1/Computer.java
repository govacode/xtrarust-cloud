package com.govacode.designpatterns.creational.builder.v1;

/**
 * Represents the product created by the builder.
 *
 * @author gova
 */
public class Computer {

    // 主板
    private String motherboard;

    // cpu
    private String cpu;

    // 显卡
    private String graphicsCard;

    // 内存条
    private String memoryChip;

    // 硬盘
    private String hardDisk;

    // 电源
    private String powerSupplier;

    // 显示器
    private String monitor;

    // 键盘
    private String keyboard;

    // 鼠标
    private String mouse;

    public String getMotherboard() {
        return motherboard;
    }

    public void setMotherboard(String motherboard) {
        this.motherboard = motherboard;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public void setGraphicsCard(String graphicsCard) {
        this.graphicsCard = graphicsCard;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public String getMemoryChip() {
        return memoryChip;
    }

    public void setMemoryChip(String memoryChip) {
        this.memoryChip = memoryChip;
    }

    public String getHardDisk() {
        return hardDisk;
    }

    public void setHardDisk(String hardDisk) {
        this.hardDisk = hardDisk;
    }

    public String getPowerSupplier() {
        return powerSupplier;
    }

    public void setPowerSupplier(String powerSupplier) {
        this.powerSupplier = powerSupplier;
    }

    public String getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(String keyboard) {
        this.keyboard = keyboard;
    }

    public String getMouse() {
        return mouse;
    }

    public void setMouse(String mouse) {
        this.mouse = mouse;
    }

    @Override
    public String toString() {
        return "Computer{" +
                "motherboard='" + motherboard + '\'' +
                ", cpu='" + cpu + '\'' +
                ", graphicsCard='" + graphicsCard + '\'' +
                ", monitor='" + monitor + '\'' +
                ", memoryChip='" + memoryChip + '\'' +
                ", hardDisk='" + hardDisk + '\'' +
                ", powerSupplier='" + powerSupplier + '\'' +
                ", keyboard='" + keyboard + '\'' +
                ", mouse='" + mouse + '\'' +
                '}';
    }
}
