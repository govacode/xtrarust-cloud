package com.govacode.designpatterns.structural.adapter;

import lombok.extern.slf4j.Slf4j;

/**
 * 将TF卡转换为SD卡的适配器
 *
 * @author wdxu
 */
@Slf4j
public class TFCardAdapter implements SDCard {

    private final TFCard tfCard;

    public TFCardAdapter(TFCard tfCard) {
        this.tfCard = tfCard;
    }

    @Override
    public byte[] readSD() {
        log.info("adapter is working, read data from tf card");
        return tfCard.readTF();
    }

    @Override
    public void writeSD(byte[] bytes) {
        log.info("adapter is working, write data to tf card");
        tfCard.writeTF(bytes);
    }
}
