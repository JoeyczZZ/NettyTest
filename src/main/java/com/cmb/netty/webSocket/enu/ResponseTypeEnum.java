package com.cmb.netty.webSocket.enu;

public enum ResponseTypeEnum {
    DATA("data", "数据"),
    EXCEPTION("exception", "异常"),
    HEART_BEAR("hearBear", "心跳检测");

    private final String code;
    private final String name;

    ResponseTypeEnum(String code, String name) {
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
