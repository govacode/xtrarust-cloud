package com.xtrarust.cloud.auth.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.xtrarust.cloud.auth.security.authentication.sms.MobileSmsCodeAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

public class MobileSmsCodeAuthenticationTokenDeserializer extends JsonDeserializer<MobileSmsCodeAuthenticationToken> {

    private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<List<GrantedAuthority>>() {};

    private static final TypeReference<Object> OBJECT = new TypeReference<>() {};

    @Override
    public MobileSmsCodeAuthenticationToken deserialize(JsonParser jp,
                                                        DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        MobileSmsCodeAuthenticationToken token = null;
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
        JsonNode principalNode = readJsonNode(jsonNode, "principal");
        Object principal = null;
        if (principalNode.isObject()) {
            principal = mapper.readValue(principalNode.traverse(mapper), Object.class);
        } else {
            principal = principalNode.asText();
        }
        JsonNode credentialsNode = readJsonNode(jsonNode, "credentials");
        Object credentials;
        if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
            credentials = null;
        } else {
            credentials = credentialsNode.asText();
        }
        List<GrantedAuthority> authorities = mapper.readValue(
                readJsonNode(jsonNode, "authorities").traverse(mapper), GRANTED_AUTHORITY_LIST);
        if (authenticated) {
            token = new MobileSmsCodeAuthenticationToken(principal, credentials, authorities);
        } else {
            token = new MobileSmsCodeAuthenticationToken(principal, credentials);
        }
        JsonNode detailsNode = readJsonNode(jsonNode, "details");
        if (detailsNode.isNull() || detailsNode.isMissingNode()) {
            token.setDetails(null);
        } else {
            Object details = mapper.readValue(detailsNode.toString(), OBJECT);
            token.setDetails(details);
        }
        return token;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
