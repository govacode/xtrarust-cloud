package com.govacode.designpatterns.structural.adapter;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class SamsungTFCard implements TFCard {

    @Override
    public byte[] readTF() {
        return "data from samsung tf card".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void writeTF(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        log.info("write data to samsung tf card success, data length: {}", bytes.length);
    }
}
