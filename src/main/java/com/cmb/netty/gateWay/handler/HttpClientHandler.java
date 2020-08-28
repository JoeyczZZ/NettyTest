package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.initializer.HttpClientInitializer;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateWay.utils.NettyUtils;
import com.cmb.netty.utils.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private final String scheme;
    private final String host;
    private final URI uri;

    public HttpClientHandler(String URL) throws URISyntaxException {
        this.uri = new URI(URL);
        this.scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        this.host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
    }

    private final List<ChannelHandlerContext> childCtx = new ArrayList<>();
    private final List<HttpRequest> httpRequests = new ArrayList<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("只支持HTTP(S)");
            return;
        }

        //SSL
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        Bootstrap bootstrap = new Bootstrap();

        bootstrap
                .channel(NioSocketChannel.class)
                .handler(new HttpClientInitializer(sslCtx, ctx, childCtx, httpRequests))
                .group(ctx.channel().eventLoop())
                .connect(new InetSocketAddress(host, port));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        NettyMessageProto.Header header = msg.getHeader();
        if (NettyMessageUtils.typeVerify(header, MessageTypeEnum.BUSINESS_REQ)) {
            if (null != header.getAttachmentMap() && header.getAttachmentMap().containsKey("method")) {
                String method = header.getAttachmentMap().get("method");
                if (!NettyUtils.httpMethodVerify(method)) {
                    ctx.writeAndFlush(NettyUtils.buildExceptionMessage("HTTP method 不正确"));
                    return;
                }

                if (!header.getAttachmentMap().containsKey("path")) {
                    ctx.writeAndFlush(NettyUtils.buildExceptionMessage("HTTP path 不存在"));
                    return;
                }

                String path = header.getAttachmentMap().get("path");
                if (StringUtils.isBlank(path)) {
                    ctx.writeAndFlush(NettyUtils.buildExceptionMessage("HTTP path 不能为空"));
                    return;
                }

                HttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.GET, path, Unpooled.EMPTY_BUFFER);
                request.headers().set(HttpHeaderNames.HOST, uri.getHost());
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.APPLICATION_JSON);
                ChannelHandlerContext cctx = childCtx.get(0);
                if (cctx.channel().isActive()) {
                    cctx.writeAndFlush(msg);
                } else {
                    httpRequests.add(request);
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
