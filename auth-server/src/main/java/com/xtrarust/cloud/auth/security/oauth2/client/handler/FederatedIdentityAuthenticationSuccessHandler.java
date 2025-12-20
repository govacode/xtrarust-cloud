package com.xtrarust.cloud.auth.security.oauth2.client.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * 自定义 Oauth2 Login 认证成功处理器<br>
 *
 * An {@link AuthenticationSuccessHandler} for capturing the {@link OidcUser} or {@link OAuth2User}
 * for Federated Account Linking or JIT Account Provisioning.
 *
 * @author gova
 */
@Setter
public class FederatedIdentityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private AuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();

    private BiConsumer<String, OAuth2User> oauth2UserHandler;

    private BiConsumer<String, OidcUser> oidcUserHandler = (clientRegistrationId, user) -> this.oauth2UserHandler.accept(clientRegistrationId, user);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            String clientRegistrationId = token.getAuthorizedClientRegistrationId();

            if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
                this.oidcUserHandler.accept(clientRegistrationId, oidcUser);
            } else if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
                this.oauth2UserHandler.accept(clientRegistrationId, oauth2User);
            }
        }

        this.delegate.onAuthenticationSuccess(request, response, authentication);
    }

}
