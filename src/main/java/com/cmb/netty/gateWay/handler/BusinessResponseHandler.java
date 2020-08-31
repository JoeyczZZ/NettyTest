package com.cmb.netty.gateWay.handler;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.MessageTypeEnum;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessResponseHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    private static final Logger log = LoggerFactory.getLogger(BusinessResponseHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.typeVerify(msg.getHeader(), MessageTypeEnum.BUSINESS_RESP)) {
            log.warn("BusinessResponseHandler accept: " + msg.toString());
        }

        if (NettyMessageUtils.attachmentMapKeyPresentVerify(msg.getHeader(), "from")) {
            log.warn("from: " + msg.getHeader().getAttachmentMap().get("from") + " " + msg.getBody());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyMessageProto.NettyMessage register = NettyMessageUtils.buildRegister("gpsoo");
        ctx.writeAndFlush(register);
        log.warn("1 --- register gpsoo   SomeOneClient");

        super.channelActive(ctx);
    }
}
