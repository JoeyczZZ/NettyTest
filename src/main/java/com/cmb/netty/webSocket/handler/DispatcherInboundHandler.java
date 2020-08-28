package com.cmb.netty.webSocket.handler;

import com.cmb.netty.utils.StringUtils;
import com.cmb.netty.webSocket.dispatch.Dispatcher;
import com.cmb.netty.webSocket.entity.ChannelAttribute;
import com.cmb.netty.webSocket.enu.HttpClientInChannelTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class DispatcherInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final Dispatcher dispatcher = Dispatcher.getInstance();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        String text = msg.text();
        dispatcher.dispatch(ctx, text);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            Attribute<ChannelAttribute> attr = ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText()));
            ChannelAttribute channelAttribute = attr.get();
            if (null != channelAttribute && StringUtils.isNotBlank(channelAttribute.getType())) {
                if (HttpClientInChannelTypeEnum.GROUND_BIG_SCREEN_PUSH.getType().equals(channelAttribute.getType())) {
                    ctx.pipeline().addLast(new ChannelClientHandler("http://api.gpsoo.net/1/account/monitor?access_token=0011045701369822736adb020814946df1ded1c8681d026d5c5&map_type=BAIDU&target=mycar&account=testacc&time=1366786321"));
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
