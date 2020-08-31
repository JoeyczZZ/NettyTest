package com.cmb.netty.gateway2.initializer.someClient;

import com.cmb.netty.gateWay.dto.NettyMessageProto;
import com.cmb.netty.gateWay.enu.ProtocolConversionEnum;
import com.cmb.netty.gateWay.syncResponse.HttpSyncWait;
import com.cmb.netty.gateWay.syncResponse.SyncWaitFuture;
import com.cmb.netty.gateWay.utils.NettyMessageUtils;
import com.cmb.netty.gateway2.enu.AttachmentEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HttpResponseHandler extends SimpleChannelInboundHandler<NettyMessageProto.NettyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessageProto.NettyMessage msg) throws Exception {
        if (NettyMessageUtils.attachmentMapKeyPresentAndEqualVerify(msg.getHeader(), ProtocolConversionEnum.PROTOCOL.code(), ProtocolConversionEnum.HTTP.code())) {
            SyncWaitFuture<NettyMessageProto.NettyMessage> syncWaitFuture = HttpSyncWait.HTTP_SYNC_WAIT_MAP.getIfPresent(msg.getHeader().getAttachmentMap().get(AttachmentEnum.REQUEST_ID.getName()));
            assert syncWaitFuture != null;
            syncWaitFuture.setMessage(msg);
        }
    }
}
