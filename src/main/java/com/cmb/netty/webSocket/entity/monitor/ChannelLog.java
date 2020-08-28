package com.cmb.netty.webSocket.entity.monitor;

public class ChannelLog {
    private String entCode;
    private String type;
    private String entity;
    private String content;
    private String responseType;
    private String id;

    public ChannelLog() {
    }

    public ChannelLog(String entCode, String type, String entity, String content, String responseType, String id) {
        this.entCode = entCode;
        this.type = type;
        this.entity = entity;
        this.content = content;
        this.responseType = responseType;
        this.id = id;
    }

    public ChannelLog(String entCode, String type, String entity, String content, String responseType) {
        this.entCode = entCode;
        this.type = type;
        this.entity = entity;
        this.content = content;
        this.responseType = responseType;
    }

    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
