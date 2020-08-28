package com.cmb.netty.gateWay.enu;

public enum MessageTypeEnum {
    BUSINESS_REQ((byte) 0, "业务请求消息", "0"),
    BUSINESS_RESP((byte) 1, "业务响应消息", "1"),
    BUSINESS_ONE_WAY((byte) 2, "业务ONEWAY消息", "2"),
    LOGIN_REQ((byte) 3, "握手请求消息", "3"),
    LOGIN_RESP((byte) 4, "握手应答消息", "4"),
    HEARTBEAT_REQ((byte) 5, "心跳请求消息", "5"),
    HEARTBEAT_RESP((byte) 6, "心跳应答消息", "6"),
    EXCEPTION((byte) 7, "异常消息", "7"),
    SUCCESS((byte) 8, "成功消息", "8"),
    REGISTER((byte) 9, "注册", "9");


    private final byte value;
    private final String name;
    private final String stringValue;

    MessageTypeEnum(byte value, String name, String stringValue) {
        this.value = value;
        this.name = name;
        this.stringValue = stringValue;
    }

    public byte getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getStringValue() {
        return stringValue;
    }
}
