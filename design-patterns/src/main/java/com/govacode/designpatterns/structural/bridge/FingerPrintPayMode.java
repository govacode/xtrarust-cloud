package com.govacode.designpatterns.structural.bridge;

import lombok.extern.slf4j.Slf4j;

/**
 * 指纹支付
 *
 * @author gova
 */
@Slf4j
public class FingerPrintPayMode implements PayMode {

    @Override
    public boolean riskControlVerification(String userId) {
        log.info("指纹支付 风控校验指纹信息");
        return true;
    }
}
