package com.govacode.designpatterns.structural.bridge;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class WechatPay extends Pay {

    public WechatPay(PayMode payMode) {
        super(payMode);
    }

    @Override
    public void transfer(String userId, String tradeId, BigDecimal amount) {
        log.info("微信支付转账开始 userId: {} tradeId: {} amount: {}", userId, tradeId, amount);
        boolean passed = payMode.riskControlVerification(userId);
        if (!passed) {
            log.info("风控校验不通过");
            return;
        }
        log.info("微信支付转账成功");
    }
}
