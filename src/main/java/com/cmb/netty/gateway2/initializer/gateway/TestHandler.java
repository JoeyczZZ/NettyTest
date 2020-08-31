package com.cmb.netty.gateway2.initializer.gateway;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class TestHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger log = LoggerFactory.getLogger(TestHandler.class.getName());
    private final ChannelHandlerContext clientCtx;
    private final List<Channel> childChannel;
    private final List<HttpRequest> httpRequests;

    public TestHandler(ChannelHandlerContext clientCtx, List<Channel> childChannel, List<HttpRequest> httpRequests) {
        this.clientCtx = clientCtx;
        this.childChannel = childChannel;
        this.httpRequests = httpRequests;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        childChannel.add(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
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
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
        Attribute<String> attribute = clientCtx.channel().attr(AttributeKey.valueOf("requestId"));
        String requestId = attribute.get();

        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .putAttachment(ProtocolConversionEnum.PROTOCOL.code(), ProtocolConversionEnum.HTTP.code())
                .putAttachment("requestId", requestId)
                .build();

        String body = response.content().toString(CharsetUtil.UTF_8);
        log.warn(" body " + body);
        NettyMessageProto.NettyMessage responseMessage = NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();
        clientCtx.writeAndFlush(responseMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
