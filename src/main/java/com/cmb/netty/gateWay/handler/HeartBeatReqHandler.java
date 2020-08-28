package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatReqHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatReqHandler.class.getName());

    private static final NettyMessageProto.NettyMessage message = NettyMessageProto.NettyMessage.newBuilder()
            .setHeader(NettyMessageProto.Header.newBuilder().setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.HEARTBEAT_REQ.getValue()})).build())
            .build();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush(message)
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (null != msg.getHeader() && msg.getHeader().getType().toByteArray()[0] == MessageTypeEnum.HEARTBEAT_RESP.getValue()) {
            log.info("Receive HeartBeat: " + msg);
        }
    }
}
