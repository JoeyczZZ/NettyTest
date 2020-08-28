package com.cmb.netty.webSocket.enu;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum HttpClientInChannelTypeEnum {
    GROUND_BIG_SCREEN_PUSH("groundBigScreenPush");

    private static final Map<String, HttpClientInChannelTypeEnum> map = Arrays.stream(HttpClientInChannelTypeEnum.values())
            .collect(Collectors.toMap(HttpClientInChannelTypeEnum::getType, v -> v));

    private final String type;

    HttpClientInChannelTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static boolean contains(String type) {
        return map.containsKey(type);
    }
}
