package com.xtrarust.cloud.auth.service.impl;

import com.xtrarust.cloud.auth.domain.entity.Oauth2AuthorizationConsentDO;
import com.xtrarust.cloud.auth.repository.mapper.Oauth2AuthorizationConsentMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * https://docs.spring.io/spring-authorization-server/docs/1.0.4/reference/html/guides/how-to-jpa.html#authorization-consent-service
 */
@Service
public class Oauth2AuthorizationConsentServiceImpl implements OAuth2AuthorizationConsentService {

    private final Oauth2AuthorizationConsentMapper oauth2AuthorizationConsentMapper;

    private final RegisteredClientRepository registeredClientRepository;

    public Oauth2AuthorizationConsentServiceImpl(Oauth2AuthorizationConsentMapper oauth2AuthorizationConsentMapper,
                                                 RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(oauth2AuthorizationConsentMapper, "oauth2AuthorizationConsentMapper cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.oauth2AuthorizationConsentMapper = oauth2AuthorizationConsentMapper;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        OAuth2AuthorizationConsent existingAuthorizationConsent = findById(
                authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
        if (existingAuthorizationConsent == null) {
            this.oauth2AuthorizationConsentMapper.insert(toEntity(authorizationConsent));
        } else {
            // Spring授权服务器 registeredClientId与principalName是联合主键 Oauth2AuthorizationConsentDO没用联合主键
            this.oauth2AuthorizationConsentMapper.updateByRegisteredClientIdAndPrincipalName(toEntity(authorizationConsent));
        }
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        this.oauth2AuthorizationConsentMapper.deleteByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        Oauth2AuthorizationConsentDO entity = this.oauth2AuthorizationConsentMapper.selectByRegisteredClientIdAndPrincipalName(
                registeredClientId, principalName);
        return Optional.ofNullable(entity).map(this::toObject).orElse(null);
    }

    private OAuth2AuthorizationConsent toObject(Oauth2AuthorizationConsentDO entity) {
        String registeredClientId = entity.getRegisteredClientId();
        RegisteredClient registeredClient = this.registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + registeredClientId + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(registeredClientId, entity.getPrincipalName());
        if (entity.getAuthorities() != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(entity.getAuthorities())) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }
        return builder.build();
    }

    private Oauth2AuthorizationConsentDO toEntity(OAuth2AuthorizationConsent authorizationConsent) {
        Oauth2AuthorizationConsentDO entity = new Oauth2AuthorizationConsentDO();
        entity.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        entity.setPrincipalName(authorizationConsent.getPrincipalName());

        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        entity.setAuthorities(StringUtils.collectionToCommaDelimitedString(authorities));
        return entity;
    }
}

