package com.cmb.netty.webSocket.entity.wrap;

import com.cmb.netty.utils.JsonUtils;
import com.cmb.netty.webSocket.entity.monitor.ChannelLog;
import com.cmb.netty.webSocket.entity.property.ChannelGroupProperty;
import com.cmb.netty.webSocket.enu.MonitorTypeEnum;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelGroupMapWrap {
    public final ConcurrentHashMap<ChannelGroupProperty, ChannelGroupLogWrap> map = new ConcurrentHashMap<>();

    public static final ChannelGroupProperty MONITOR = ChannelGroupProperty.builder()
            .type(MonitorTypeEnum.MONITOR_9001.getCode())
            .build();


    public void put(ChannelGroupProperty channelGroupProperty, ChannelGroupLogWrap channelGroup) {
        map.put(channelGroupProperty, channelGroup);
        ChannelGroupLogWrap channels = map.get(MONITOR);
        if (null != channels) {
            ChannelLog channelLog = new ChannelLog(channelGroupProperty.getEntCode(), channelGroupProperty.getType(), "ChannelGroup", channelGroup.toString(), "MONITOR");
            try {
                channels.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(channelLog)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ChannelGroupLogWrap get(ChannelGroupProperty channelGroupProperty) {
        return map.get(channelGroupProperty);
    }

    public ChannelGroupLogWrap getMonitorChannelGroup() {
        return map.get(MONITOR);
    }
}
