package com.cmb.netty.gateWay.syncResponse;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.entity.body.ProtocolHttpBody;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import com.cmb.netty.utils.JsonUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.TimeUnit;


public class HttpSyncWait {
    public static final Cache<String, SyncWaitFuture<NettyMessageProto.NettyMessage>> HTTP_SYNC_WAIT_MAP = Caffeine.newBuilder()
            .build();

    public static NettyMessageProto.NettyMessage send(final Channel channel, final String method, final String path, final String to) throws Exception {
        return send(channel, method, path, to, -1);
    }

    public static NettyMessageProto.NettyMessage send(final Channel channel, final String method, final String path, final String to, final long timeout) throws Exception {
        NettyMessageProto.NettyMessage request = NettyMessageProto.NettyMessage.newBuilder()
                .setBody(JsonUtils.toJson(ProtocolHttpBody.builder()
                        .method(method)
                        .path(path)
                        .to(to)
                        .build()))
                .build();

        return send(channel, request, timeout);
    }

    public static NettyMessageProto.NettyMessage send(final Channel channel, final NettyMessageProto.NettyMessage request) throws Exception {
        return send(channel, request, -1);
    }

    public static NettyMessageProto.NettyMessage send(final Channel channel, final NettyMessageProto.NettyMessage request, final long timeout) throws Exception {
        if (null == channel) {
            throw new NullPointerException("channel");
        }

        String id = "1234";  //此处应为全局唯一id

        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .putAttachment(ProtocolConversionEnum.PROTOCOL.code(), ProtocolConversionEnum.HTTP.code())
                .putAttachment("requestId", id)
                .build();

        NettyMessageProto.NettyMessage r = request.toBuilder().setHeader(header).build();

        SyncWaitFuture<NettyMessageProto.NettyMessage> syncWaitFuture = new SyncWaitResponseFuture(id);
        HTTP_SYNC_WAIT_MAP.put(id, syncWaitFuture);

        channel.writeAndFlush(r).addListener((ChannelFutureListener) channelFuture -> {
            syncWaitFuture.setWriteSuccess(channelFuture.isSuccess());
            syncWaitFuture.setCause(channelFuture.cause());

            if (!channelFuture.isSuccess()) {
                HTTP_SYNC_WAIT_MAP.invalidate(id);
            }
        });

        NettyMessageProto.NettyMessage response = get(syncWaitFuture, timeout);

        HTTP_SYNC_WAIT_MAP.invalidate(id);
        return response;
    }

    private static NettyMessageProto.NettyMessage get(final SyncWaitFuture<NettyMessageProto.NettyMessage> syncWaitFuture, final long timeout) throws Exception {
        NettyMessageProto.NettyMessage response;
        if (timeout == -1) {
            response = syncWaitFuture.get();
        }
        else {
            if (timeout < -1) {
                throw new IllegalArgumentException("timeout < -1");
            }
            response = syncWaitFuture.get(timeout, TimeUnit.MILLISECONDS);
        }

        if (null == response) {
            throw new Exception(syncWaitFuture.cause());
        }
        return response;
    }

}
