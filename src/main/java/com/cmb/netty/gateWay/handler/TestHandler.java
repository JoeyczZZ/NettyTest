package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class TestHandler extends SimpleChannelInboundHandler<FullHttpMessage> {
    private static final Logger log = LoggerFactory.getLogger(TestHandler.class.getName());
    private final ChannelHandlerContext clientCtx;
    private final List<Channel> childChannel;
    private final List<HttpRequest> httpRequests;

    private final InetSocketAddress inetSocketAddress;
    private final Bootstrap bootstrap;

    public TestHandler(ChannelHandlerContext clientCtx, List<Channel> childChannel, List<HttpRequest> httpRequests, InetSocketAddress inetSocketAddress, Bootstrap bootstrap) {
        this.clientCtx = clientCtx;
        this.childChannel = childChannel;
        this.httpRequests = httpRequests;
        this.inetSocketAddress = inetSocketAddress;
        this.bootstrap = bootstrap;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        childChannel.add(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.warn("TestHandler channelActive");
        if (!httpRequests.isEmpty()) {
            for (HttpRequest request : httpRequests) {
                ctx.write(request);
            }
            ctx.flush();
            httpRequests.clear();
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        log.warn("FullHttpMessage " + msg);

        Attribute<String> attribute = clientCtx.channel().attr(AttributeKey.valueOf("requestId"));
        String requestId = attribute.get();

        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .putAttachment(ProtocolConversionEnum.PROTOCOL.code(), ProtocolConversionEnum.HTTP.code())
                .putAttachment("requestId", requestId)
                .build();

        NettyMessageProto.NettyMessage response = NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(msg.content().toString())
                .build();
        clientCtx.writeAndFlush(response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("TestHandler channelInactive");
//        childChannel.clear();
//        ChannelFuture channelFuture = bootstrap.connect(inetSocketAddress);
//        childChannel.add(channelFuture.channel());

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
