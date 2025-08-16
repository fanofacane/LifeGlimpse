package com.sky.skybackend.ws;

import com.alibaba.fastjson.JSON;
import com.sky.skybackend.domain.pojo.Message;
import com.sky.skybackend.domain.pojo.WebSocketConnectionManager;
import com.sky.skybackend.service.ChatService;
import com.sky.skybackend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 事件回调
     */
//    @Autowired
    private ChatService chatService;
    public NettyWebSocketHandler (ChatService chatService) {
        this.chatService = chatService;
    }
        @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            //协议握手成功完成
            log.info("协议握手成功完成");
            //检查用户token
            AttributeKey<String> attributeKey = AttributeKey.valueOf("token");
            //从通道中获取用户token
            String token = ctx.channel().attr(attributeKey).get();
            //校验token逻辑
            try {
                Claims claims = JwtUtils.parseToken(token);
                String userId = claims.get("id").toString();
                // 存储用户ID与Channel的关联
                WebSocketConnectionManager.addConnection(userId, ctx.channel());
                System.out.println("当前登录id"+userId);
                System.out.println("当前连接数"+WebSocketConnectionManager.getConnectionCount());
            } catch (Exception e) {
                //如果token校验不通过，发送连接关闭的消息给客户端，设置自定义code和msg用来区分下服务器是因为token不对才导致关闭
                ctx.writeAndFlush(new CloseWebSocketFrame(40001, "token 无效"))
                        .addListener(ChannelFutureListener.CLOSE);
            }
        }
        //通过判断IdleStateEvent的状态来实现自己的读空闲，写空闲，读写空闲处理逻辑
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
            //读空闲，关闭通道
            log.info("NettyWebSocketHandler.userEventTriggered --> : 读空闲，关闭通道");
            ctx.close();
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("收到一条消息"+ msg.text());
        Message message = JSON.parseObject(msg.text(), Message.class);
        //获取用户channel
        Channel channel = WebSocketConnectionManager.getChannelByUserId(message.getTo());
        //把消息存入数据库
        chatService.save(message);
        //如果用户在线，就发送消息给用户
        if (channel != null && channel.isActive()) channel.writeAndFlush(new TextWebSocketFrame(msg.text()));

    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("NettyWebSocketHandler.channelInactive --> : 通道关闭了");
        //获取用户关闭通道时设置的close code
        AttributeKey<Integer> attributeKey = AttributeKey.valueOf("closeCode");
        Attribute<Integer> closeCode = ctx.channel().attr(attributeKey);
        //对closeCode进行区分处理....
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyWebSocketHandler.exceptionCaught --> cause: ", cause);
        ctx.close();
    }
}
