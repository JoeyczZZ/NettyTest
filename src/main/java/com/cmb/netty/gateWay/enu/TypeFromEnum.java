package com.cmb.netty.gateWay.enu;

public enum TypeFromEnum {
    GPSOO("gpsoo");

    private final String name;

    TypeFromEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
