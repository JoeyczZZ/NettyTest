package com.cmb.netty.webSocket.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Component
public class DispatcherRouteTableLoad implements ApplicationListener<ApplicationStartedEvent> {
    private final static Logger log = LoggerFactory.getLogger(DispatcherRouteTableLoad.class.getName());

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> nettyBusinessLogicMap = event.getApplicationContext().getBeansWithAnnotation(NettyBusinessLogic.class);
        for (Map.Entry<String, Object> entry : nettyBusinessLogicMap.entrySet()) {
            Object object = entry.getValue();
            Class c = object.getClass();
            log.info("Load NettyBusinessLogic ========== " + c);

            Annotation[] annotations = c.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(NettyBusinessLogic.class)) {
                    NettyBusinessLogic nettyBusinessLogic = (NettyBusinessLogic) annotation;
                    map.put(nettyBusinessLogic.key(), object);
                }
            }

            Dispatcher dispatcher = (Dispatcher) event.getApplicationContext().getBean("dispatcher");
            dispatcher.setRoute(map);
        }
    }
}
