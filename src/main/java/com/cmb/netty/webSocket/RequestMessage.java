package com.cmb.netty.webSocket;

public class RequestMessage {
    private String entCode;

    private String type;

    private String channelName;

    private String params;

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

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public ChannelGroupProperty buildChannelGroupProperty() {
        return ChannelGroupProperty.builder()
                .entCode(entCode)
                .type(type)
                .build();
    }

    private RequestMessage(String entCode, String type, String channelName, String params) {
        this.entCode = entCode;
        this.type = type;
        this.channelName = channelName;
        this.params = params;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String entCode;

        private String type;

        private String channelName;

        private String params;

        public Builder() {
        }

        public Builder entCode(String entCode) {
            this.entCode = entCode;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder channelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public Builder params(String params) {
            this.params = params;
            return this;
        }

        public RequestMessage build() {
            return new RequestMessage(entCode, type, channelName, params);
        }
    }
}
