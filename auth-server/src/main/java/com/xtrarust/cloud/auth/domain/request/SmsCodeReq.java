package com.xtrarust.cloud.auth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "短信验证码 Request VO")
@Data
@EqualsAndHashCode
public class SmsCodeReq {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18312345678")
    @NotNull(message = "手机号不能为空")
    private String mobile;

}
