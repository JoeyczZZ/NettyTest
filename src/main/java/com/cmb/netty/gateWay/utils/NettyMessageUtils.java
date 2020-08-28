package com.cmb.netty.gateWay.utils;

import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.google.protobuf.ByteString;

public class NettyMessageUtils {
    public static final ByteString BUSINESS_RESP = ByteString.copyFrom(new byte[]{MessageTypeEnum.BUSINESS_RESP.getValue()});
    public static final ByteString FROM = ByteString.copyFrom(new byte[] {MessageTypeEnum.FROM.getValue()});

    public static boolean typeVerify(NettyMessageProto.Header header, MessageTypeEnum messageTypeEnum) {
        return null != header && !header.getType().isEmpty() && header.getType().byteAt(0) == messageTypeEnum.getValue();
    }

    public static boolean attachmentMapKeyPresentVerify(NettyMessageProto.Header header, String key) {
        return null != header && null != header.getAttachmentMap() && header.getAttachmentMap().containsKey(key);
    }

    public static boolean attachmentMapKeyPresentAndEqualVerify(NettyMessageProto.Header header, String key, String value) {
        return null != header && null != header.getAttachmentMap() && header.getAttachmentMap().containsKey(key) && header.getAttachmentMap().get(key).equals(value);
    }

    public static NettyMessageProto.NettyMessage buildBusinessResponseMessage(NettyMessageProto.NettyMessage msg) {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(BUSINESS_RESP)
                .build();
        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(msg.getBody())
                .build();
    }

    public static NettyMessageProto.NettyMessage buildBusinessResponseMessage(String body) {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(BUSINESS_RESP)
                .build();
        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();
    }

    public static NettyMessageProto.NettyMessage buildFromRMessage(String from, String body) {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .putAttachment("from", from)
                .build();
        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();
    }
}
