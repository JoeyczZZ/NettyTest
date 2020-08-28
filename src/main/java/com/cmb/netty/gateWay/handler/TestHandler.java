package com.cmb.netty.gateWay.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import java.util.List;

public class TestHandler extends SimpleChannelInboundHandler<FullHttpMessage> {
    private final ChannelHandlerContext clientCtx;
    private List<ChannelHandlerContext> childCtx;
    private final List<HttpRequest> httpRequests;

    public TestHandler(ChannelHandlerContext clientCtx, List<ChannelHandlerContext> childCtx, List<HttpRequest> httpRequests) {
        this.clientCtx = clientCtx;
        this.childCtx = childCtx;
        this.httpRequests = httpRequests;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        childCtx.add(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!httpRequests.isEmpty()) {
            for (HttpRequest request : httpRequests) {
                ctx.write(request);
            }
            ctx.flush();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        System.out.println(msg.content().toString());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("unregistered");
        childCtx = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
