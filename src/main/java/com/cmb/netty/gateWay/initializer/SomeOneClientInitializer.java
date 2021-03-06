package com.cmb.netty.gateWay.initializer;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.handler.BusinessResponseHandler;
import com.cmb.netty.gateWay.handler.ExceptionCatchHandler;
import com.cmb.netty.gateWay.handler.HeartBeatReqHandler;
import com.cmb.netty.gateWay.handler.LoginAuthReqHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SomeOneClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final List<ChannelHandler> channelHandlers;

    public SomeOneClientInitializer(SslContext sslCtx, List<ChannelHandler> channelHandlers) {
        this.sslCtx = sslCtx;
        this.channelHandlers = channelHandlers;
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
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        pipeline.addLast(new HeartBeatReqHandler());
        pipeline.addLast(new LoginAuthReqHandler());
        pipeline.addLast(new BusinessResponseHandler());

        channelHandlers.forEach(pipeline::addLast);

        pipeline.addLast(new ExceptionCatchHandler());
    }
}
