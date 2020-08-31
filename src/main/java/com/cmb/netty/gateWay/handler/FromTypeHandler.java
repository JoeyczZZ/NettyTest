package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.AttachmentEnum;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.utils.StringUtils;
import com.github.benmanes.caffeine.cache.Cache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

public class FromTypeHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private final Cache<String, ChannelGroup> gsChannelGroupCache;

    public FromTypeHandler(Cache<String, ChannelGroup> gsChannelGroupCache) {
        this.gsChannelGroupCache = gsChannelGroupCache;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        NettyMessageProto.Header header = msg.getHeader();
        if (NettyMessageUtils.attachmentMapKeyPresentVerify(header, AttachmentEnum.FROM.key())) {

            String from = header.getAttachmentMap().get(AttachmentEnum.FROM.key());
            if (StringUtils.isNotBlank(from)) {
                ChannelGroup channels = gsChannelGroupCache.getIfPresent(from);
                if (null != channels) {
                    channels.writeAndFlush(msg);
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
