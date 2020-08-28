package com.cmb.netty.gateWay.initializer;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.handler.HC2ISMDispatcher;
import com.cmb.netty.gateWay.handler.HeartBeatReqHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HttpClient2InnerServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public HttpClient2InnerServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(NettyMessageProto.NettyMessage.getDefaultInstance()));

        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
//        pipeline.addLast(new LoginAuthReqHandler());
        pipeline.addLast(new HC2ISMDispatcher());
        pipeline.addLast(new HeartBeatReqHandler());
    }
}
