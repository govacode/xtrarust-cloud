package com.govacode.designpatterns.structural.bridge;

/**
 * 支付方式
 *
 * @author gova
 */
public interface PayMode {

    // 风控校验
    boolean riskControlVerification(String userId);
}
