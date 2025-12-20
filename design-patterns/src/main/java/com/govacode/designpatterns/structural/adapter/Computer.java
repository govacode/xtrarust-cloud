package com.govacode.designpatterns.structural.adapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Computer {

    public void readSDCard(SDCard sdCard) {
        byte[] bytes = sdCard.readSD();
        log.info("computer read data from sd card, data length: {}", bytes.length);
    }
}
