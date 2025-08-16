package com.sky.skybackend.ws;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketServer {
    private int port;
    @Autowired
    private WebSocketChannelInitializer webSocketChannelInitializer;
    public WebSocketServer() {
    }
    public WebSocketServer setPort(int port) {
        this.port = port;
        return this;
    }

    public void start() throws InterruptedException {
        // 主线程组（接受连接）
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 工作线程组（处理连接）
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(webSocketChannelInitializer); // 初始化管道

            // 绑定端口并启动
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("WebSocket服务器启动，端口：" + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

//    public static void main(String[] args) throws InterruptedException {
//        new WebSocketServer(8082).start(); // 启动服务器，监听8082端口
//    }
}
