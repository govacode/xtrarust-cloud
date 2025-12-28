package com.xtrarust.cloud.common.util.json;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xtrarust.cloud.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * JSON 工具类
 */
@Slf4j
public class JacksonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略 null 值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Long转String 避免前端js精度丢失
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        // 解决 LocalDateTime 的序列化
        objectMapper.registerModules(new JavaTimeModule());
    }

    /**
     * 初始化 objectMapper 属性
     * <p>
     * 通过这样的方式，使用 Spring 创建的 ObjectMapper Bean
     *
     * @param objectMapper ObjectMapper 对象
     */
    public static void init(ObjectMapper objectMapper) {
        JacksonUtils.objectMapper = objectMapper;
    }

    public static String toJsonString(Object object) {
        if (Objects.isNull(object)) return null;
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonPrettyString(Object object) {
        if (Objects.isNull(object)) return null;
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) return null;
        try {
            return objectMapper.readValue(text, clazz);
        } catch (IOException e) {
            log.error("json parse error, json: {}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, String path, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) return null;
        try {
            JsonNode jsonNode = objectMapper.readTree(text);
            JsonNode node = jsonNode.path(path);
            return objectMapper.readValue(node.toString(), clazz);
        } catch (IOException e) {
            log.error("json parse error, json: {}, path: {}", text, path, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, Type type) {
        if (StringUtils.isEmpty(text)) return null;
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructType(type));
        } catch (IOException e) {
            log.error("json parse error, json: {}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtil.isEmpty(bytes)) return null;
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("json parse error, json bytes: {}", Arrays.toString(bytes), e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(text)) return null;
        try {
            return objectMapper.readValue(text, typeReference);
        } catch (IOException e) {
            log.error("json parse error, json: {}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static Dict parseMap(String text) {
        if (StringUtils.isEmpty(text)) return null;
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructType(Dict.class));
        } catch (MismatchedInputException e) {
            // 类型不匹配说明不是json
            return null;
        } catch (IOException e) {
            log.error("json parse error, json: {}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static List<Dict> parseArrayMap(String text) {
        if (StringUtils.isEmpty(text)) return null;
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, Dict.class));
        } catch (IOException e) {
            log.error("json parse error, json: {}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("json parse error, json: {}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseArray(String text, String path, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(text);
            JsonNode node = jsonNode.path(path);
            return objectMapper.readValue(node.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("json parse error, json: {}, path: {}", text, path, e);
            throw new RuntimeException(e);
        }
    }

    public static JsonNode parseTree(String text) {
        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            log.error("json parse error, json: {}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static JsonNode parseTree(byte[] bytes) {
        try {
            return objectMapper.readTree(bytes);
        } catch (IOException e) {
            log.error("json parse error, json: {}", Arrays.toString(bytes), e);
            throw new RuntimeException(e);
        }
    }

    public static boolean isJson(String text) {
        if (StringUtils.isEmpty(text)) return false;
        try {
            JsonNode jsonNode = objectMapper.readTree(text);
            return jsonNode.isObject() || jsonNode.isArray();
        } catch (IOException e) {
            log.error("json parse error, json: {}", text, e);
            return false;
        }
    }

}
