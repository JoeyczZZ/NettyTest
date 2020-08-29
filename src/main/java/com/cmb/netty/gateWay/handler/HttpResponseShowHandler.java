package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseShowHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger log = LoggerFactory.getLogger(HttpResponseShowHandler.class.getName());

    private final Channel toServerChannel;
    private final String from;

    public HttpResponseShowHandler(Channel toServerChannel, String from) {
        this.toServerChannel = toServerChannel;
        this.from = from;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        if (null != toServerChannel && toServerChannel.isActive()) {
            toServerChannel.writeAndFlush(NettyMessageUtils.buildFromRMessage(from, msg.content().toString()));
        } else {
            log.warn("HttpResponseShowHandler 发送消息失败");
        }
    }
}
