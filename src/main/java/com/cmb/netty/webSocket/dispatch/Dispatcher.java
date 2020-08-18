package com.cmb.netty.webSocket.dispatch;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Dispatcher {
    private static final Dispatcher dispatcher = new Dispatcher();
    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class.getName());

    public Dispatcher() {}

    public static Dispatcher getInstance() {
        return dispatcher;
    }

    private static final Map<String, Object> routeTable = new ConcurrentHashMap<>();

    public void dispatch(ChannelHandlerContext ctx, String message){
        RequestMessage requestMessage = null;
        try {
            requestMessage = JsonUtils.fromJson(message, RequestMessage.class);
        } catch (Exception e) {
            log.error("WebSocketServerNetty Dispatcher === Message: " + message + " Json转换失败!", e);
        }
        if (null != requestMessage) {
            NettyBusinessLogicService service = (NettyBusinessLogicService) routeTable.get(requestMessage.getType());
            service.logic(ctx, requestMessage);
        }
    }

    public void setRoute(Map<String, Object> route) {
        if (route != null && route.size() > 0) {
            for (Map.Entry<String, Object> entry : route.entrySet()) {
                routeTable.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
