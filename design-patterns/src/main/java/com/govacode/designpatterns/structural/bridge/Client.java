package com.govacode.designpatterns.structural.bridge;

import java.math.BigDecimal;

public class Client {

    public static void main(String[] args) {
        Pay aliPay = new AliPay(new FingerPrintPayMode());
        aliPay.transfer("user1001", "2381842847", new BigDecimal("18.56"));

        Pay wechatPay = new WechatPay(new FacePayMode());
        wechatPay.transfer("user1009", "2381842899", new BigDecimal("23.78"));
    }
}
