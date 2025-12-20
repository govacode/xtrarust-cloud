package com.xtrarust.cloud.auth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "图形验证码 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResp {

    @Schema(description = "是否开启", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean enable;

    @Schema(description = "验证码标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "1b3b7d00-83a8-4638-9e37-d67011855968")
    private String uuid;

    @Schema(description = "验证码图片，使用 Base64 编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String img;
}
