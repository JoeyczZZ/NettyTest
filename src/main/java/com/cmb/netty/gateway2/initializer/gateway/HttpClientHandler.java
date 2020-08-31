package com.cmb.netty.gateway2.initializer.gateway;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.entity.body.ProtocolHttpBody;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateWay.utils.NettyUtils;
import com.cmb.netty.gateway2.entity.URICompose;
import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.utils.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class HttpClientHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(HttpClientHandler.class.getName());

    private final URICompose uriCompose;

    private final Bootstrap bootstrap = new Bootstrap();

    public HttpClientHandler(URICompose uriCompose) {
        this.uriCompose = uriCompose;
    }

    private final List<Channel> childChannel = new ArrayList<>();
    private final List<HttpRequest> httpRequests = new ArrayList<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        if (!"http".equalsIgnoreCase(uriCompose.getScheme()) && !"https".equalsIgnoreCase(uriCompose.getScheme())) {
            System.err.println("只支持HTTP(S)");
            return;
        }

        //SSL
        final boolean ssl = "https".equalsIgnoreCase(uriCompose.getScheme());
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        ChannelFuture channelFuture = bootstrap
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new HttpClientInitializer(sslCtx, ctx, childChannel, httpRequests))
                .group(ctx.channel().eventLoop())
                .connect(new InetSocketAddress(uriCompose.getHost(), uriCompose.getPort()));
        channelFuture.addListener((ChannelFutureListener) cFuture -> {
            if (cFuture.isSuccess()) {
                log.info("connect to remote server: " + uriCompose);
            } else {
                log.error("connect to remote server: " + uriCompose + " fail!", cFuture.cause());
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        NettyMessageProto.Header header = msg.getHeader();
        if (NettyMessageUtils.attachmentMapKeyPresentAndEqualVerify(header, ProtocolConversionEnum.PROTOCOL.code(), ProtocolConversionEnum.HTTP.code())) {
            ProtocolHttpBody body = JsonUtils.fromJson(msg.getBody(), ProtocolHttpBody.class);
            if (StringUtils.isNotBlank(body.getMethod())) {
                String method = body.getMethod();
                if (!NettyUtils.httpMethodVerify(method)) {
                    ctx.writeAndFlush(NettyUtils.buildExceptionMessage("HTTP method 不正确"));
                    return;
                }

                String path = body.getPath();
                if (StringUtils.isBlank(path)) {
                    ctx.writeAndFlush(NettyUtils.buildExceptionMessage("HTTP path 不能为空"));
                    return;
                }

                Attribute<String> attribute = ctx.channel().attr(AttributeKey.valueOf("requestId"));
                attribute.set(header.getAttachmentMap().get("requestId"));

                HttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.valueOf(body.getMethod()), path, Unpooled.EMPTY_BUFFER);
                request.headers().set(HttpHeaderNames.HOST, uriCompose.getHost());
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.APPLICATION_JSON);
                Channel channel = childChannel.get(0);

                if (!channel.isRegistered()) {
                    bootstrap.connect(new InetSocketAddress(uriCompose.getHost(), uriCompose.getPort()));
                }
                if (channel.isActive()) {
                    channel.writeAndFlush(request);
                } else {
                    httpRequests.add(request);
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
