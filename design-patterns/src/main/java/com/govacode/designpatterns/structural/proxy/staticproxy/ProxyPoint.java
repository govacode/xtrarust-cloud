package com.govacode.designpatterns.structural.proxy.staticproxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyPoint implements SellTickets {

    private final TrainStation trainStation = new TrainStation();

    @Override
    public void sell(int count) {
        log.info("收取服务费");
        trainStation.sell(count);
    }

    public static void main(String[] args) {
        new ProxyPoint().sell(5);
    }
}
