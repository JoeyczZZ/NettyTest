package com.cmb.netty.gateway2.initializer.gateway;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.handler.ExceptionCatchHandler;
import com.cmb.netty.gateWay.handler.HeartBeatRespHandler;
import com.cmb.netty.gateWay.handler.LoginAuthRespHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

public class GatewayInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public GatewayInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
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
        pipeline.addLast(new ProtocolConversionInHandler());
        pipeline.addLast(new ExceptionCatchHandler());
    }
}
