package com.cmb.netty.webSocket.handler;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.entity.message.ResponseMessage;
import com.cmb.netty.webSocket.enu.MessageTypeEnum;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class HeartbeatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(HeartbeatHandler.class.getName());

    private static String RESPONSE_JSON;

    static {
        ResponseMessage responseMessage = ResponseMessage.builder()
                .responseType(MessageTypeEnum.HEART_BEAR.getCode())
                .build();
        try {
            RESPONSE_JSON = JsonUtils.toJson(responseMessage);
        } catch (Exception e) {
            log.error("心跳Response初始化失败 ", e);
        }
    }

    private static final TextWebSocketFrame HEARTBEAT = new TextWebSocketFrame(RESPONSE_JSON);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush(HEARTBEAT.copy())
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        ctx.fireChannelRead(msg.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ChannelId: " + ctx.channel().id().asLongText() + "抛出异常", cause);
        ctx.close();
    }
}
