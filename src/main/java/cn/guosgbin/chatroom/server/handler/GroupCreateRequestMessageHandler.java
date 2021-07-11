package cn.guosgbin.chatroom.server.handler;

import cn.guosgbin.chatroom.message.GroupCreateRequestMessage;
import cn.guosgbin.chatroom.message.GroupCreateResponseMessage;
import cn.guosgbin.chatroom.server.session.Group;
import cn.guosgbin.chatroom.server.session.GroupSession;
import cn.guosgbin.chatroom.server.session.GroupSessionFactory;
import cn.guosgbin.chatroom.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/7/11 23:42
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            // 创建成功
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, "创建群组" + groupName +"成功"));
            // 给成员发送被拉入群组信息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            membersChannel.forEach(channel -> channel.writeAndFlush(new GroupCreateResponseMessage(true, "你被拉入群组" + groupName)));
        } else {
            // 创建失败
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + "已经存在"));
        }
    }
}
