package com.cmb.netty.webSocket.enu;

public enum MonitorTypeEnum {
    MONITOR_9001("@@mon9001", "");
    private final String code;
    private final String name;

    MonitorTypeEnum(String code, String name) {
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
