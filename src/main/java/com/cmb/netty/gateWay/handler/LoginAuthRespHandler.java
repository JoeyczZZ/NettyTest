package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateWay.utils.NettyUtils;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthRespHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(LoginAuthRespHandler.class.getName());

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    private String[] whiteList = { "127.0.0.1"};

    @Override
    public void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.typeVerify(msg.getHeader(), MessageTypeEnum.LOGIN_REQ)) {
            String nodeIndex = ctx.channel().remoteAddress().toString();

            NettyMessageProto.NettyMessage loginResp = null;
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResponse("-1", MessageTypeEnum.LOGIN_RESP);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false;
                for (String WIP : whiteList) {
                    if (ip.equals(WIP)) {
                        isOk = true;
                        break;
                    }
                }
                loginResp = isOk ? buildResponse("0", MessageTypeEnum.LOGIN_RESP) : buildResponse("-1", MessageTypeEnum.LOGIN_RESP);
                if (isOk) {
                    nodeCheck.put(nodeIndex, true);

                }
                log.info("The login response is : " + loginResp + " body [" + loginResp.getBody() + "]");
                ctx.writeAndFlush(loginResp);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessageProto.NettyMessage buildResponse(String result, MessageTypeEnum messageTypeEnum) {
        NettyMessageProto.Header header = NettyMessageProto.Header.newBuilder()
                .setType(ByteString.copyFrom(new byte[]{messageTypeEnum.getValue()}))
                .build();

        return NettyMessageProto.NettyMessage.newBuilder()
                .setHeader(header)
                .setBody(result)
                .build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
