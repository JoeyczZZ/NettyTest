package com.cmb.netty.gateway2;

import com.cmb.netty.gateway2.initializer.someClient.SomeClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Component
public class SomeClient {

    private static final Logger log = LoggerFactory.getLogger(SomeClient.class.getName());

    private static final EventLoopGroup group = new NioEventLoopGroup();

    public static final boolean SSL = System.getProperty("ssl") != null;

    private static final Bootstrap bootstrap = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);


    public static ChannelFuture connect(String host, int port) throws Exception {
        return connect(host, port ,new ArrayList<>());
    }


    public static ChannelFuture connect(String host, int port, List<ChannelHandler> channelHandlers) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        ChannelFuture future = bootstrap.handler(new SomeClientInitializer(sslCtx))
                .connect(new InetSocketAddress(host, port))
                .sync();
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("SomeClient 绑定端口" + port + "成功!");
            } else {
                log.error("SomeClient 尝试绑定端口" + port + "失败!", channelFuture.cause());
            }
        });

        return future;
    }

    @PreDestroy
    public void destroy() {
        group.shutdownGracefully();
    }

}
