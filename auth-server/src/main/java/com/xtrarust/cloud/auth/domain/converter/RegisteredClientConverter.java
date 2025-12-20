package com.xtrarust.cloud.auth.domain.converter;

import com.xtrarust.cloud.auth.domain.entity.Oauth2RegisteredClientDO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegisteredClientConverter {

    /**
     * Oauth2RegisteredClientDO实体转授权服务器RegisteredClient
     *
     * @param entity Oauth2RegisteredClientDO实体
     * @return the RegisteredClient
     */
    public static RegisteredClient toRegisteredClient(Oauth2RegisteredClientDO entity) {
        Set<String> clientAuthenticationMethods = entity.getClientAuthenticationMethods() == null ?
                Collections.emptySet() : new LinkedHashSet<>(entity.getClientAuthenticationMethods());
        Set<String> authorizationGrantTypes = entity.getAuthorizationGrantTypes() == null ?
                Collections.emptySet() : new LinkedHashSet<>(entity.getAuthorizationGrantTypes());
        Set<String> redirectUris = entity.getRedirectUris() == null ?
                Collections.emptySet() : new LinkedHashSet<>(entity.getRedirectUris());
        Set<String> postLogoutRedirectUris = entity.getPostLogoutRedirectUris() == null ?
                Collections.emptySet() : new LinkedHashSet<>(entity.getPostLogoutRedirectUris());
        Set<String> clientScopes = entity.getScopes() == null ?
                Collections.emptySet() : new LinkedHashSet<>(entity.getScopes());

        RegisteredClient.Builder builder = RegisteredClient
                .withId(entity.getId())
                .clientId(entity.getClientId())
                .clientIdIssuedAt(entity.getClientIdIssuedAt().atZone(ZoneId.systemDefault()).toInstant())
                .clientSecret(entity.getClientSecret())
                .clientSecretExpiresAt(Objects.nonNull(entity.getClientSecretExpiresAt()) ?
                        entity.getClientSecretExpiresAt().atZone(ZoneId.systemDefault()).toInstant() : null)
                .clientName(entity.getClientName())
                .clientAuthenticationMethods(authenticationMethods -> {
                    Set<ClientAuthenticationMethod> methods = clientAuthenticationMethods.stream()
                            .map(RegisteredClientConverter::resolveClientAuthenticationMethod)
                            .collect(Collectors.toSet());
                    authenticationMethods.addAll(methods);
                })
                .authorizationGrantTypes(grantTypes -> {
                    Set<AuthorizationGrantType> types = authorizationGrantTypes.stream()
                            .map(RegisteredClientConverter::resolveAuthorizationGrantType)
                            .collect(Collectors.toSet());
                    grantTypes.addAll(types);
                })
                .redirectUris(uris -> uris.addAll(redirectUris))
                .postLogoutRedirectUris(uris -> uris.addAll(postLogoutRedirectUris))
                .scopes(scopes -> scopes.addAll(clientScopes));

        builder.clientSettings(resolveClientSettings(entity.getClientSettings()));
        builder.tokenSettings(resolveTokenSettings(entity.getTokenSettings()));
        return builder.build();
    }

    public static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        } else if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.DEVICE_CODE;
        }
        return new AuthorizationGrantType(authorizationGrantType);              // Custom authorization grant type
    }

    private static ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);      // Custom client authentication method
    }

    private static ClientSettings resolveClientSettings(Oauth2RegisteredClientDO.Oauth2ClientSettings settings) {
        ClientSettings.Builder builder = ClientSettings.builder()
                .requireProofKey(Boolean.TRUE.equals(settings.getRequireProofKey()))
                .requireAuthorizationConsent(Boolean.TRUE.equals(settings.getRequireAuthorizationConsent()));
        if (StringUtils.hasText(settings.getJwkSetUrl())) {
            builder.jwkSetUrl(settings.getJwkSetUrl());
        }
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.from(settings.getSigningAlgorithm());
        JwsAlgorithm jwsAlgorithm = signatureAlgorithm == null ? MacAlgorithm.from(settings.getSigningAlgorithm()) : signatureAlgorithm;
        if (jwsAlgorithm != null) {
            builder.tokenEndpointAuthenticationSigningAlgorithm(jwsAlgorithm);
        }
        return builder.build();
    }

    private static TokenSettings resolveTokenSettings(Oauth2RegisteredClientDO.Oauth2TokenSettings settings) {
        return TokenSettings.builder()
                .authorizationCodeTimeToLive(Optional.ofNullable(settings.getAuthorizationCodeTimeToLive()).orElse(Duration.ofMinutes(5)))
                .accessTokenTimeToLive(Optional.ofNullable(settings.getAccessTokenTimeToLive()).orElse(Duration.ofMinutes(30)))
                .accessTokenFormat(Optional.ofNullable(settings.getAccessTokenFormat())
                        .map(OAuth2TokenFormat::new)
                        .orElse(OAuth2TokenFormat.SELF_CONTAINED))
                .deviceCodeTimeToLive(Optional.ofNullable(settings.getDeviceCodeTimeToLive()).orElse(Duration.ofMinutes(5)))
                .reuseRefreshTokens(Boolean.TRUE.equals(settings.getReuseRefreshTokens()))
                .refreshTokenTimeToLive(Optional.ofNullable(settings.getRefreshTokenTimeToLive()).orElse(Duration.ofMinutes(60)))
                .idTokenSignatureAlgorithm(Optional.ofNullable(settings.getIdTokenSignatureAlgorithm())
                        .map(SignatureAlgorithm::from)
                        .orElse(SignatureAlgorithm.RS256))
                .build();
    }

    /**
     * 授权服务器RegisteredClient转Oauth2RegisteredClientDO实体
     *
     * @param registeredClient the RegisteredClient
     * @return Oauth2RegisteredClientDO实体
     */
    public static Oauth2RegisteredClientDO toEntity(RegisteredClient registeredClient) {

        Oauth2RegisteredClientDO entity = new Oauth2RegisteredClientDO();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        if (Objects.nonNull(registeredClient.getClientIdIssuedAt())) {
            entity.setClientIdIssuedAt(LocalDateTime.ofInstant(registeredClient.getClientIdIssuedAt(), ZoneId.systemDefault()));
        }
        entity.setClientSecret(registeredClient.getClientSecret());
        if (Objects.nonNull(registeredClient.getClientSecretExpiresAt())) {
            entity.setClientSecretExpiresAt(LocalDateTime.ofInstant(registeredClient.getClientSecretExpiresAt(), ZoneId.systemDefault()));
        }
        entity.setClientName(registeredClient.getClientName());

        List<String> clientAuthenticationMethods = registeredClient.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toList());
        entity.setClientAuthenticationMethods(clientAuthenticationMethods);
        List<String> authorizationGrantTypes = registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toList());
        entity.setAuthorizationGrantTypes(authorizationGrantTypes);
        entity.setRedirectUris(new ArrayList<>(registeredClient.getRedirectUris()));
        entity.setPostLogoutRedirectUris(new ArrayList<>(registeredClient.getPostLogoutRedirectUris()));
        entity.setScopes(new ArrayList<>(registeredClient.getScopes()));
        entity.setClientSettings(toOauth2ClientSettings(registeredClient.getClientSettings()));
        entity.setTokenSettings(toOauth2TokenSettings(registeredClient.getTokenSettings()));
        return entity;
    }

    private static Oauth2RegisteredClientDO.Oauth2ClientSettings toOauth2ClientSettings(ClientSettings clientSettings) {
        Oauth2RegisteredClientDO.Oauth2ClientSettings oauth2ClientSettings = new Oauth2RegisteredClientDO.Oauth2ClientSettings();
        oauth2ClientSettings.setRequireProofKey(clientSettings.isRequireProofKey());
        oauth2ClientSettings.setRequireAuthorizationConsent(clientSettings.isRequireAuthorizationConsent());
        oauth2ClientSettings.setJwkSetUrl(clientSettings.getJwkSetUrl());
        JwsAlgorithm algorithm;
        if (Objects.nonNull(algorithm = clientSettings.getTokenEndpointAuthenticationSigningAlgorithm())) {
            oauth2ClientSettings.setSigningAlgorithm(algorithm.getName());
        }
        return oauth2ClientSettings;
    }

    private static Oauth2RegisteredClientDO.Oauth2TokenSettings toOauth2TokenSettings(TokenSettings tokenSettings) {
        Oauth2RegisteredClientDO.Oauth2TokenSettings oauth2TokenSettings = new Oauth2RegisteredClientDO.Oauth2TokenSettings();
        oauth2TokenSettings.setAuthorizationCodeTimeToLive(tokenSettings.getAuthorizationCodeTimeToLive());
        oauth2TokenSettings.setAccessTokenTimeToLive(tokenSettings.getAccessTokenTimeToLive());
        oauth2TokenSettings.setAccessTokenFormat(tokenSettings.getAccessTokenFormat().getValue());
        oauth2TokenSettings.setDeviceCodeTimeToLive(tokenSettings.getDeviceCodeTimeToLive());
        oauth2TokenSettings.setReuseRefreshTokens(tokenSettings.isReuseRefreshTokens());
        oauth2TokenSettings.setRefreshTokenTimeToLive(tokenSettings.getRefreshTokenTimeToLive());
        oauth2TokenSettings.setIdTokenSignatureAlgorithm(tokenSettings.getIdTokenSignatureAlgorithm().getName());
        return oauth2TokenSettings;
    }
}
