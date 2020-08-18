package com.cmb.netty.webSocket.initializer;

import com.cmb.netty.webSocket.ChannelGroupProperty;
import com.cmb.netty.webSocket.ChannelProperty;
import com.cmb.netty.webSocket.handler.ChannelManageHandler;
import com.cmb.netty.webSocket.handler.DispatcherInboundHandler;
import com.cmb.netty.webSocket.handler.HeartbeatHandler;
import com.cmb.netty.webSocket.handler.HttpRequestHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final ConcurrentHashMap<ChannelGroupProperty, ChannelGroup> channelGroupMap;
    private final ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap;

    public WebSocketServerInitializer(SslContext sslCtx, ConcurrentHashMap<ChannelGroupProperty, ChannelGroup> channelGroupMap, ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap) {
        this.sslCtx = sslCtx;
        this.channelGroupMap = channelGroupMap;
        this.channelNameAndChannelMap = channelNameAndChannelMap;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new HttpRequestHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/serve/ws"));
        pipeline.addLast(new ChannelManageHandler(channelGroupMap, channelNameAndChannelMap));
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatHandler());
        pipeline.addLast(new DispatcherInboundHandler());
    }
}
