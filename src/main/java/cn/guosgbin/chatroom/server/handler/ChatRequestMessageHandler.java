package cn.guosgbin.chatroom.server.handler;

import cn.guosgbin.chatroom.message.ChatRequestMessage;
import cn.guosgbin.chatroom.message.ChatResponseMessage;
import cn.guosgbin.chatroom.server.service.UserServiceFactory;
import cn.guosgbin.chatroom.server.session.Session;
import cn.guosgbin.chatroom.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/7/11 22:52
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    // 当服务器接收到一条消息时调用
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String toUser = msg.getTo();
        Channel toUserChannel = SessionFactory.getSession().getChannel(toUser);
        if (toUserChannel != null) {
            // 对方在线
            toUserChannel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        } else {
            // 对方不在线  给发送者发送消息
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方不存在或者不在线"));
        }
    }
}
