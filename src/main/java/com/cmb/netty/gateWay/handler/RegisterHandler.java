package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.github.benmanes.caffeine.cache.Cache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class.getName());

    private final Cache<String, ChannelGroup> gsChannelGroupCache;
    private final ChannelGroup innerChannel;

    public RegisterHandler(Cache<String, ChannelGroup> gsChannelGroupCache, ChannelGroup innerChannel) {
        this.gsChannelGroupCache = gsChannelGroupCache;
        this.innerChannel = innerChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.typeVerify(msg.getHeader(), MessageTypeEnum.REGISTER)) {
            if ("innerClient".equals(msg.getBody())) {
                innerChannel.add(ctx.channel());
                return;
            }

            log.warn("2 --- receive register " + msg.getBody() + "   GatewayServer");

            ChannelGroup channels = gsChannelGroupCache.get(msg.getBody(), k -> new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));
            if (null != channels) {
                channels.add(ctx.channel());
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
