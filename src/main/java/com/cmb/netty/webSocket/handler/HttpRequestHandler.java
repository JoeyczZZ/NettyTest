package com.cmb.netty.webSocket.handler;

import com.cmb.netty.webSocket.entity.ChannelAttribute;
import com.cmb.netty.webSocket.enu.RequestMessageEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Pattern patten = Pattern.compile("([^=&]+)(?=(=([^=&]+)+))|(?<=([^=&]=))([^=&]+)");
    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class.getName());

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String filteredUrl = urlProcess(request.uri(), ctx);

        if (null != filteredUrl) {
            request.setUri(filteredUrl);
            ctx.fireChannelRead(request.retain());
        }
    }

    private String urlProcess(String URL, ChannelHandlerContext ctx) throws InterruptedException {
        String[] separate = StringUtils.split(URL, "?");

        String channelName = null;
        String entCode = null;
        String type = null;

        if (separate.length > 1) {
            Map<String, String> params = new HashMap<>();
            Matcher matcher = patten.matcher(separate[1]);
            while (matcher.find()) {
                String k = matcher.group();
                if(matcher.find()) {
                    params.put(k, matcher.group());
                }
            }

            channelName = params.get(RequestMessageEnum.CHANNEL_NAME.getCode());
            entCode = params.get(RequestMessageEnum.ENT_CODE.getCode());
            type = params.get(RequestMessageEnum.TYPE.getCode());
        }

        Attribute<ChannelAttribute> attribute = ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText()));
        ChannelAttribute channelAttribute = ChannelAttribute.builder()
                .channelName(channelName)
                .entCode(entCode)
                .type(type)
                .build();
        attribute.set(channelAttribute);

        return separate[0];
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ChannelId: " + ctx.channel().id().asLongText() + "抛出异常", cause);
        ctx.close();
    }
}
