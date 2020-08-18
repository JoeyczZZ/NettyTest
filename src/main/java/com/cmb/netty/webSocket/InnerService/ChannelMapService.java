package com.cmb.netty.webSocket.InnerService;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.ChannelProperty;
import com.cmb.netty.webSocket.RequestMessage;
import com.cmb.netty.webSocket.WebSocketServerNetty;
import com.cmb.netty.webSocket.dispatch.NettyBusinessLogic;
import com.cmb.netty.webSocket.dispatch.NettyBusinessLogicService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@NettyBusinessLogic(key = "aaaaChannelMap")
public class ChannelMapService implements NettyBusinessLogicService {
    private static final Logger log = LoggerFactory.getLogger(ChannelMapService.class.getName());

    @Override
    public void logic(ChannelHandlerContext ctx, RequestMessage requestMessage) {
        List<ChannelProperty> channelProperties = new ArrayList<>();
        WebSocketServerNetty.channelNameAndChannelMap.forEach((key, value) -> {
            channelProperties.add(value);
        });

        try {
            ctx.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(channelProperties)));
        } catch (Exception e) {
            log.error("aaaaChannelMap error", e);
        }
    }
}
