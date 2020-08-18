package com.cmb.netty.webSocket.dispatch;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface NettyBusinessLogic {
    String key();
}
