package com.cmb.netty.webSocket.dispatch;

import com.cmb.netty.webSocket.RequestMessage;
import io.netty.channel.ChannelHandlerContext;

public interface NettyBusinessLogicService {
    void logic(ChannelHandlerContext ctx, RequestMessage requestMessage);
}
