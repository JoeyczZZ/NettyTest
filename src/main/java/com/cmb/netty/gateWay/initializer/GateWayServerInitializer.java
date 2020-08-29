package com.cmb.netty.gateWay.initializer;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.handler.*;
import com.github.benmanes.caffeine.cache.Cache;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

public class GateWayServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final Cache<String, ChannelGroup> gsChannelGroupCache;
    private final ChannelGroup innerChannel;

    public GateWayServerInitializer(SslContext sslCtx, Cache<String, ChannelGroup> gsChannelGroupCache, ChannelGroup innerChannel) {
        this.sslCtx = sslCtx;
        this.gsChannelGroupCache = gsChannelGroupCache;
        this.innerChannel = innerChannel;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(NettyMessageProto.NettyMessage.getDefaultInstance()));
        
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());

        pipeline.addLast(new LoginAuthRespHandler());
        pipeline.addLast(new HeartBeatRespHandler());
        pipeline.addLast(new FromTypeHandler(gsChannelGroupCache));
        pipeline.addLast(new RegisterHandler(gsChannelGroupCache, innerChannel));
        pipeline.addLast(new GatewayDispatcherHandler(innerChannel));
    }
}
