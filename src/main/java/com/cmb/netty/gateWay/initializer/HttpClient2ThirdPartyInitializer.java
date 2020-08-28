package com.cmb.netty.gateWay.initializer;

import com.cmb.netty.gateWay.handler.HttpResponseShowHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

public class HttpClient2ThirdPartyInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    private final Channel toServerChannel;

    public HttpClient2ThirdPartyInitializer(SslContext sslCtx, Channel toServerChannel) {
        this.sslCtx = sslCtx;
        this.toServerChannel = toServerChannel;
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
        pipeline.addLast(new HttpResponseShowHandler(toServerChannel));
    }
}
