package com.xtrarust.cloud.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xtrarust.cloud.db.mybatis.core.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

/**
 * Oauth2 授权确认信息<br>
 * An OAuth2AuthorizationConsent is a representation of an authorization "consent" (decision) from an OAuth2 authorization request flow – for example, the authorization_code grant, which holds the authorities granted to a client by the resource owner.
 *
 * @author gova
 * @see OAuth2AuthorizationConsent
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oauth2_authorization_consent")
public class Oauth2AuthorizationConsentDO extends BaseDO {

    /**
     * spring授权服务器registeredClientId与principalName为联合主键
     * 但MybatisPlus不支持联合主键 因此自行添加一个
     */
    @TableId
    private Long id;

    /**
     * 客户端ID<br>
     * The ID that uniquely identifies the RegisteredClient.
     */
    private String registeredClientId;

    /**
     * 用户名<br>
     * The principal name of the resource owner.
     */
    private String principalName;

    /**
     * 确认授予客户端的权限<br>
     * The authorities granted to the client by the resource owner. An authority can represent a scope, a claim, a permission, a role, and others.
     */
    private String authorities;
}
