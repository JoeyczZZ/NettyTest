package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.HttpClient2InnerServer;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayDispatcherHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(GatewayDispatcherHandler.class.getName());

    private final ChannelGroup innerChannel;

    public GatewayDispatcherHandler(ChannelGroup innerChannel) {
        this.innerChannel = innerChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.attachmentMapKeyPresentVerify(msg.getHeader(), "protocol")) {
            innerChannel.writeAndFlush(msg);
            log.warn("3 --- send protocol to innerClient   GatewayServer");
        }
    }
}
