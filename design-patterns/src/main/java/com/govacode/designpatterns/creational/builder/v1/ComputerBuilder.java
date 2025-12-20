package com.govacode.designpatterns.creational.builder.v1;

/**
 * The builder abstraction.
 *
 * @author gova
 */
public interface ComputerBuilder {

    void buildMotherboard(String motherboard);

    void buildCpu(String cpu);

    void buildGraphicsCard(String graphicsCard);

    void buildMemoryChip(String memoryChip);

    void buildHardDisk(String hardDisk);

    void buildPowerSupplier(String powerSupplier);

    void buildMonitor(String monitor);

    void buildKeyboard(String keyboard);

    void buildMouse(String mouse);

    Computer build();

}
