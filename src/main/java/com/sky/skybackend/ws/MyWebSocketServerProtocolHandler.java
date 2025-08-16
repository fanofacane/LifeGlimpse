package com.sky.skybackend.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;

import java.util.List;

public class MyWebSocketServerProtocolHandler extends WebSocketServerProtocolHandler {

    public MyWebSocketServerProtocolHandler(String websocketPath) {
        super(websocketPath);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        //对客户端消息解码时如果消息是关闭通道类型，则获取Close Code
        if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame closeWebSocketFrame = (CloseWebSocketFrame) frame;
            //传递CloseWebSocketFrame的statusCode
            AttributeKey<Integer> attributeKey = AttributeKey.valueOf("closeCode");
            ctx.channel().attr(attributeKey).setIfAbsent(closeWebSocketFrame.statusCode());
        }
        super.decode(ctx, frame, out);
    }

}
