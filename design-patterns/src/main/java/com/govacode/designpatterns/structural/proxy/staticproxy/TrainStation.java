package com.govacode.designpatterns.structural.proxy.staticproxy;

import lombok.extern.slf4j.Slf4j;

/**
 * 火车站
 *
 * @author gova
 */
@Slf4j
public class TrainStation implements SellTickets {

    @Override
    public void sell(int count) {
        log.info("火车站售出{}张票", count);
    }
}
