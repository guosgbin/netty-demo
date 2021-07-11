package cn.guosgbin.chatroom.server.handler;

import cn.guosgbin.chatroom.message.GroupChatRequestMessage;
import cn.guosgbin.chatroom.message.GroupChatResponseMessage;
import cn.guosgbin.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/7/11 23:51
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String groupName = msg.getGroupName();
        Set<String> members = GroupSessionFactory.getGroupSession().getMembers(groupName);
        if (!members.contains(from)) {
            ctx.writeAndFlush(new GroupChatResponseMessage(false, "您不是群组" + groupName + "的成员"));
            return;
        }

        List<Channel> channels = GroupSessionFactory.getGroupSession()
                .getMembersChannel(msg.getGroupName());

        for (Channel channel : channels) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}