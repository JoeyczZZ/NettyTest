package com.cmb.netty.gateway2.enu;

public enum AttachmentEnum {
    REQUEST_ID("requestId");

    private final String name;


    AttachmentEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
