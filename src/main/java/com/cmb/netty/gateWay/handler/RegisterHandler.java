package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.HttpClient2InnerServer;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.github.benmanes.caffeine.cache.Cache;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class RegisterHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private final Cache<String, ChannelGroup> gsChannelGroupCache;

    public RegisterHandler(Cache<String, ChannelGroup> gsChannelGroupCache) {
        this.gsChannelGroupCache = gsChannelGroupCache;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.typeVerify(msg.getHeader(), MessageTypeEnum.REGISTER)) {
            ChannelGroup channels = gsChannelGroupCache.get(msg.getBody(), k -> new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));
            channels.add(ctx.channel());
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, "/", Unpooled.EMPTY_BUFFER);
            request.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.APPLICATION_JSON);

            ChannelGroup c = HttpClient2InnerServer.THIRD_PART_MAP.getIfPresent(msg.getBody());
            if (null != c) {
                c.writeAndFlush(request);
            }

        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
