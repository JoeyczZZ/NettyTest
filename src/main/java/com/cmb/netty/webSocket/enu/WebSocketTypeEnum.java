package com.cmb.netty.webSocket.enu;

public enum WebSocketTypeEnum {
    GROUND_ENTITY_LAST_LOCATION("groundEntityLastLocation"),

    GROUND_WARNING_EVENT("groundWarningEvent");

    private final String type;

    WebSocketTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
