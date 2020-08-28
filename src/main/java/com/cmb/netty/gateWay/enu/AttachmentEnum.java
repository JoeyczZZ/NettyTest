package com.cmb.netty.gateWay.enu;

public enum AttachmentEnum {
    HTTP_CLIENT_2_INNER_SERVER("from", "httpClient2InnerServer"),

    CONNECT("connect", "");

    private final String key;
    private final String value;

    AttachmentEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }
}
