package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.HttpClientURLEnum;
import com.cmb.netty.gateWay.enu.TypeFromEnum;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.github.benmanes.caffeine.cache.Cache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

public class HttpClient2InnerServerMessageHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private final Cache<String, ChannelGroup> gsChannelGroupCache;

    public HttpClient2InnerServerMessageHandler(Cache<String, ChannelGroup> gsChannelGroupCache) {
        this.gsChannelGroupCache = gsChannelGroupCache;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.attachmentMapKeyPresentAndEqualVerify(msg.getHeader(), "from", "gpsoo")) {
            if (TypeFromEnum.GPSOO.getName().equals(msg.getHeader().getAttachmentMap().get("from"))) {
                ChannelGroup channels = gsChannelGroupCache.getIfPresent(HttpClientURLEnum.GPSOO.getName());
                if (null != channels) {
                    channels.writeAndFlush(NettyMessageUtils.buildBusinessResponseMessage(msg));
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
