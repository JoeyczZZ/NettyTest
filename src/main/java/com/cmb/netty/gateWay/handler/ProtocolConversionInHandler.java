package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateWay.utils.NettyUtils;
import com.cmb.netty.utils.StringUtils;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.URISyntaxException;

public class ProtocolConversionInHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.typeVerify(msg.getHeader(), MessageTypeEnum.BUSINESS_REQ)) {
            String protocol = msg.getHeader().getAttachmentMap().get(ProtocolConversionEnum.PROTOCOL.code());
            if (StringUtils.isNotBlank(protocol) && ProtocolConversionEnum.HTTP_CONNECT.code().equals(protocol)) {
                httpConnect(ctx, msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void httpConnect(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws URISyntaxException {
        String urlValue = msg.getHeader().getAttachmentMap().get("url");
        if (null == urlValue) {
            NettyMessageProto.NettyMessage ex = NettyUtils.buildExceptionMessage("header缺乏必要的url参数");
            if (null != ex) {
                ctx.writeAndFlush(ex);
            }
            return;
        } else {
            if (StringUtils.isBlank(urlValue)) {
                NettyMessageProto.NettyMessage ex = NettyUtils.buildExceptionMessage("header的url参数为空");
                if (null != ex) {
                    ctx.writeAndFlush(ex);
                }
                return;
            }
        }
        ctx.pipeline().addLast(new HttpClientHandler(urlValue));

        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.SUCCESS.getValue()}))
                .build();
        ctx.writeAndFlush(NettyMessageProto.NettyMessage.newBuilder().setHeader(header).build());

        ctx.pipeline().remove(this);
    }
}
