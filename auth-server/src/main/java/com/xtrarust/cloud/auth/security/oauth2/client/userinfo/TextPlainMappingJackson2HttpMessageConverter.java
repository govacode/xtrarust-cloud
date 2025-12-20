package com.xtrarust.cloud.auth.security.oauth2.client.userinfo;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;

public class TextPlainMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public TextPlainMappingJackson2HttpMessageConverter() {
        setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, new MediaType("application", "*+json")));
    }
}
