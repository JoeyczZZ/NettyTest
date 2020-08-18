package com.cmb.netty.webSocket;

import com.cmb.netty.webSocket.initializer.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketServerNetty implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {
    private static final int PORT = 8199;
    private static final Logger log = LoggerFactory.getLogger(WebSocketServerNetty.class.getName());

    public static final boolean SSL = System.getProperty("ssl") != null;

    public static final ConcurrentHashMap<ChannelGroupProperty, ChannelGroup> channelGroupMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap = new ConcurrentHashMap<>();

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final WebSocketServerNetty serverNetty = new WebSocketServerNetty();
        ChannelFuture future = serverNetty.start();
        Runtime.getRuntime().addShutdownHook(new Thread(serverNetty::destroy));
        future.channel().closeFuture().syncUninterruptibly();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {

    }

    public ChannelFuture start() throws CertificateException, SSLException {
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
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new WebSocketServerInitializer(sslCtx, channelGroupMap, channelNameAndChannelMap));

        ChannelFuture future = b.bind(new InetSocketAddress(PORT));
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("WebSocketServerNetty 绑定端口" + PORT + "成功!");
            } else {
                log.error("WebSocketServerNetty 尝试绑定端口" + PORT + "失败!", channelFuture.cause());
            }
        });
        future.syncUninterruptibly();

        this.channel = future.channel();

        return future;
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }

        channelGroupMap.forEach((key, value) -> value.close());

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


}
