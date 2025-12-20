package com.xtrarust.cloud.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.xtrarust.cloud.db.mybatis.core.entity.BaseDO;
import com.xtrarust.cloud.db.mybatis.core.type.StringListTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Oauth2 客户端
 *
 * @author gova
 * @see RegisteredClient
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "oauth2_registered_client", autoResultMap = true)
public class Oauth2RegisteredClientDO extends BaseDO {

    /**
     * 主键ID<br>
     * The ID that uniquely identifies the RegisteredClient.
     */
    @TableId
    private String id;

    /**
     * 客户端ID<br>
     * The client identifier.
     */
    private String clientId;

    /**
     * The time at which the client identifier was issued.
     */
    private LocalDateTime clientIdIssuedAt;

    /**
     * 客户端密码<br>
     * The client’s secret. The value should be encoded using Spring Security’s PasswordEncoder.
     */
    private String clientSecret;

    /**
     * 客户端密码过期时间<br>
     * The time at which the client secret expires.
     */
    private LocalDateTime clientSecretExpiresAt;

    /**
     * 客户端名称<br>
     * A descriptive name used for the client. The name may be used in certain scenarios, such as when displaying the client name in the consent page.
     */
    private String clientName;

    /**
     * 客户端认证方法列表<br>
     * The authentication method(s) that the client may use.
     *
     * @see ClientAuthenticationMethod
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> clientAuthenticationMethods;

    /**
     * 授权类型列表<br>
     * The authorization grant type(s) that the client can use.
     *
     * @see AuthorizationGrantType
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> authorizationGrantTypes;

    /**
     * 重定向URI列表<br>
     * The registered redirect URI(s) that the client may use in redirect-based flows – for example, authorization_code grant.
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> redirectUris;

    /**
     * OIDC登出后重定向URI列表<br>
     * The post logout redirect URI(s) that the client may use for logout.
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> postLogoutRedirectUris;

    /**
     * scope列表<br>
     * The scope(s) that the client is allowed to request.
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> scopes;

    /**
     * 客户端额外配置信息<br>
     * The custom settings for the client – for example, require PKCE, require authorization consent, and others.
     *
     * @see ClientSettings
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Oauth2ClientSettings clientSettings;

    /**
     * 客户端token额外配置信息<br>
     * The custom settings for the OAuth2 tokens issued to the client – for example, access/refresh token time-to-live, reuse refresh tokens, and others.
     *
     * @see TokenSettings
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Oauth2TokenSettings tokenSettings;

    @Data
    public static class Oauth2ClientSettings {

        /**
         * 授权码流程中客户端是否需要提供proof key（PKCE需要设置为true）
         */
        private Boolean requireProofKey = false;

        /**
         * 是否需要授权确认
         */
        private Boolean requireAuthorizationConsent = false;

        /**
         * the URL for the Client's JSON Web Key Set
         */
        private String jwkSetUrl;

        /**
         * the JWS algorithm that must be used for signing the JWT used to authenticate the Client
         * at the Token Endpoint for the private_key_jwt and client_secret_jwt authentication methods.
         * @see JwsAlgorithm
         */
        private String signingAlgorithm;
    }

    @Data
    public static class Oauth2TokenSettings {

        /**
         * 授权码TTL
         */
        private Duration authorizationCodeTimeToLive;

        /**
         * access_token TTL
         */
        private Duration accessTokenTimeToLive;

        /**
         * access_token 格式
         * 1. 自包含 JWT
         * 2. Reference (opaque)
         * @see OAuth2TokenFormat
         */
        private String accessTokenFormat;

        /**
         * device_code TTL
         */
        private Duration deviceCodeTimeToLive;

        /**
         * 是否重用refresh_token
         */
        private Boolean reuseRefreshTokens;

        /**
         * refresh_token TTL
         */
        private Duration refreshTokenTimeToLive;

        /**
         * OIDC id_token签名算法
         * @see SignatureAlgorithm
         */
        private String idTokenSignatureAlgorithm;
    }
}
