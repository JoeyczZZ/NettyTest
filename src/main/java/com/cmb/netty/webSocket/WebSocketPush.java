package com.cmb.netty.webSocket;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.enu.ResponseTypeEnum;
import com.cmb.netty.webSocket.enu.WebSocketTypeEnum;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketPush {
    private static final Logger log = LoggerFactory.getLogger(WebSocketPush.class.getName());

    public static int pushData(String entCode, String dataType, WebSocketTypeEnum webSocketTypeEnum, Object data) {
        ChannelGroupProperty channelGroupProperty = ChannelGroupProperty.builder()
                .entCode(entCode)
                .type(webSocketTypeEnum.getType())
                .build();
        ChannelGroup channels = WebSocketServerNetty.channelGroupMap.get(channelGroupProperty);
        if (null != channels) {
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .entCode(entCode)
                    .type(webSocketTypeEnum.getType())
                    .responseType(ResponseTypeEnum.DATA.getCode())
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
        ChannelGroup channels = WebSocketServerNetty.channelGroupMap.get(channelGroupProperty);
        if (null != channels) {
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .entCode(channelGroupProperty.getEntCode())
                    .type(channelGroupProperty.getType())
                    .responseType(ResponseTypeEnum.DATA.getCode())
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
}
