package com.xtrarust.cloud.auth.runner;

import com.xtrarust.cloud.id.util.SnowflakeIdUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Component
public class InitOauth2ClientRunner implements CommandLineRunner {

    @Resource
    private RegisteredClientRepository registeredClientRepository;

    @Override
    public void run(String... args) throws Exception {
        // http://localhost:9000/oauth2/authorize?response_type=code&client_id=oidc-client&scope=openid%20profile&state=PWXqMKvJvmevD9ct4xHipJQe1TU7r09xI6MrM5Wcj5U%3D&redirect_uri=http://127.0.0.1:8000/login/oauth2/code/oidc-client
        RegisteredClient registeredClient = registeredClientRepository.findByClientId("oidc-client");
        if (Objects.isNull(registeredClient)) {
            registeredClient = RegisteredClient.withId(SnowflakeIdUtil.nextIdStr())
                    .clientId("oidc-client")
                    .clientSecret("{noop}secret")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .redirectUri("http://127.0.0.1:8000/login/oauth2/code/oidc-client")
                    .redirectUri("http://127.0.0.1:8000/authorized")
                    .postLogoutRedirectUri("http://127.0.0.1:8000/logged-out")
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope("message.read")
                    .scope("message.write")
                    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                    .tokenSettings(TokenSettings.builder()
                            .authorizationCodeTimeToLive(Duration.ofMinutes(5))
                            .accessTokenTimeToLive(Duration.ofMinutes(30))
                            .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                            .reuseRefreshTokens(true)
                            .refreshTokenTimeToLive(Duration.ofMinutes(180))
                            .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
                            .build())
                    .build();
            registeredClientRepository.save(registeredClient);
        }

        // 设备码客户端
        RegisteredClient deviceClient = registeredClientRepository.findByClientId("device-client");
        if (Objects.isNull(deviceClient)) {
            deviceClient = RegisteredClient.withId(SnowflakeIdUtil.nextIdStr())
                    .clientId("device-client")
                    // 公共客户端
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    // 设备码授权
                    .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .scope("message.read")
                    .scope("message.write")
                    .build();
            registeredClientRepository.save(deviceClient);
        }

        // public客户端
        // 客户端认证方式为none 且框架不会签发refresh token给公开客户端
        // http://127.0.0.1:9000/oauth2/authorize?response_type=code&client_id=pkce-client&scope=openid%20profile%20api&state=PWXqMKvJvmevD9ct4xHipJQe1TU7r09xI6MrM5Wcj5U%3D&redirect_uri=http://127.0.0.1:8000/authorized&code_challenge=xQObLnSgnZMYVTNs3U168CDV0IlSHTDqK71O3t6lduE&code_challenge_method=S256
        RegisteredClient pkceClient = registeredClientRepository.findByClientId("pkce-client");
        if (Objects.isNull(pkceClient)) {
            pkceClient = RegisteredClient.withId(SnowflakeIdUtil.nextIdStr())
                    .clientId("pkce-client")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    // 授权码模式回调地址，oauth2.1已改为精准匹配，不能只设置域名，并且屏蔽了localhost，本机使用127.0.0.1访问
                    .redirectUri("http://127.0.0.1:8000/authorized")
                    .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofMinutes(5))
                            .refreshTokenTimeToLive(Duration.ofDays(30))
                            .build()
                    )
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope("api")
                    .build();
            registeredClientRepository.save(pkceClient);
        }
    }
}
