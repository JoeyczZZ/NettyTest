package com.cmb.netty.webSocket;

public class ChannelAttribute {
    private String channelName;

    private String entCode;

    private String type;

    public ChannelAttribute() {
    }

    private ChannelAttribute(String channelName, String entCode, String type) {
        this.channelName = channelName;
        this.entCode = entCode;
        this.type = type;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String channelName;

        private String entCode;

        private String type;

        public Builder() {
        }

        public Builder channelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public Builder entCode(String entCode) {
            this.entCode = entCode;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public ChannelAttribute build() {
            return new ChannelAttribute(channelName, entCode, type);
        }
    }
}
