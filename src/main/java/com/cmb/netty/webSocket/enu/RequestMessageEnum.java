package com.cmb.netty.webSocket.enu;

public enum RequestMessageEnum {
    ENT_CODE("entCode", "企业号"),
    TYPE("type", "类型"),
    CHANNEL_NAME("channelName", "通道名称"),
    DATA("data", "数据");

    private final String code;
    private final String name;

    RequestMessageEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
