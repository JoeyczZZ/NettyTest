package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseShowHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger log = LoggerFactory.getLogger(HttpResponseShowHandler.class.getName());

    private final Channel toServerChannel;

    public HttpResponseShowHandler(Channel toServerChannel) {
        this.toServerChannel = toServerChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        if (null != toServerChannel && toServerChannel.isActive()) {
            toServerChannel.writeAndFlush(NettyMessageUtils.buildFromRMessage("gpsoo", msg.content().toString()));
        } else {
            log.warn("HttpResponseShowHandler 发送消息失败");
        }
    }
}
