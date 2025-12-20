package com.xtrarust.cloud.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xtrarust.cloud.db.mybatis.core.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.time.LocalDateTime;

/**
 * Oauth2 授权信息
 *
 * @author gova
 * @see OAuth2Authorization
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oauth2_authorization")
public class Oauth2AuthorizationDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private String id;

    /**
     * 客户端主键ID
     */
    private String registeredClientId;

    /**
     * The principal name of the resource owner (or client).
     */
    private String principalName;

    /**
     * 授权类型
     * @see AuthorizationGrantType
     */
    private String authorizationGrantType;

    /**
     * 已授权scopes
     */
    private String authorizedScopes;

    /**
     * The additional attributes specific to the executed authorization grant type – for example, the authenticated Principal, OAuth2AuthorizationRequest, and others.
     */
    private String attributes;

    private String state;

    /**
     * 授权码
     */
    private String authorizationCodeValue;

    /**
     * 授权码颁发时间
     */
    private LocalDateTime authorizationCodeIssuedAt;

    /**
     * 授权码过期时间
     */
    private LocalDateTime authorizationCodeExpiresAt;

    /**
     * 授权码元数据信息
     */
    private String authorizationCodeMetadata;

    /**
     * access_token
     */
    private String accessTokenValue;

    /**
     * access_token签发时间
     */
    private LocalDateTime accessTokenIssuedAt;

    /**
     * access_token过期时间
     */
    private LocalDateTime accessTokenExpiresAt;

    /**
     * access_token元数据信息
     */
    private String accessTokenMetadata;

    /**
     * access_token类型
     */
    private String accessTokenType;

    /**
     * access_token scopes
     */
    private String accessTokenScopes;

    /**
     * refresh_token
     */
    private String refreshTokenValue;

    /**
     * refresh_token签发时间
     */
    private LocalDateTime refreshTokenIssuedAt;

    /**
     * refresh_token过期时间
     */
    private LocalDateTime refreshTokenExpiresAt;

    /**
     * refresh_token元数据信息
     */
    private String refreshTokenMetadata;

    /**
     * id_token
     */
    private String oidcIdTokenValue;

    /**
     * id_token签发时间
     */
    private LocalDateTime oidcIdTokenIssuedAt;

    /**
     * id_token过期时间
     */
    private LocalDateTime oidcIdTokenExpiresAt;

    /**
     * id_token元数据信息
     */
    private String oidcIdTokenMetadata;

    /**
     * id_token claims
     */
    private String oidcIdTokenClaims;

    /**
     * user code
     */
    private String userCodeValue;

    /**
     * user code签发时间
     */
    private LocalDateTime userCodeIssuedAt;

    /**
     * user code过期时间
     */
    private LocalDateTime userCodeExpiresAt;

    /**
     * user code元数据信息
     */
    private String userCodeMetadata;

    /**
     * 设备码
     */
    private String deviceCodeValue;

    /**
     * 设备码签发时间
     */
    private LocalDateTime deviceCodeIssuedAt;

    /**
     * 设备码过期时间
     */
    private LocalDateTime deviceCodeExpiresAt;

    /**
     * 设备码元数据信息
     */
    private String deviceCodeMetadata;
}
