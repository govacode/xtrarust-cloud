package com.govacode.designpatterns.structural.adapter;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class SamsungSDCard implements SDCard {

    @Override
    public byte[] readSD() {
        return "data from samsung sd card".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void writeSD(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        log.info("write data to samsung sd card success, data length: {}", bytes.length);
    }
}
