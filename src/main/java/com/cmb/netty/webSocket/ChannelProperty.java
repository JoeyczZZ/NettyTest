package com.cmb.netty.webSocket;

import io.netty.channel.Channel;

public class ChannelProperty {
    private String channelId;

    private Channel channel;

    public ChannelProperty() {}

    private ChannelProperty(String channelId, Channel channel) {
        this.channelId = channelId;
        this.channel = channel;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "ChannelProperty{" +
                "channelId='" + channelId + '\'' +
                ", channel=" + channel +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String channelId;

        private Channel channel;

        public Builder() {
        }

        public Builder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder channel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public ChannelProperty build() {
            return new ChannelProperty(channelId, channel);
        }
    }
}
