package com.govacode.designpatterns.creational.builder.v1;

/**
 * {@link ComputerBuilder} interface implementation.
 *
 * @author gova
 */
public class ComputerBuilderImpl implements ComputerBuilder {

    private final Computer computer = new Computer();

    @Override
    public void buildMotherboard(String motherboard) {
        computer.setMotherboard(motherboard);
    }

    @Override
    public void buildCpu(String cpu) {
        computer.setCpu(cpu);
    }

    @Override
    public void buildGraphicsCard(String graphicsCard) {
        computer.setGraphicsCard(graphicsCard);
    }

    @Override
    public void buildMemoryChip(String memoryChip) {
        computer.setMemoryChip(memoryChip);
    }

    @Override
    public void buildHardDisk(String hardDisk) {
        computer.setHardDisk(hardDisk);
    }

    @Override
    public void buildPowerSupplier(String powerSupplier) {
        computer.setPowerSupplier(powerSupplier);
    }

    @Override
    public void buildMonitor(String monitor) {
        computer.setMonitor(monitor);
    }

    @Override
    public void buildKeyboard(String keyboard) {
        computer.setKeyboard(keyboard);
    }

    @Override
    public void buildMouse(String mouse) {
        computer.setMouse(mouse);
    }

    @Override
    public Computer build() {
        return computer;
    }
}
