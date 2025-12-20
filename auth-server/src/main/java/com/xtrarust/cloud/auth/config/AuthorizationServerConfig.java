package com.xtrarust.cloud.auth.config;

import cn.hutool.core.util.ReflectUtil;
import com.xtrarust.cloud.auth.security.oauth2.server.authentication.CustomPublicClientAuthenticationConverter;
import com.xtrarust.cloud.auth.security.oauth2.server.authentication.CustomPublicClientAuthenticationProvider;
import com.xtrarust.cloud.auth.security.oauth2.server.resource.CustomBearerTokenAccessDeniedHandler;
import com.xtrarust.cloud.auth.security.oauth2.server.resource.CustomBearerTokenAuthenticationEntryPoint;
import com.xtrarust.cloud.auth.security.oauth2.server.token.CustomOAuth2RefreshTokenGenerator;
import com.xtrarust.cloud.auth.security.oauth2.server.authentication.DeviceClientAuthenticationConverter;
import com.xtrarust.cloud.auth.security.oauth2.server.authentication.DeviceClientAuthenticationProvider;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.PublicClientAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.PublicClientAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.DelegatingAccessDeniedHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 授权服务器配置
 *
 * @author gova
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private static final Set<String> ID_TOKEN_CLAIMS = Set.of(
            IdTokenClaimNames.ISS,
            IdTokenClaimNames.SUB,
            IdTokenClaimNames.AUD,
            IdTokenClaimNames.EXP,
            IdTokenClaimNames.IAT,
            IdTokenClaimNames.AUTH_TIME,
            IdTokenClaimNames.NONCE,
            IdTokenClaimNames.ACR,
            IdTokenClaimNames.AMR,
            IdTokenClaimNames.AZP,
            IdTokenClaimNames.AT_HASH,
            IdTokenClaimNames.C_HASH
    );

    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    private final RegisteredClientRepository registeredClientRepository;

    private final OAuth2AuthorizationService authorizationService;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      AuthorizationServerSettings authorizationServerSettings) throws Exception {
        DeviceClientAuthenticationConverter deviceClientAuthenticationConverter =
                new DeviceClientAuthenticationConverter(authorizationServerSettings.getDeviceAuthorizationEndpoint());
        DeviceClientAuthenticationProvider deviceClientAuthenticationProvider =
                new DeviceClientAuthenticationProvider(registeredClientRepository);

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, authorizationServer -> {
                    authorizationServer
                            // 设备码授权端点配置 此端点会返回device_code、user_code、verification_uri
                            .deviceAuthorizationEndpoint(endpoint -> {
                                endpoint.verificationUri("/activate");
                            })
                            // 设备码验证端点配置
                            .deviceVerificationEndpoint(endpoint -> {
                                endpoint.consentPage(CUSTOM_CONSENT_PAGE_URI);
                            })
                            // 客户端认证添加设备码authenticationConverter、authenticationProvider
                            .clientAuthentication(clientAuthentication -> {
                                clientAuthentication
                                        .authenticationConverter(deviceClientAuthenticationConverter)
                                        .authenticationProvider(deviceClientAuthenticationProvider);
                            })
                            .authorizationEndpoint(endpoint -> {
                                // 授权码模式授权确认页 不论配置的是绝对路径还是相对路径均为重定向 见OAuth2AuthorizationEndpointFilter#sendAuthorizationConsent
                                endpoint.consentPage(CUSTOM_CONSENT_PAGE_URI);
                            })
                            .tokenEndpoint(endpoint -> {
                                // OAuth2TokenEndpointConfigurer#createDefaultAuthenticationProviders() 所有的provider使用的是同一个DelegatingOAuth2TokenGenerator
                                // 使用自定义的OAuth2RefreshTokenGenerator为public client签发refresh token
                                endpoint.authenticationProviders(providers -> {
                                    providers.forEach(provider -> {
                                        if (provider instanceof OAuth2AuthorizationCodeAuthenticationProvider) {
                                            Object tokenGenerator = ReflectUtil.getFieldValue(provider, "tokenGenerator");
                                            if (tokenGenerator instanceof DelegatingOAuth2TokenGenerator) {
                                                Object tokenGenerators = ReflectUtil.getFieldValue(tokenGenerator, "tokenGenerators");
                                                if (tokenGenerators instanceof List) {
                                                    List<OAuth2TokenGenerator<? extends OAuth2Token>> newTokenGenerators = new ArrayList<>();
                                                    ((List<?>) tokenGenerators).forEach(generator -> {
                                                        if (generator instanceof OAuth2RefreshTokenGenerator) {
                                                            newTokenGenerators.add(new CustomOAuth2RefreshTokenGenerator());
                                                        } else {
                                                            newTokenGenerators.add((OAuth2TokenGenerator<? extends OAuth2Token>) generator);
                                                        }
                                                    });
                                                    ReflectUtil.setFieldValue(tokenGenerator, "tokenGenerators", Collections.unmodifiableList(newTokenGenerators));
                                                }
                                            }
                                        }
                                    });
                                });
                            })
                            .clientAuthentication(clientAuthentication -> {
                                clientAuthentication
                                        .authenticationConverters(converters -> {
                                            converters.removeIf(e -> e instanceof PublicClientAuthenticationConverter);
                                            converters.add(new CustomPublicClientAuthenticationConverter());
                                        })
                                        .authenticationProviders(providers -> {
                                            providers.removeIf(provider -> provider instanceof PublicClientAuthenticationProvider);
                                            providers.add(new CustomPublicClientAuthenticationProvider(registeredClientRepository, authorizationService));
                                        });
                            })
                            // Enable OpenID Connect 1.0
                            .oidc(Customizer.withDefaults());
                })
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                // Redirect to the login page when not authenticated from the authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        // DelegatingAuthenticationEntryPoint
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // 资源服务器配置（OIDC用户信息端点及客户端注册端点属于资源）
                // 核心类：BearerTokenAuthenticationFilter、JwtAuthenticationProvider、JwtGrantedAuthoritiesConverter
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer
                                .jwt(Customizer.withDefaults())
                                .authenticationEntryPoint(new CustomBearerTokenAuthenticationEntryPoint())
                                .accessDeniedHandler(
                                        new DelegatingAccessDeniedHandler(
                                                new LinkedHashMap<>(Map.of(CsrfException.class, new AccessDeniedHandlerImpl())),
                                                new CustomBearerTokenAccessDeniedHandler()
                                        )
                                )
                );
        return http.build();
    }

    @Bean
    @SneakyThrows
    public JWKSource<SecurityContext> jwkSource() {
        // keytool -genkey -alias jose -keyalg RSA -storetype PKCS12 -keysize 2048 -validity 3650 -keystore ./jose.p12 -storepass 123456
        // 实例化密钥库
        KeyStore ks = KeyStore.getInstance("PKCS12");
        // 加载密钥库
        try (InputStream is = new ClassPathResource("cert/jose.p12").getInputStream()) {
            ks.load(is, "123456".toCharArray());
        }
        RSAKey rsaKey = RSAKey.load(ks, "jose", "123456".toCharArray());
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        // OAuth2AuthorizationServerConfiguration.jwtDecoder(JWKSource<SecurityContext>) is a convenience (static) utility method that
        // can be used to register a JwtDecoder @Bean, which is REQUIRED for the OpenID Connect 1.0 UserInfo endpoint and the OpenID Connect 1.0 Client Registration endpoint.
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    // 自定义OAuth2TokenCustomizer 在JwtGenerator、OAuth2AccessTokenGenerator生成token时进行增强
    // 若对access_token进行了权限增强 资源服务器解析解析jwt成JwtAuthenticationToken时可以自定义JwtAuthenticationConverter
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwsHeader.Builder headers = context.getJwsHeader();
            JwtClaimsSet.Builder claims = context.getClaims();
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                // Customize headers/claims for access_token
                if (context.getPrincipal().getPrincipal() instanceof UserDetails user) {
                    Set<String> authorities = Optional.ofNullable(user.getAuthorities()).orElse(Collections.emptyList()).stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());
                    claims.claim("authorities", authorities);
                }
            } else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                // Customize headers/claims for id_token
                Map<String, Object> thirdPartyClaims = extractClaims(context.getPrincipal());
                context.getClaims().claims(existingClaims -> {
                    // Remove conflicting claims set by this authorization server
                    existingClaims.keySet().forEach(thirdPartyClaims::remove);

                    // Remove standard id_token claims that could cause problems with clients
                    ID_TOKEN_CLAIMS.forEach(thirdPartyClaims::remove);

                    // Add all other claims directly to id_token
                    existingClaims.putAll(thirdPartyClaims);
                });
            }
        };
    }

    private Map<String, Object> extractClaims(Authentication principal) {
        Map<String, Object> claims;
        if (principal.getPrincipal() instanceof OidcUser oidcUser) {
            claims = oidcUser.getIdToken().getClaims();
        } else if (principal.getPrincipal() instanceof OAuth2User oauth2User) {
            claims = oauth2User.getAttributes();
        } else if (principal.getPrincipal() instanceof UserDetails) {
            // TODO 转为平台自己的用户
            claims = Collections.emptyMap();
        } else {
            claims = Collections.emptyMap();
        }
        return new HashMap<>(claims);
    }

}
