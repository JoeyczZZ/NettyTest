package com.cmb.netty.webSocket.handler;

import com.cmb.netty.webSocket.ChannelAttribute;
import com.cmb.netty.webSocket.ChannelGroupProperty;
import com.cmb.netty.webSocket.ChannelProperty;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelManageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(ChannelManageHandler.class.getName());

    private final ConcurrentHashMap<ChannelGroupProperty, ChannelGroup> channelGroupMap;
    private final ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap;

    public ChannelManageHandler(ConcurrentHashMap<ChannelGroupProperty, ChannelGroup> channelGroupMap, ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap) {
        this.channelGroupMap = channelGroupMap;
        this.channelNameAndChannelMap = channelNameAndChannelMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        ctx.fireChannelRead(msg.retain());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            Attribute<ChannelAttribute> attr = ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText()));
            ChannelAttribute channelAttribute = attr.get();

            if (StringUtils.isNotBlank(channelAttribute.getChannelName())) {
                log.info(channelNameAndChannelMap.toString());

                if(channelNameAndChannelMap.containsKey(channelAttribute.getChannelName())) {
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("ChannelName: " + channelAttribute.getChannelName() + " 重复！"));
                    ctx.channel().close();
                    return;
                } else {
                    ChannelProperty channelProperty = ChannelProperty.builder()
                            .channelId(ctx.channel().id().asLongText())
                            .channel(ctx.channel())
                            .build();
                    channelNameAndChannelMap.put(channelAttribute.getChannelName(), channelProperty);
                }
            }

            ChannelGroupProperty channelGroupProperty = ChannelGroupProperty.builder()
                    .entCode(channelAttribute.getEntCode())
                    .type(channelAttribute.getType())
                    .build();

            ChannelGroup channelGroup = channelGroupMap.get(channelGroupProperty);
            if (null != channelGroup) {
                channelGroup.add(ctx.channel());
            } else {
                ChannelGroup newChannelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
                newChannelGroup.add(ctx.channel());
                channelGroupMap.put(channelGroupProperty, newChannelGroup);
            }

            ctx.pipeline().remove(HttpRequestHandler.class);

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Attribute<ChannelAttribute> attr = ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText()));
        ChannelAttribute channelAttribute = attr.get();
        if (null != channelAttribute && StringUtils.isNotBlank(channelAttribute.getChannelName())) {
            ChannelProperty channelProperty = channelNameAndChannelMap.get(channelAttribute.getChannelName());
            if (ctx.channel().id().asLongText().equals(channelProperty.getChannelId())){
                channelNameAndChannelMap.remove(channelAttribute.getChannelName());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


