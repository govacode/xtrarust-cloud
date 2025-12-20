package com.govacode.designpatterns.structural.bridge;

import lombok.extern.slf4j.Slf4j;

/**
 * 密码支付
 *
 * @author gova
 */
@Slf4j
public class PasswordPayMode implements PayMode {

    @Override
    public boolean riskControlVerification(String userId) {
        log.info("密码支付 风控校验密码信息");
        return true;
    }
}
