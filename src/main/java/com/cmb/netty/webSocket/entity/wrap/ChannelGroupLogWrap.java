package com.cmb.netty.webSocket.entity.wrap;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.utils.StringUtils;
import com.cmb.netty.webSocket.WebSocketServerNetty;
import com.cmb.netty.webSocket.entity.monitor.ChannelLog;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ChannelGroupLogWrap {
    private ChannelGroup channelGroup;

    public ChannelGroupLogWrap(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    public String toString() {
        return "ChannelGroupLogWrap{" +
                "channelGroup=" + channelGroup +
                '}';
    }

    public boolean add(Channel e) {
        boolean r = channelGroup.add(e);
        String[] names = StringUtils.slit(channelGroup.name(), '-');
        ChannelLog channelLog = new ChannelLog(names[0], names[1], "Channel", e.toString(), "MONITOR", e.id().toString());
        try {
            ChannelGroupLogWrap channelGroupLogWrap = WebSocketServerNetty.channelGroupMap.getMonitorChannelGroup();
            if (null != channelGroupLogWrap) {
                channelGroupLogWrap.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(channelLog)));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return r;
    }

    public ChannelGroupFuture writeAndFlush(Object message) {
        return channelGroup.writeAndFlush(message);
    }

    public ChannelGroupFuture close() {
        return channelGroup.close();
    }
}
