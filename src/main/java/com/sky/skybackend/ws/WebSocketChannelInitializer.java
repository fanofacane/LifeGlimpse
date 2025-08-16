package com.sky.skybackend.ws;


import com.sky.skybackend.service.ChatService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private ChatService chatService;
//    @Autowired
//    private NettyWebSocketHandler nettyWebSocketHandler;
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // HTTP 编解码器（WebSocket 基于 HTTP 握手）
        pipeline.addLast(new HttpServerCodec());
        // 聚合 HTTP 消息（将分片的请求合并为完整消息）
        pipeline.addLast(new HttpObjectAggregator(65536));
        // 处理大文件传输（可选）
        pipeline.addLast(new ChunkedWriteHandler());
        // 自定义参数处理器，必须放在 new WebSocketServerProtocolHandler() 处理器之前
        pipeline.addLast(new NettyWebSocketParamHandler());
        // WebSocket 协议处理器（指定握手路径为 /order-tracking）
        pipeline.addLast(new MyWebSocketServerProtocolHandler("/order-tracking"));
        // 自定义消息处理器（处理订单状态推送逻辑）
        pipeline.addLast(new NettyWebSocketHandler(chatService));
    }
}
