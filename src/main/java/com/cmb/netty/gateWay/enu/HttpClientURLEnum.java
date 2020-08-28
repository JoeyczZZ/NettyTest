package com.cmb.netty.gateWay.enu;

public enum HttpClientURLEnum {
    GPSOO("gpsoo", "http://127.0.0.1:8081/");  //http://api.gpsoo.net/1/

    private final String name;
    private final String url;

    HttpClientURLEnum(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
