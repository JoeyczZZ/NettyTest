package com.cmb.netty;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.entity.message.ResponseMessage;
import com.cmb.netty.webSocket.enu.MessageTypeEnum;
import org.junit.jupiter.api.Test;

public class JsonUtilsTest {
    @Test
    public void toJson() {
        ResponseMessage responseMessage = ResponseMessage.builder()
                .responseType(MessageTypeEnum.HEART_BEAR.getCode())
                .build();

        try {
            String s = JsonUtils.toJson(responseMessage);
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void aa() {

        System.out.println("GET "+"GET".hashCode());
        System.out.println("POST "+"POST".hashCode());
        System.out.println("PUT "+"PUT".hashCode());
        System.out.println("HEAD "+"HEAD".hashCode());
        System.out.println("OPTIONS "+"OPTIONS".hashCode());
        System.out.println("PATCH "+"PATCH".hashCode());
        System.out.println("DELETE "+"DELETE".hashCode());
        System.out.println("TRACE "+"TRACE".hashCode());
        System.out.println("CONNECT "+"CONNECT".hashCode());
    }
}
