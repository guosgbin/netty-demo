package cn.guosgbin.chatroom2.protocol.handler;

import cn.guosgbin.chatroom2.protocol.message.Message;
import cn.guosgbin.chatroom2.protocol.resolver.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/10 8:39
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ServerMessageHandler extends SimpleChannelInboundHandler<Message> {

    private MessageResolverFactory resolverFactory = MessageResolverFactory.getInstanse();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Resolver resolver = resolverFactory.getMessageResolver(msg);
        Message result = resolver.resolve(msg);
        ctx.writeAndFlush(result);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // 注册一些消息处理器
        resolverFactory.registerResolver(new RequesetMessageResolver());
        resolverFactory.registerResolver(new ResponseMessageResolver());
        resolverFactory.registerResolver(new PingMessageResolver());
        resolverFactory.registerResolver(new PongMessageResolver());
        super.channelRegistered(ctx);
    }
}
