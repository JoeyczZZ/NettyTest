package com.cmb.netty.webSocket;

import com.cmb.netty.webSocket.dispatch.NettyBusinessLogic;
import com.cmb.netty.webSocket.dispatch.NettyBusinessLogicService;
import com.cmb.netty.webSocket.entity.message.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@NettyBusinessLogic(key = "specialChannelGroup")
public class TestService implements NettyBusinessLogicService {

    @Override
    public void logic(ChannelHandlerContext ctx, RequestMessage requestMessage) {
        ctx.writeAndFlush(new TextWebSocketFrame(WebSocketServerNetty.specialChannelGroup.toString()));
    }
}
