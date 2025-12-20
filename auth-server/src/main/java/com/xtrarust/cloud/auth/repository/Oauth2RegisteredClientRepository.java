package com.xtrarust.cloud.auth.repository;

import com.xtrarust.cloud.auth.domain.converter.RegisteredClientConverter;
import com.xtrarust.cloud.auth.repository.mapper.Oauth2RegisteredClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

/**
 * Spring授权服务器{@link RegisteredClientRepository}实现<br>
 * The RegisteredClientRepository is the central component where new clients can be registered and existing clients can be queried. <br>
 *
 * <a href="https://docs.spring.io/spring-authorization-server/reference/guides/how-to-jpa.html">How-to: Implement core services with JPA</a>
 */
@Component
@RequiredArgsConstructor
public class Oauth2RegisteredClientRepository implements RegisteredClientRepository {

    private final Oauth2RegisteredClientMapper oauth2RegisteredClientMapper;

    @Override
    public void save(RegisteredClient registeredClient) {
        // 更新逻辑
        // 如PasswordEncoder配置的是`DelegatingPasswordEncoder` 初始化密码是noop不加密的
        // 在ClientSecretAuthenticationProvider做客户端认证时发现需要升级加密方式为bcrypt
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        RegisteredClient existingRegisteredClient = findById(registeredClient.getId());
        if (Objects.nonNull(existingRegisteredClient)) {
            oauth2RegisteredClientMapper.updateById(RegisteredClientConverter.toEntity(registeredClient));
        } else {
            oauth2RegisteredClientMapper.insert(RegisteredClientConverter.toEntity(registeredClient));
        }
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return Optional.ofNullable(oauth2RegisteredClientMapper.selectById(id))
                .map(RegisteredClientConverter::toRegisteredClient)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        return Optional.ofNullable(this.oauth2RegisteredClientMapper.selectByClientId(clientId))
                .map(RegisteredClientConverter::toRegisteredClient)
                .orElse(null);
    }

}
