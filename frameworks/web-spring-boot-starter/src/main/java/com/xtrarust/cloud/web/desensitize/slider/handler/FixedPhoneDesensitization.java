package com.xtrarust.cloud.web.desensitize.slider.handler;

import com.xtrarust.cloud.web.desensitize.slider.annotation.FixedPhoneDesensitize;

/**
 * {@link FixedPhoneDesensitize} 的脱敏处理器
 *
 * @author gaibu
 */
public class FixedPhoneDesensitization extends AbstractSliderDesensitizationHandler<FixedPhoneDesensitize> {
    @Override
    Integer getPrefixKeep(FixedPhoneDesensitize annotation) {
        return annotation.prefixKeep();
    }

    @Override
    Integer getSuffixKeep(FixedPhoneDesensitize annotation) {
        return annotation.suffixKeep();
    }

    @Override
    String getReplacer(FixedPhoneDesensitize annotation) {
        return annotation.replacer();
    }
}
