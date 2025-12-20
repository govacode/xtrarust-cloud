package com.govacode.designpatterns.creational.builder.v3;

/**
 * {@link AbstractSettings} and {@link ProviderSettings} copy from Spring Authorization Server 0.2.3
 *
 * @author gova
 */
public class Client {

    public static void main(String[] args) {
        ProviderSettings settings = ProviderSettings.builder()
                .authorizationEndpoint("/oauth/authorize")
                .tokenEndpoint("/oauth/token")
                .build();

        System.out.println((String) settings.getSetting(ConfigurationSettingNames.AUTHORIZATION_ENDPOINT));
    }
}
