package com.example.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
@Component
public class NettyTcpConnectionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(io.netty.channel.ChannelHandlerContext ctx) {
        log.info("New connection from {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(io.netty.channel.ChannelHandlerContext ctx) {
        log.info("Connection closed from {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught", cause);
        ctx.close();
    }
}
