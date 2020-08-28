package com.cmb.netty.gateWay;

import com.cmb.netty.gateWay.enu.HttpClientURLEnum;
import com.cmb.netty.gateWay.initializer.HttpClient2InnerServerInitializer;
import com.cmb.netty.gateWay.initializer.HttpClient2ThirdPartyInitializer;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.net.URI;

@Component
public class HttpClient2InnerServer {
    private static final Logger log = LoggerFactory.getLogger(HttpClient2InnerServer.class.getName());

    private static final boolean SSL = System.getProperty("ssl") != null;
    //逻辑不可变对象
    private static SslContext sslCtx;

    public static final Cache<String, ChannelGroup> THIRD_PART_MAP = Caffeine.newBuilder().build();

    private static final EventLoopGroup group = new NioEventLoopGroup();
    public static final Bootstrap bootstrap = new Bootstrap();

    //逻辑不可变对象
    private static Channel toServerChannel;

    @Value("${netty.server.1.host}")
    private String server1Host;

    @Value("${netty.server.1.port}")
    private int server1Port;

    @PostConstruct
    public void start() throws Exception {
        // Configure SSL.
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new HttpClient2InnerServerInitializer(sslCtx));
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(server1Host, server1Port));
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("HttpClient2InnerServer 连接端口" + server1Port + "成功!");
            } else {
                log.error("HttpClient2InnerServer 尝试连接端口" + server1Port + "失败!", channelFuture.cause());
            }
        });
        toServerChannel = future.channel();

        for (HttpClientURLEnum enu : HttpClientURLEnum.values()) {
            connect(enu);
        }
    }

    public static void connect(HttpClientURLEnum httpClientURLEnum) throws Exception {
        URI uri = new URI(httpClientURLEnum.getUrl());
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }
        bootstrap.handler(new HttpClient2ThirdPartyInitializer(sslCtx, toServerChannel));
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        log.info("Create connect: " + httpClientURLEnum.getName() + " " + httpClientURLEnum.getUrl());

        ChannelGroup channelGroup = THIRD_PART_MAP.get(httpClientURLEnum.getName(), k ->
             new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));
        if (null != channelGroup) {
            channelGroup.add(future.channel());
        } else {
            log.warn("THIRD_PART_MAP " + httpClientURLEnum.getName() + " ChannelGroup is null");
        }
    }

    @PreDestroy
    public void destroy() {
        toServerChannel.close();
        group.shutdownGracefully();
    }

}
