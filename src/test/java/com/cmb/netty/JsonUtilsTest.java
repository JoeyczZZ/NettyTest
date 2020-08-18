package com.cmb.netty;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.ResponseMessage;
import com.cmb.netty.webSocket.enu.ResponseTypeEnum;
import org.junit.jupiter.api.Test;

public class JsonUtilsTest {
    @Test
    public void toJson() {
        ResponseMessage responseMessage = ResponseMessage.builder()
                .responseType(ResponseTypeEnum.HEART_BEAR.getCode())
                .build();

        try {
            String s = JsonUtils.toJson(responseMessage);
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
