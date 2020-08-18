package com.cmb.netty.webSocket;

import io.netty.channel.Channel;

public class WebSocketNettyUtil {
    public static Channel channel(String channelName) {
        return WebSocketServerNetty.channelNameAndChannelMap.get(channelName).getChannel();
    }
}
