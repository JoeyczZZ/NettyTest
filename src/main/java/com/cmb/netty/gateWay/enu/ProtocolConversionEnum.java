package com.cmb.netty.gateWay.enu;

public enum ProtocolConversionEnum {
    PROTOCOL("protocol"),
    HTTP("http"),
    HTTP_CONNECT("http_connect");

    private final String code;

    ProtocolConversionEnum(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
