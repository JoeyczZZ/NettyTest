package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HC2ISMDispatcher extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.typeVerify(msg.getHeader(), MessageTypeEnum.FROM)) {
            ctx.writeAndFlush(msg);
        }
    }
}
