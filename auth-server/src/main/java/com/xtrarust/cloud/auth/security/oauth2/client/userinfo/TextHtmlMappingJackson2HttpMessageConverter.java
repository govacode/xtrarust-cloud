package com.xtrarust.cloud.auth.security.oauth2.client.userinfo;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;

public class TextHtmlMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public TextHtmlMappingJackson2HttpMessageConverter() {
        setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML, new MediaType("application", "*+json")));
    }
}
