package com.cmb.netty.gateWay.utils;

import com.cmb.netty.gateWay.entity.body.ProtocolHttpBody;
import com.cmb.netty.gateWay.enu.AttachmentEnum;
import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import com.cmb.netty.gateway2.entity.URICompose;
import com.cmb.netty.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.ByteString;

public class NettyMessageUtils {
    public static final ByteString BUSINESS_RESP = ByteString.copyFrom(new byte[]{MessageTypeEnum.BUSINESS_RESP.getValue()});

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
                .putAttachment(AttachmentEnum.FROM.key(), from)
                .build();
        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();
    }

    public static NettyMessageProto.NettyMessage buildLoginReq() {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.LOGIN_REQ.getValue()}))
                .build();

        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .build();
    }

    public static NettyMessageProto.NettyMessage buildLoginResp(String body) {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.LOGIN_RESP.getValue()}))
                .build();

        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();
    }

    public static NettyMessageProto.NettyMessage buildRegister(String body) {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.REGISTER.getValue()}))
                .build();

        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();
    }

    public static NettyMessageProto.NettyMessage buildHttpConnect(final URICompose uriCompose) throws JsonProcessingException {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .putAttachment(ProtocolConversionEnum.PROTOCOL.code(), ProtocolConversionEnum.HTTP_CONNECT.code())
                .build();

        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(JsonUtils.toJson(uriCompose))
                .build();
    }

    public static NettyMessageProto.NettyMessage buildHttpRequest(final String method, final String path) throws JsonProcessingException {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .putAttachment(ProtocolConversionEnum.PROTOCOL.code(), ProtocolConversionEnum.HTTP.code())
                .build();

        ProtocolHttpBody body = ProtocolHttpBody.builder()
                .method(method)
                .path(path)
                .build();

        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(JsonUtils.toJson(body))
                .build();
    }
}
