package com.govacode.designpatterns.structural.bridge;

import lombok.extern.slf4j.Slf4j;

/**
 * 人脸支付
 *
 * @author gova
 */
@Slf4j
public class FacePayMode implements PayMode {

    @Override
    public boolean riskControlVerification(String userId) {
        log.info("人脸支付 风控校验脸部识别");
        return true;
    }
}
