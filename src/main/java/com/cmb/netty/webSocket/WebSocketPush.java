package com.cmb.netty.webSocket;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.entity.message.ResponseMessage;
import com.cmb.netty.webSocket.entity.property.ChannelGroupProperty;
import com.cmb.netty.webSocket.entity.property.SpecialGroupProperty;
import com.cmb.netty.webSocket.entity.wrap.ChannelGroupLogWrap;
import com.cmb.netty.webSocket.enu.MessageTypeEnum;
import com.cmb.netty.webSocket.enu.WebSocketTypeEnum;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WebSocketPush {
    private static final Logger log = LoggerFactory.getLogger(WebSocketPush.class.getName());

    public static int pushData(String entCode, String dataType, WebSocketTypeEnum webSocketTypeEnum, Object data) {
        ChannelGroupProperty channelGroupProperty = ChannelGroupProperty.builder()
                .entCode(entCode)
                .type(webSocketTypeEnum.getType())
                .build();
        ChannelGroupLogWrap channels = WebSocketServerNetty.channelGroupMap.get(channelGroupProperty);
        if (null != channels) {
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .entCode(entCode)
                    .type(webSocketTypeEnum.getType())
                    .responseType(MessageTypeEnum.DATA.getCode())
                    .dataType(dataType)
                    .data(data)
                    .build();

            try {
                channels.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(responseMessage)));
            } catch (Exception e) {
                log.error("WebSocket推送失败！" + responseMessage, e);
                return 500;
            }
        }

        return 404;
    }

    public static int pushData(ChannelGroupProperty channelGroupProperty, String dataType, Object data) {
        ChannelGroupLogWrap channels = WebSocketServerNetty.channelGroupMap.get(channelGroupProperty);
        if (null != channels) {
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .entCode(channelGroupProperty.getEntCode())
                    .type(channelGroupProperty.getType())
                    .responseType(MessageTypeEnum.DATA.getCode())
                    .dataType(dataType)
                    .data(data)
                    .build();

            try {
                channels.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(responseMessage)));
            } catch (Exception e) {
                log.error("WebSocket推送失败！" + responseMessage, e);
                return 500;
            }
        }

        return 404;
    }

    public static int pushData(String entCode, String dataType, String webSocketTypeEnum, String data) {
        ChannelGroupProperty channelGroupProperty = ChannelGroupProperty.builder()
                .entCode(entCode)
                .type(webSocketTypeEnum)
                .build();
        ChannelGroupLogWrap channels = WebSocketServerNetty.channelGroupMap.get(channelGroupProperty);
        if (null != channels) {
            try {
                channels.writeAndFlush(new TextWebSocketFrame(data));
            } catch (Exception e) {
                log.error("WebSocket推送失败！" + data, e);
                return 500;
            }
        }

        return 404;
    }

    public static int pushSpecial(SpecialGroupProperty property, String dataType, Object data) {
        WebSocketServerNetty.specialChannelGroup.forEach((channelId, sgpAndChannelMap) -> {
            Channel channel = sgpAndChannelMap.get(property);
            if (null != channel) {
                ResponseMessage responseMessage = ResponseMessage.builder()
                        .entCode(property.getEntCode())
                        .type(property.getType())
                        .responseType(MessageTypeEnum.DATA.getCode())
                        .dataType(dataType)
                        .data(data)
                        .build();
                try {
                    channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(responseMessage)));
                } catch (Exception e) {
                    log.error("Push Special推送失败！" + responseMessage, e);
                }
            }
        });
        return 200;
    }

    public static int pushSpecial(String entCode, String label, String content, String dataType, Object data) {
        SpecialGroupProperty specialGroupProperty = SpecialGroupProperty.builder()
                .entCode(entCode)
                .label(label)
                .content(content)
                .build();
        return pushSpecial(specialGroupProperty, dataType, data);
    }

    public static int pushSpecial(String entCode, String label, List<String> contents, String dataType, Object data) {
        WebSocketServerNetty.specialChannelGroup.forEach((channelId, sgpAndChannelMap) -> {
            sgpAndChannelMap.forEach((sgp, channel) -> {
                if (null != channel) {
                    if (StringUtils.equals(sgp.getEntCode(), entCode) && StringUtils.equals(sgp.getLabel(), label)) {
                        if (contents.contains(sgp.getContent())) {
                            ResponseMessage responseMessage = ResponseMessage.builder()
                                    .entCode(entCode)
                                    .type(sgp.getType())
                                    .responseType(MessageTypeEnum.DATA.getCode())
                                    .dataType(dataType)
                                    .data(data)
                                    .build();
                            try {
                                channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(responseMessage)));
                            } catch (Exception e) {
                                log.error("Push Special推送失败！" + responseMessage, e);
                            }
                        }
                    }
                }
            });
        });
        return 200;
    }

    public static int pushSpecial(String label, List<String> contents, String dataType, Object data) {
        WebSocketServerNetty.specialChannelGroup.forEach((channelId, sgpAndChannelMap) -> {
            sgpAndChannelMap.forEach((sgp, channel) -> {
                if (null != channel) {
                    if (StringUtils.equals(sgp.getLabel(), label)) {
                        if (contents.contains(sgp.getContent())) {
                            ResponseMessage responseMessage = ResponseMessage.builder()
                                    .type(sgp.getType())
                                    .responseType(MessageTypeEnum.DATA.getCode())
                                    .dataType(dataType)
                                    .data(data)
                                    .build();
                            try {
                                channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(responseMessage)));
                            } catch (Exception e) {
                                log.error("Push Special推送失败！" + responseMessage, e);
                            }
                        }
                    }
                }
            });
        });
        return 200;
    }
}
