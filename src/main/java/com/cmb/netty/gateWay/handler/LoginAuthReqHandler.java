package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginAuthReqHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(LoginAuthReqHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {

        if (null != msg.getHeader() && msg.getHeader().getType().toByteArray()[0] == MessageTypeEnum.LOGIN_RESP.getValue()) {
            String loginResult = msg.getBody();
            if (!"0".equals(loginResult)) {
                ctx.close();
            } else {
                log.info("Login is ok : " + msg);

                NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                        .setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.REGISTER.getValue()}))
                        .build();
                NettyMessageProto.NettyMessage request = NettyMessageProto.NettyMessage.newBuilder()
                        .setHeader(header)
                        .setBody("gpsoo")
                        .build();
                ctx.writeAndFlush(request);

                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessageProto.NettyMessage buildLoginReq() {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(ByteString.copyFrom(new byte[]{MessageTypeEnum.LOGIN_REQ.getValue()}))
                .build();

        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
