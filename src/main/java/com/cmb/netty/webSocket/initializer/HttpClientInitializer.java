package com.cmb.netty.webSocket.initializer;

import com.cmb.netty.webSocket.handler.GpsooHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

import java.net.URI;

public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {
    private final URI uri;
    private final SslContext sslCtx;
    private final ChannelHandlerContext clientCtx;
    private ChannelHandlerContext childCtx;

    public HttpClientInitializer(URI uri, SslContext sslCtx, ChannelHandlerContext clientCtx) {
        this.uri = uri;
        this.sslCtx = sslCtx;
        this.clientCtx = clientCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast(new LoggingHandler());
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new GpsooHandler(uri, clientCtx, childCtx));
    }
}
