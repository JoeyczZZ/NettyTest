package com.cmb.netty.webSocket.entity.message;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.enu.MessageTypeEnum;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtil {
    private static final Logger log = LoggerFactory.getLogger(MessageUtil.class.getName());

    private static String SUCCESS_JSON;
    private static String RESPONSE_JSON;
    private static String FAIL_JSON;

    static {
        HeartBearMessage heartBearMessage = HeartBearMessage.builder()
                .responseType(MessageTypeEnum.HEART_BEAR.toString())
                .build();
        CommandResponseMessage success = CommandResponseMessage.builder()
                .responseType(MessageTypeEnum.SUCCESS.toString())
                .build();
        CommandResponseMessage fail = CommandResponseMessage.builder()
                .responseType(MessageTypeEnum.FAIL.toString())
                .build();
        try {
            RESPONSE_JSON = JsonUtils.toJson(heartBearMessage);
            SUCCESS_JSON = JsonUtils.toJson(success);
            FAIL_JSON = JsonUtils.toJson(fail);
        } catch (Exception e) {
            log.error("心跳Response初始化失败 ", e);
        }
    }
    public static final TextWebSocketFrame SUCCESS = new TextWebSocketFrame(SUCCESS_JSON);
    public static final TextWebSocketFrame FAIL = new TextWebSocketFrame(FAIL_JSON);
}
