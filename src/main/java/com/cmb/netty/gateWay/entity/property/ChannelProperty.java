package com.cmb.netty.gateWay.entity.property;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

public class ChannelProperty {
    private ChannelId channelId;

    private Channel channel;

    public ChannelProperty() {}

    public ChannelProperty(ChannelId channelId, Channel channel) {
        this.channelId = channelId;
        this.channel = channel;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public void setChannelId(ChannelId channelId) {
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

}
