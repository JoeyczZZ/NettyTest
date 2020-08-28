package com.cmb.netty.webSocket.enu;

public enum MessageTypeEnum {
    DATA("data", "数据"),
    EXCEPTION("exception", "异常"),
    SPECIAL_GROUP("specialGroup", "Channel组分类"),
    HEART_BEAR("hearBear", "心跳检测"),
    SUCCESS("success", "成功"),
    FAIL("fail", "失败");

    private final String code;
    private final String name;

    MessageTypeEnum(String code, String name) {
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
