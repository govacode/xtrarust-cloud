package com.xtrarust.cloud.auth.security.oauth2.client.endpoint;

import com.xtrarust.cloud.auth.security.oauth2.client.Oauth2ClientConstants;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AuthorizationRequestCustomizer {

    // DefaultOAuth2AuthorizationRequestResolver#resolve在调用builder.build()前会调用authorizationRequestCustomizer对builder做自定义操作
    // 这里提供授权请求的构建函数，对请求uri做自定义处理，具体逻辑见build方法
    public static void customize(OAuth2AuthorizationRequest.Builder builder) {
        builder.attributes(attributes -> {
            String registrationId = (String) attributes.get(OAuth2ParameterNames.REGISTRATION_ID);
            // 微信请求授权码链接示例：
            // https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
            // Spring Security 默认的参数名为response_type、client_id、scope、state、redirect_uri与微信要求的不同
            // Spring Security 标准链接参数 client_id=CLIENT_ID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE
            // 对比可以发现微信把 client_id 变成了 appid 而且最后多了个锚点 #wechat_redirect 另外 参数顺序必须与示例一致
            if (Oauth2ClientConstants.REGISTRATION_ID_WECHAT.equals(registrationId)) { // 兼容微信
                // 将client_id替换为appid
                builder.parameters(parameters -> {
                    Map<String, Object> paramMap = new LinkedHashMap<>();
                    paramMap.put(Oauth2ClientConstants.PARAMETER_NAME_APPID, parameters.get(OAuth2ParameterNames.CLIENT_ID));
                    paramMap.put(OAuth2ParameterNames.REDIRECT_URI, parameters.get(OAuth2ParameterNames.REDIRECT_URI));
                    paramMap.put(OAuth2ParameterNames.RESPONSE_TYPE, parameters.get(OAuth2ParameterNames.RESPONSE_TYPE));
                    paramMap.put(OAuth2ParameterNames.SCOPE, parameters.get(OAuth2ParameterNames.SCOPE));
                    paramMap.put(OAuth2ParameterNames.STATE, parameters.get(OAuth2ParameterNames.STATE));

                    parameters.clear();
                    parameters.putAll(paramMap);
                });
                // 默认是(builder) -> builder.build() 这里追加'#wechat_redirect'锚点
                builder.authorizationRequestUri(uriBuilder -> uriBuilder.fragment("wechat_redirect").build());
            }
        });
    }
}
