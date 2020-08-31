package com.cmb.netty.gateway2;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.syncResponse.HttpSyncWait;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateway2.entity.URICompose;
import com.cmb.netty.utils.JsonUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.URI;

public class HttpUtils {
    public static final Cache<URICompose, Channel> URI_COMPOSE_CHANNEL = Caffeine.newBuilder()
            .build();

    private static final String host = "127.0.0.1";

    private static final int port = 8197;

    public static <T> T send(final String method, final String URL, final Class<T> clazz) throws Exception {
        return send(method, URL, null, clazz);
    }

    public static <T> T send(final String method, final String URL, final Long timeout, final Class<T> clazz) throws Exception {
        NettyMessageProto.NettyMessage response = send(method, URL, timeout);
        return clazz == String.class ? (T) response.getBody() : JsonUtils.fromJson(response.getBody(), clazz);
    }

    private static NettyMessageProto.NettyMessage send(final String method, final String URL) throws Exception {
        return send(method, URL, 3000L);
    }

    private static NettyMessageProto.NettyMessage send(final String method, final String URL, final Long timeout) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        URICompose uriCompose = URICompose.builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .build();

        Channel channel = URI_COMPOSE_CHANNEL.getIfPresent(uriCompose);
        if (null == channel) {
            channel = connect(uriCompose);
        }
        NettyMessageProto.NettyMessage request = NettyMessageUtils.buildHttpRequest(method, uri.getRawPath());
        return timeout == null ? HttpSyncWait.send(channel, request) : HttpSyncWait.send(channel, request, timeout);
    }

    private static Channel connect(String URL) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        URICompose uriCompose = URICompose.builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .build();

        return connect(uriCompose);
    }

    private static Channel connect(URICompose uriCompose) throws Exception {

        Channel channel = URI_COMPOSE_CHANNEL.getIfPresent(uriCompose);
        if (null == channel) {
            ChannelFuture channelFuture = SomeClient.connect(HttpUtils.host, HttpUtils.port);
            URI_COMPOSE_CHANNEL.put(uriCompose, channelFuture.channel());

            NettyMessageProto.NettyMessage httpConnect = NettyMessageUtils.buildHttpConnect(uriCompose);
            channelFuture.channel().writeAndFlush(httpConnect);
            return channelFuture.channel();
        }
        return channel;
    }
}
