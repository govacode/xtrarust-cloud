package com.govacode.designpatterns.structural.adapter;

/**
 * TF卡（即适配者接口）
 *
 * @author gova
 */
public interface TFCard {

    byte[] readTF();

    void writeTF(byte[] bytes);
}
