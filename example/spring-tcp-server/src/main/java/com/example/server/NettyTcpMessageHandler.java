package com.example.server;

import com.example.SystemEventProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyTcpMessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf message) {
        try {
            byte[] rawData = new byte[message.readableBytes()];
            message.readBytes(rawData);

            SystemEventProto decodedMessage = SystemEventProto.parseFrom(rawData);
            String clientAddress = getClientAddress(ctx);

            if(decodedMessage.hasHeartbeat()) {
                log.info("Received heartbeat from {}", clientAddress);
                return;
            }

            log.info("Received from {} -> {}", clientAddress, decodedMessage);
        } catch (Exception e) {
            log.error("Protobuf decoding failed!", e);
        }
    }

    private String getClientAddress(ChannelHandlerContext ctx) {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return String.format("%s:%d", remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
    }
}
