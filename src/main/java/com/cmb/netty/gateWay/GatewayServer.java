package com.cmb.netty.gateWay;

import com.cmb.netty.gateWay.initializer.GateWayServerInitializer;
import com.cmb.netty.webSocket.WebSocketServerNetty;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

@Component
public class GatewayServer {
    private static final int PORT = 8198;
    private static final Logger log = LoggerFactory.getLogger(WebSocketServerNetty.class.getName());

    public static final boolean SSL = System.getProperty("ssl") != null;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static final Cache<String, ChannelGroup> GS_REGISTER_CHANNEL_GROUP_CACHE = Caffeine.newBuilder().build();
    private static final ChannelGroup innerChannel = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    @PostConstruct
    public void start() throws CertificateException, SSLException {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new GateWayServerInitializer(sslCtx, GS_REGISTER_CHANNEL_GROUP_CACHE, innerChannel));

        ChannelFuture future = b.bind(new InetSocketAddress(PORT));
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("GatewayServer 绑定端口" + PORT + "成功!");
            } else {
                log.error("GatewayServer 尝试绑定端口" + PORT + "失败!", channelFuture.cause());
            }
        });
        future.syncUninterruptibly();
    }

    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("GateWayServer 关闭成功!");
    }
}
