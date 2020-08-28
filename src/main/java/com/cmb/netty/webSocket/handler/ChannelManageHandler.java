package com.cmb.netty.webSocket.handler;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.utils.StringUtils;
import com.cmb.netty.webSocket.entity.ChannelAttribute;
import com.cmb.netty.webSocket.entity.message.MessageUtil;
import com.cmb.netty.webSocket.entity.message.SpecialGroupMessage;
import com.cmb.netty.webSocket.entity.property.ChannelGroupProperty;
import com.cmb.netty.webSocket.entity.property.ChannelProperty;
import com.cmb.netty.webSocket.entity.property.SpecialGroupProperty;
import com.cmb.netty.webSocket.entity.wrap.ChannelGroupLogWrap;
import com.cmb.netty.webSocket.entity.wrap.ChannelGroupMapWrap;
import com.cmb.netty.webSocket.enu.MessageTypeEnum;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelManageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(ChannelManageHandler.class.getName());

    public  final ChannelGroupMapWrap channelGroupMap;
    private final ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap;
    private final ConcurrentHashMap<ChannelId, ConcurrentHashMap<SpecialGroupProperty, Channel>> specialChannelGroup;

    public ChannelManageHandler(ChannelGroupMapWrap channelGroupMap , ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap, ConcurrentHashMap<ChannelId, ConcurrentHashMap<SpecialGroupProperty, Channel>> specialChannelGroup) {
        this.channelGroupMap = channelGroupMap;
        this.channelNameAndChannelMap = channelNameAndChannelMap;
        this.specialChannelGroup = specialChannelGroup;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String message = msg.text();

        String requestType = null;
        String params = null;

        if (org.apache.commons.lang3.StringUtils.isNotBlank(message)) {
            try {
                JsonNode node = JsonUtils.jsonNode(message);
                if (node.has("requestType")) {
                    requestType = node.get("requestType").asText();
                }
                if (node.has("params")) {
                    params = node.get("params").asText();
                }
            } catch (JsonParseException e) {
                ctx.writeAndFlush(MessageUtil.FAIL.copy());
                return;
            }
        }

        if (MessageTypeEnum.SPECIAL_GROUP.toString().equals(requestType)) {
            if (StringUtils.isBlank(params)) {
                ctx.writeAndFlush(new TextWebSocketFrame("SPECIAL_GROUP 缺乏参数params"));
                return;
            }

            SpecialGroupMessage specialGroupMessage = null;
            try {
                specialGroupMessage = JsonUtils.fromJson(params, SpecialGroupMessage.class);
            } catch (Exception e) {
                log.error("SpecialGroupMessage : " + params + " Json转换失败!", e);
            }

            if (null != specialGroupMessage) {
                //获取channel属性
                Attribute<ChannelAttribute> attribute = ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText()));
                ChannelAttribute channelAttribute = attribute.get();
                String entCode = null;
                String type = null;
                if (null != channelAttribute) {
                    entCode = channelAttribute.getEntCode();
                    type = channelAttribute.getType();
                }

                ConcurrentHashMap<SpecialGroupProperty, Channel> map = specialChannelGroup.get(ctx.channel().id());

                if (null != map) {
                    if (specialGroupMessage.getReplace()) {
                        map.clear();
                    }

                    for (String content : specialGroupMessage.getContents()) {
                        SpecialGroupProperty specialGroupProperty = SpecialGroupProperty.builder()
                                .entCode(entCode)
                                .type(type)
                                .content(content)
                                .label(specialGroupMessage.getLabel())
                                .build();
                        map.put(specialGroupProperty, ctx.channel());
                    }
                    ctx.writeAndFlush(new TextWebSocketFrame(specialGroupMessage + " 添加成功! "));
                    return;
                }

                ConcurrentHashMap<SpecialGroupProperty, Channel> specialMap = new ConcurrentHashMap<>();
                for (String content : specialGroupMessage.getContents()) {
                    SpecialGroupProperty specialGroupProperty = SpecialGroupProperty.builder()
                            .entCode(entCode)
                            .type(type)
                            .content(content)
                            .label(specialGroupMessage.getLabel())
                            .build();
                    specialMap.put(specialGroupProperty, ctx.channel());
                }
                specialChannelGroup.put(ctx.channel().id(), specialMap);
                ctx.writeAndFlush(new TextWebSocketFrame(specialGroupMessage + " 添加成功! "));
                return;
            }
            ctx.writeAndFlush(new TextWebSocketFrame(message + " 添加失败! "));
            return;
        }

        ctx.fireChannelRead(msg.retain());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            Attribute<ChannelAttribute> attr = ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText()));
            ChannelAttribute channelAttribute = attr.get();

            if (StringUtils.isNotBlank(channelAttribute.getChannelName())) {
                log.info(channelNameAndChannelMap.toString());

                if (channelNameAndChannelMap.containsKey(channelAttribute.getChannelName())) {
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

            //ChannelGroup
            ChannelGroupLogWrap channelGroup = channelGroupMap.get(channelGroupProperty);
            if (null != channelGroup) {
                channelGroup.add(ctx.channel());
            } else {
                ChannelGroup newChannelGroup = StringUtils.isBlank(channelAttribute.getEntCode()) && StringUtils.isBlank(channelAttribute.getType()) ?
                        new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE) :
                        new DefaultChannelGroup(channelAttribute.getEntCode() + "-" + channelAttribute.getType(), ImmediateEventExecutor.INSTANCE);

                ChannelGroupLogWrap channelGroupLogWrap = new ChannelGroupLogWrap(newChannelGroup);
                newChannelGroup.add(ctx.channel());
                channelGroupMap.put(channelGroupProperty, channelGroupLogWrap);
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
            if (ctx.channel().id().asLongText().equals(channelProperty.getChannelId())) {
                channelNameAndChannelMap.remove(channelAttribute.getChannelName());
            }
        }
        specialChannelGroup.remove(ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


