package com.example.server;

import com.example.config.NettyTcpConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NettyTcpServerManager {
    private final NettyTcpConfig nettyTcpConfig;
    private final NettyTcpPipelineInitializer nettyTcpPipelineInitializer;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public void start() {
        bossGroup = new NioEventLoopGroup(nettyTcpConfig.getBossThreads());
        workerGroup = new NioEventLoopGroup(nettyTcpConfig.getWorkerThreads());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(nettyTcpPipelineInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            log.info("Starting TCP Server on port {}", nettyTcpConfig.getPort());

            serverChannel = bootstrap.bind(nettyTcpConfig.getPort()).sync().channel();

        } catch (InterruptedException e) {
            log.error("Error starting TCP server", e);
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping TCP Server...");

        try {
            if (serverChannel != null) {
                serverChannel.close().sync();
            }
        } catch (InterruptedException e) {
            log.error("Error while stopping TCP server", e);
            Thread.currentThread().interrupt();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
