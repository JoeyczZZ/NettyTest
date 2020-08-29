package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.entity.body.ProtocolHttpBody;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.utils.StringUtils;
import com.github.benmanes.caffeine.cache.Cache;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpInnerClientDispatcherHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(HttpInnerClientDispatcherHandler.class.getName());
    private final Cache<String, ChannelGroup> thirdPartMap;

    public HttpInnerClientDispatcherHandler(Cache<String, ChannelGroup> thirdPartMap) {
        this.thirdPartMap = thirdPartMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.attachmentMapKeyPresentAndEqualVerify(msg.getHeader(), "protocol", "HTTP")) {
            if (StringUtils.isNotBlank(msg.getBody())) {
                ProtocolHttpBody body = JsonUtils.fromJson(msg.getBody(), ProtocolHttpBody.class);

                log.warn("5 --- receive a protocol HTTP request to " + body.getTo() + "   HttpClientInnner");

                ChannelGroup channels = thirdPartMap.getIfPresent(body.getTo());

                if (null != channels) {
                    HttpRequest request = new DefaultFullHttpRequest(
                            HttpVersion.HTTP_1_1, new HttpMethod(body.getMethod()), body.getPath(), Unpooled.EMPTY_BUFFER);
                    request.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
                    request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.APPLICATION_JSON);
                    channels.writeAndFlush(request);
                }
            }
        }
    }
}
