package com.cmb.netty.gateWay;

import com.cmb.netty.gateWay.initializer.SomeOneClientInitializer;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SomeOneClient {
    private static final Logger log = LoggerFactory.getLogger(SomeOneClient.class.getName());
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final EventLoopGroup group = new NioEventLoopGroup();

    public static final boolean SSL = System.getProperty("ssl") != null;

    public static Channel channel;

    @Value("${netty.client.1.host}")
    private String host;

    @Value("${netty.client.1.port}")
    private int port;

    @PostConstruct
    public void start() throws Exception {
        new SomeOneClient().connect(host, port);
    }

    public void connect(String host, int port) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

//        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture future = bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new SomeOneClientInitializer(sslCtx))
                    .connect(new InetSocketAddress(host, port))
                    .sync();
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("SomeOneClient 绑定端口" + port + "成功!");
                } else {
                    log.error("SomeOneClient 尝试绑定端口" + port + "失败!", channelFuture.cause());
                }
            });
           channel = future.channel();
//        }
//        finally {
//            executorService.execute(() -> {
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                    try {
//                        connect("127.0.0.1", 8198);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
    }
}
