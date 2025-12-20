package com.xtrarust.cloud.auth.security.jackson2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "cause", "stackTrace" })
public class NeedMfaExceptionMixin {

    @JsonCreator
    NeedMfaExceptionMixin(@JsonProperty("message") String message) {
    }
}
