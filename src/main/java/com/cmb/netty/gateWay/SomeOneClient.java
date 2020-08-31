package com.cmb.netty.gateWay;

import com.cmb.netty.gateWay.initializer.SomeOneClientInitializer;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

//@Component
public class SomeOneClient {
    private static final Logger log = LoggerFactory.getLogger(SomeOneClient.class.getName());

    private static final EventLoopGroup group = new NioEventLoopGroup();

    public static final boolean SSL = System.getProperty("ssl") != null;

    public static Cache<String, Channel> CHANNEL_MAP = Caffeine.newBuilder()
            .build();

    private static final Bootstrap bootstrap = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);

    @Value("${netty.client.1.host}")
    private String host;

    @Value("${netty.client.1.port}")
    private int port;

    @PostConstruct
    public void start() throws Exception {
        new SomeOneClient().connect(host, port);
    }

    public void connect(String host, int port) throws Exception {
        connect(host, port, "default" ,new ArrayList<>());
    }


    public void connect(String host, int port, String name, List<ChannelHandler> channelHandlers) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        ChannelFuture future = bootstrap.handler(new SomeOneClientInitializer(sslCtx, channelHandlers))
                .connect(new InetSocketAddress(host, port))
                .sync();
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("SomeOneClient 绑定端口" + port + "成功!");
            } else {
                log.error("SomeOneClient 尝试绑定端口" + port + "失败!", channelFuture.cause());
            }
        });

        CHANNEL_MAP.put(name, future.channel());
    }



    @PreDestroy
    public void destroy() {
        do {
            CHANNEL_MAP.asMap().forEach((k, v) -> {
            v.close();
            CHANNEL_MAP.invalidate(k);
        });
        } while (CHANNEL_MAP.asMap().size() > 0);


        group.shutdownGracefully();
    }
}
