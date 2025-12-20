package com.xtrarust.cloud.common.validation;

import cn.hutool.core.util.StrUtil;
import com.xtrarust.cloud.common.factory.RegexPatternPoolFactory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MobileValidator implements ConstraintValidator<Mobile, String> {

    @Override
    public void initialize(Mobile annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StrUtil.isEmpty(value)) {
            return true;
        }
        return RegexPatternPoolFactory.MOBILE.matcher(value).matches();
    }

}
