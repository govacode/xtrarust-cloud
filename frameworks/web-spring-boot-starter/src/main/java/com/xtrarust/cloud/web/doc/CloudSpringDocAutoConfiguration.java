package com.xtrarust.cloud.web.doc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.utils.Constants;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

/**
 * Swagger 自动配置类
 *
 * @author gova
 */
@ConditionalOnClass({OpenAPI.class})
@AutoConfiguration(before = SpringDocConfiguration.class)
@EnableConfigurationProperties(SpringDocProperties.class)
@ConditionalOnProperty(name = Constants.SPRINGDOC_ENABLED, matchIfMissing = true)
public class CloudSpringDocAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI createApi(SpringDocProperties properties) {
        return new OpenAPI()
                .info(buildInfo(properties.getInfo()))
                .components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION, buildSecuritySchema(properties.getOAuth2())))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
    }

    private Info buildInfo(SpringDocProperties.InfoProperties infoProperties) {
        return new Info()
                .title(infoProperties.getTitle())
                .description(infoProperties.getDescription())
                .contact(infoProperties.getContact())
                .license(infoProperties.getLicense())
                .version(infoProperties.getVersion());
    }

    private SecurityScheme buildSecuritySchema(SpringDocProperties.OAuth2 oAuth2) {
        OAuthFlows oAuthFlows = new OAuthFlows();
        // 客户端模式
        Scopes scopes = new Scopes();
        oAuth2.getScopes().forEach(scope -> scopes.addString(scope, scope));
        OAuthFlow oAuthFlow = new OAuthFlow()
                .tokenUrl(oAuth2.getTokenUrl())
                .scopes(scopes);
        oAuthFlows.clientCredentials(oAuthFlow);

        return new SecurityScheme().flows(oAuthFlows).type(SecurityScheme.Type.OAUTH2);
    }

}

