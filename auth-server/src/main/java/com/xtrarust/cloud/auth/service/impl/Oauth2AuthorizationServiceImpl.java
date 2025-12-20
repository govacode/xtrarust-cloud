package com.xtrarust.cloud.auth.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtrarust.cloud.auth.config.SessionConfig;
import com.xtrarust.cloud.auth.domain.entity.Oauth2AuthorizationDO;
import com.xtrarust.cloud.auth.repository.mapper.Oauth2AuthorizationMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static com.xtrarust.cloud.auth.domain.converter.RegisteredClientConverter.resolveAuthorizationGrantType;

/**
 * The OAuth2AuthorizationService is the central component where new authorizations are stored and existing authorizations are queried.<br>
 *
 * 最好参照内部jdbc实现{@link JdbcOAuth2AuthorizationService} 这个文档实现有问题比如save方法应该有更新逻辑
 *
 * @link <a href="https://docs.spring.io/spring-authorization-server/reference/guides/how-to-jpa.html">How-to: Implement core services with JPA</a>
 */
@Service
public class Oauth2AuthorizationServiceImpl implements OAuth2AuthorizationService {

    private final Oauth2AuthorizationMapper oauth2AuthorizationMapper;

    private final RegisteredClientRepository registeredClientRepository;

    private final ObjectMapper objectMapper = SessionConfig.objectMapper();

    public Oauth2AuthorizationServiceImpl(Oauth2AuthorizationMapper oauth2AuthorizationMapper, RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(oauth2AuthorizationMapper, "oauth2AuthorizationMapper cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.oauth2AuthorizationMapper = oauth2AuthorizationMapper;
        this.registeredClientRepository = registeredClientRepository;

        ClassLoader classLoader = Oauth2AuthorizationServiceImpl.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        OAuth2Authorization existingAuthorization = findById(authorization.getId());
        if (existingAuthorization == null) {
            this.oauth2AuthorizationMapper.insert(toEntity(authorization));
        } else {
            this.oauth2AuthorizationMapper.updateById(toEntity(authorization));
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        this.oauth2AuthorizationMapper.deleteById(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return Optional.ofNullable(this.oauth2AuthorizationMapper.selectById(id))
                .map(this::toObject)
                .orElse(null);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");

        Oauth2AuthorizationDO result = null;
        if (tokenType == null) {
            result = this.oauth2AuthorizationMapper.selectByToken(token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            result = this.oauth2AuthorizationMapper.selectByState(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            result = this.oauth2AuthorizationMapper.selectByAuthorizationCode(token);
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            result = this.oauth2AuthorizationMapper.selectByAccessToken(token);
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            result = this.oauth2AuthorizationMapper.selectByRefreshToken(token);
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            result = this.oauth2AuthorizationMapper.selectByIdToken(token);
        } else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
            result = this.oauth2AuthorizationMapper.selectByUserCode(token);
        } else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
            result = this.oauth2AuthorizationMapper.selectByDeviceCode(token);
        }

        return Optional.ofNullable(result).map(this::toObject).orElse(null);
    }

    private OAuth2Authorization toObject(Oauth2AuthorizationDO entity) {
        RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + entity.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(resolveAuthorizationGrantType(entity.getAuthorizationGrantType()))
                .authorizedScopes(StringUtils.commaDelimitedListToSet(entity.getAuthorizedScopes()))
                .attributes(attributes -> attributes.putAll(parseMap(entity.getAttributes())));
        if (entity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }

        if (entity.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(),
                    entity.getAuthorizationCodeIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getAuthorizationCodeExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
            builder.token(authorizationCode, metadata -> metadata.putAll(parseMap(entity.getAuthorizationCodeMetadata())));
        }

        if (entity.getAccessTokenValue() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessTokenValue(),
                    entity.getAccessTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getAccessTokenExpiresAt().atZone(ZoneId.systemDefault()).toInstant(),
                    StringUtils.commaDelimitedListToSet(entity.getAccessTokenScopes()));
            builder.token(accessToken, metadata -> metadata.putAll(parseMap(entity.getAccessTokenMetadata())));
        }

        if (entity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getRefreshTokenExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
            builder.token(refreshToken, metadata -> metadata.putAll(parseMap(entity.getRefreshTokenMetadata())));
        }

        if (entity.getOidcIdTokenValue() != null) {
            OidcIdToken idToken = new OidcIdToken(
                    entity.getOidcIdTokenValue(),
                    entity.getOidcIdTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getOidcIdTokenExpiresAt().atZone(ZoneId.systemDefault()).toInstant(),
                    parseMap(entity.getOidcIdTokenClaims()));
            builder.token(idToken, metadata -> metadata.putAll(parseMap(entity.getOidcIdTokenMetadata())));
        }

        if (entity.getUserCodeValue() != null) {
            OAuth2UserCode userCode = new OAuth2UserCode(
                    entity.getUserCodeValue(),
                    entity.getUserCodeIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getUserCodeExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
            builder.token(userCode, metadata -> metadata.putAll(parseMap(entity.getUserCodeMetadata())));
        }

        if (entity.getDeviceCodeValue() != null) {
            OAuth2DeviceCode deviceCode = new OAuth2DeviceCode(
                    entity.getDeviceCodeValue(),
                    entity.getDeviceCodeIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getDeviceCodeExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
            builder.token(deviceCode, metadata -> metadata.putAll(parseMap(entity.getDeviceCodeMetadata())));
        }

        return builder.build();
    }

    private Oauth2AuthorizationDO toEntity(OAuth2Authorization authorization) {
        Oauth2AuthorizationDO entity = new Oauth2AuthorizationDO();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        entity.setAuthorizedScopes(StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ","));
        entity.setAttributes(writeMap(authorization.getAttributes()));
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        setTokenValues(
                authorizationCode,
                entity::setAuthorizationCodeValue,
                entity::setAuthorizationCodeIssuedAt,
                entity::setAuthorizationCodeExpiresAt,
                entity::setAuthorizationCodeMetadata
        );

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        setTokenValues(
                accessToken,
                entity::setAccessTokenValue,
                entity::setAccessTokenIssuedAt,
                entity::setAccessTokenExpiresAt,
                entity::setAccessTokenMetadata
        );
        if (accessToken != null && accessToken.getToken().getScopes() != null) {
            entity.setAccessTokenScopes(StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(), ","));
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        setTokenValues(
                refreshToken,
                entity::setRefreshTokenValue,
                entity::setRefreshTokenIssuedAt,
                entity::setRefreshTokenExpiresAt,
                entity::setRefreshTokenMetadata
        );

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        setTokenValues(
                oidcIdToken,
                entity::setOidcIdTokenValue,
                entity::setOidcIdTokenIssuedAt,
                entity::setOidcIdTokenExpiresAt,
                entity::setOidcIdTokenMetadata
        );
        if (oidcIdToken != null) {
            entity.setOidcIdTokenClaims(writeMap(oidcIdToken.getClaims()));
        }

        OAuth2Authorization.Token<OAuth2UserCode> userCode = authorization.getToken(OAuth2UserCode.class);
        setTokenValues(
                userCode,
                entity::setUserCodeValue,
                entity::setUserCodeIssuedAt,
                entity::setUserCodeExpiresAt,
                entity::setUserCodeMetadata
        );

        OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode = authorization.getToken(OAuth2DeviceCode.class);
        setTokenValues(
                deviceCode,
                entity::setDeviceCodeValue,
                entity::setDeviceCodeIssuedAt,
                entity::setDeviceCodeExpiresAt,
                entity::setDeviceCodeMetadata
        );

        return entity;
    }

    private void setTokenValues(
            OAuth2Authorization.Token<?> token,
            Consumer<String> tokenValueConsumer,
            Consumer<LocalDateTime> issuedAtConsumer,
            Consumer<LocalDateTime> expiresAtConsumer,
            Consumer<String> metadataConsumer) {
        if (token == null) {
            return;
        }
        OAuth2Token oAuth2Token = token.getToken();
        tokenValueConsumer.accept(oAuth2Token.getTokenValue());
        if (oAuth2Token.getIssuedAt() != null) {
            issuedAtConsumer.accept(LocalDateTime.ofInstant(oAuth2Token.getIssuedAt(), ZoneId.systemDefault()));
        }
        if (oAuth2Token.getExpiresAt() != null) {
            expiresAtConsumer.accept(LocalDateTime.ofInstant(oAuth2Token.getExpiresAt(), ZoneId.systemDefault()));
        }
        metadataConsumer.accept(writeMap(token.getMetadata()));
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> metadata) {
        try {
            return this.objectMapper.writeValueAsString(metadata);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}


