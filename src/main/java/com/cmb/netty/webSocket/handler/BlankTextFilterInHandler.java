package com.cmb.netty.webSocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;

public class BlankTextFilterInHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        if (StringUtils.isBlank(msg.text())) {
            return;
        }
        System.out.println(ctx.pipeline().toMap());
        ctx.fireChannelRead(msg.retain());
    }
}



