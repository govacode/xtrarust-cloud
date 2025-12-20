package com.govacode.designpatterns.structural.adapter;

/**
 * SD卡接口（即目标接口）
 *
 * @author gova
 */
public interface SDCard {

    byte[] readSD();

    void writeSD(byte[] bytes);
}
