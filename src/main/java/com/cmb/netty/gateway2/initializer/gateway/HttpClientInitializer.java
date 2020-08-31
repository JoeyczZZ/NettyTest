package com.cmb.netty.gateway2.initializer.gateway;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

import java.net.InetSocketAddress;
import java.util.List;

public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final ChannelHandlerContext clientCtx;
    private final List<Channel> childChannel;
    private final List<HttpRequest> httpRequests;

    public HttpClientInitializer(SslContext sslCtx, ChannelHandlerContext clientCtx, List<Channel> childChannel, List<HttpRequest> httpRequests) {
        this.sslCtx = sslCtx;
        this.clientCtx = clientCtx;
        this.childChannel = childChannel;
        this.httpRequests = httpRequests;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new TestHandler(clientCtx, childChannel, httpRequests));
    }
}
