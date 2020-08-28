package com.cmb.netty.gateWay.utils;

import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.dto.ExceptionMessage;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.ByteString;

public class NettyUtils {

    public static NettyMessageProto.NettyMessage  buildExceptionMessage(String msg) {
        ExceptionMessage exceptionMessage = ExceptionMessage.builder().msg(msg).build();
        String json = "";
        try {
            json = JsonUtils.toJson(exceptionMessage);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return null;
        }

        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.EXCEPTION.getValue()}))
                .build();

        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(json)
                .build();
    }

    public static boolean httpMethodVerify(String method) {
        switch (method.hashCode()) {
            case 70454      : //GET
            case 2461856    : //POST
            case 79599      : //PUT
            case 2213344    : //HEAD
            case -531492226 : //OPTIONS
            case 75900968   : //PATCH
            case 2012838315 : //DELETE
            case 80083237   : //TRACE
            case 1669334218 : //CONNECT
                return true;
        }

        return false;
    }
}
