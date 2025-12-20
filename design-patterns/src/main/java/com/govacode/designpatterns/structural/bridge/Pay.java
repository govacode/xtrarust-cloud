package com.govacode.designpatterns.structural.bridge;

import java.math.BigDecimal;

public abstract class Pay {

    protected PayMode payMode;

    public Pay(PayMode payMode) {
        this.payMode = payMode;
    }

    public abstract void transfer(String userId, String tradeId, BigDecimal amount);
}
