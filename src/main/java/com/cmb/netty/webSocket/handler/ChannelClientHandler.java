package com.cmb.netty.webSocket.handler;

import com.cmb.netty.webSocket.initializer.HttpClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;
import java.net.URI;

public class ChannelClientHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final String URL;

    public ChannelClientHandler(String URL) {
        this.URL = URL;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
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
                .handler(new HttpClientInitializer(uri, sslCtx, ctx))
                .group(ctx.channel().eventLoop())
                .connect(new InetSocketAddress(host, port));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

    }
}
