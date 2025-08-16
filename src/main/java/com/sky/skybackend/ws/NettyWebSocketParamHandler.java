package com.sky.skybackend.ws;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class NettyWebSocketParamHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * 此处进行url参数提取，重定向URL，访问webSocket的url不支持带参数的，带参数会抛异常，这里先提取参数，将参数放入通道中传递下去，重新设置一个不带参数的url
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        //提取token
        Map<CharSequence, CharSequence> queryMap = UrlBuilder.ofHttp(uri).getQuery().getQueryMap();
        //判断是否携带token
        if (!queryMap.isEmpty()){
            //将参数放入通道中传递下去
            AttributeKey<String> attributeKey = AttributeKey.valueOf("token");
            ctx.channel().attr(attributeKey).setIfAbsent(queryMap.get("token").toString());
            request.setUri(URLUtil.getPath(uri));
            ctx.fireChannelRead(request.retain());
        }else {
            System.out.println("token为空，断开连接");
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("NettyWebSocketParamHandler.exceptionCaught --> cause: ", cause);
        ctx.close();
    }

}
