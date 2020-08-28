package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateWay.utils.NettyUtils;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatRespHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatRespHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.typeVerify(msg.getHeader(), MessageTypeEnum.HEARTBEAT_REQ)) {
            log.info("HeartBeat: " + msg);
            NettyMessageProto.NettyMessage response = NettyMessageProto.NettyMessage.newBuilder()
                    .setHeader(NettyMessageProto.Header.newBuilder().setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.HEARTBEAT_RESP.getValue()})).build())
                    .build();

            ctx.writeAndFlush(response);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
