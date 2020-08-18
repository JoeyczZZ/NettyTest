package com.cmb.netty.webSocket;

import com.cmb.netty.webSocket.dispatch.NettyBusinessLogic;
import com.cmb.netty.webSocket.dispatch.NettyBusinessLogicService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@NettyBusinessLogic(key = "channelNameAndIdMap")
public class TestService implements NettyBusinessLogicService {

    @Override
    public void logic(ChannelHandlerContext ctx, RequestMessage requestMessage) {
        ctx.writeAndFlush(new TextWebSocketFrame(WebSocketServerNetty.channelNameAndChannelMap.toString()));
    }
}
