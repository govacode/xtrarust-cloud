package com.govacode.designpatterns.creational.builder.v3;

import java.util.Map;

public class ProviderSettings extends AbstractSettings {

    protected ProviderSettings(Map<String, Object> settings) {
        super(settings);
    }

    public static Builder builder() {
        return new Builder().authorizationEndpoint("/oauth2/authorize").tokenEndpoint("/oauth2/token");
    }

    public static Builder withSettings(Map<String, Object> settings) {
        return new Builder().settings(s -> s.putAll(settings));
    }

    public static class Builder extends AbstractBuilder<ProviderSettings, Builder> {

        public Builder authorizationEndpoint(String authorizationEndpoint) {
            setting(ConfigurationSettingNames.AUTHORIZATION_ENDPOINT, authorizationEndpoint);
            return getThis();
        }

        public Builder tokenEndpoint(String tokenPoint) {
            setting(ConfigurationSettingNames.TOKEN_ENDPOINT, tokenPoint);
            return getThis();
        }

        @Override
        public ProviderSettings build() {
            return new ProviderSettings(getSettings());
        }
    }
}
