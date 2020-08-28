package com.cmb.netty.webSocket.initializer;

import com.cmb.netty.webSocket.entity.property.ChannelProperty;
import com.cmb.netty.webSocket.entity.property.SpecialGroupProperty;
import com.cmb.netty.webSocket.entity.wrap.ChannelGroupMapWrap;
import com.cmb.netty.webSocket.handler.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
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
    public  final ChannelGroupMapWrap channelGroupMap;
    private final ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap;
    private final ConcurrentHashMap<ChannelId, ConcurrentHashMap<SpecialGroupProperty, Channel>> specialChannelGroup;

    public WebSocketServerInitializer(SslContext sslCtx, ChannelGroupMapWrap channelGroupMap, ConcurrentHashMap<String, ChannelProperty> channelNameAndChannelMap, ConcurrentHashMap<ChannelId, ConcurrentHashMap<SpecialGroupProperty, Channel>> specialChannelGroup) {
        this.sslCtx = sslCtx;
        this.channelGroupMap = channelGroupMap;
        this.channelNameAndChannelMap = channelNameAndChannelMap;
        this.specialChannelGroup = specialChannelGroup;
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
        pipeline.addLast(new WebSocketServerProtocolHandler("/serve/ws", null, true));
        pipeline.addLast(new BlankTextFilterInHandler());
        pipeline.addLast(new ChannelManageHandler(channelGroupMap, channelNameAndChannelMap, specialChannelGroup));
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatHandler());
        pipeline.addLast(new DispatcherInboundHandler());
        pipeline.addLast(new ChannelClientHandler("http://api.gpsoo.net/1/account/monitor?access_token=0011045701369822736adb020814946df1ded1c8681d026d5c5&map_type=BAIDU&target=mycar&account=testacc&time=1366786321"));
    }
}
