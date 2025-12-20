package com.govacode.designpatterns.creational.builder.v1;

/**
 * Director for Computer
 *
 * @author fulgens
 */
public class ComputerDirector {

    private final ComputerBuilder computerBuilder;

    public ComputerDirector(ComputerBuilder computerBuilder) {
        this.computerBuilder = computerBuilder;
    }

    public Computer construct(String motherboard, String cpu, String graphicsCard, String monitor,
                              String memoryChip, String hardDisk, String powerSupplier, String keyboard,
                              String mouse) {
        computerBuilder.buildMotherboard(motherboard);
        computerBuilder.buildCpu(cpu);
        computerBuilder.buildGraphicsCard(graphicsCard);
        computerBuilder.buildMemoryChip(memoryChip);
        computerBuilder.buildHardDisk(hardDisk);
        computerBuilder.buildPowerSupplier(powerSupplier);
        computerBuilder.buildMonitor(monitor);
        computerBuilder.buildKeyboard(keyboard);
        computerBuilder.buildMouse(mouse);
        return computerBuilder.build();
    }

    public static void main(String[] args) {
        ComputerDirector director = new ComputerDirector(new ComputerBuilderImpl());
        Computer computer = director.construct(
                "Xxx牌B360",
                "Xxx牌 I5",
                "Xxx牌GTX1060",
                "Xxx牌显示器",
                "Xxx牌16G",
                "Xxx牌SSD",
                "Xxx牌电源",
                "Xxx牌键盘",
                "Xxx牌鼠标");
        System.out.println(computer);
    }
}
