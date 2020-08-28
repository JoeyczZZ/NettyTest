package com.cmb.netty.gateWay;

import com.cmb.netty.gateWay.initializer.SomeOneClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SomeOneClient {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final EventLoopGroup group = new NioEventLoopGroup();

    public static final boolean SSL = System.getProperty("ssl") != null;

    public static final ConcurrentHashMap<String, Channel> channelNameAndChannelMap = new ConcurrentHashMap<>();

    @Value("${netty.client.1.host}")
    private String host;

    @Value("${netty.client.1.port}")
    private int port;

    public static final List<Channel> channels = new ArrayList<>();

    public void connect(String host, int port) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture future = bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new SomeOneClientInitializer(sslCtx, channelNameAndChannelMap))
                    .connect(new InetSocketAddress(host, port))
                    .sync();
            future.channel().closeFuture().sync();
        }
        finally {
            executorService.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    try {
                        connect("127.0.0.1", 8198);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new SomeOneClient().connect("127.0.0.1", 8198);
    }
}
