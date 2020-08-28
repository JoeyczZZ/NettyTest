package com.cmb.netty.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static final ObjectMapper getInstance() {
        return objectMapper;
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return getInstance().writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<?> parametrized, Class<?>... parameterClasses) throws JsonProcessingException {
        JavaType javaType = getInstance().getTypeFactory().constructParametricType(parametrized, parameterClasses);
        return getInstance().readValue(json, javaType);
    }

    public static JsonNode jsonNode(String json) throws JsonProcessingException {
        return getInstance().readTree(json);
    }
}
