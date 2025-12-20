package com.govacode.designpatterns.structural.adapter;

import java.util.Collection;

/**
 * 适配器模式
 *
 * JDK适配器：
 * {@link java.util.Arrays#asList(Object[])}
 * {@link java.util.Collections#enumeration(Collection)}
 * {@link java.io.InputStreamReader}中的StreamDecoder
 *
 * @author gova
 */
public class Client {

    public static void main(String[] args) {
        Computer computer = new Computer();

        computer.readSDCard(new SamsungSDCard());

        computer.readSDCard(new TFCardAdapter(new SamsungTFCard()));
    }
}
