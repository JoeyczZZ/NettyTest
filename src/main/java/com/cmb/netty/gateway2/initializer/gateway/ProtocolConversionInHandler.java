package com.cmb.netty.gateway2.initializer.gateway;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateway2.entity.URICompose;
import com.cmb.netty.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.URISyntaxException;

public class ProtocolConversionInHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.attachmentMapKeyPresentAndEqualVerify(msg.getHeader(), ProtocolConversionEnum.PROTOCOL.code(), ProtocolConversionEnum.HTTP_CONNECT.code())) {
            httpConnect(ctx, msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void httpConnect(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws URISyntaxException, JsonProcessingException {
        URICompose uriCompose = JsonUtils.fromJson(msg.getBody(), URICompose.class);

        ctx.pipeline().addLast(new HttpClientHandler(uriCompose));

        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.SUCCESS.getValue()}))
                .build();
        ctx.writeAndFlush(NettyMessageProto.NettyMessage.newBuilder().setHeader(header).build());

        ctx.pipeline().remove(this);
    }
}
