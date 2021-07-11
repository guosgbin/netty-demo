package cn.guosgbin.chatroom.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 组的内存实现
 */
public class GroupSessionMemoryImpl implements GroupSession {
    // 聊天组 key是组名，value是Group对象
    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();

    /**
     * 创建聊天组
     *
     * @param name 组名
     * @param members 成员
     * @return
     */
    @Override
    public Group createGroup(String name, Set<String> members) {
        Group group = new Group(name, members);
        return groupMap.putIfAbsent(name, group);
    }

    /**
     * 加入聊天室
     *
     * @param name 组名
     * @param member 成员名
     * @return  如果组不存在返回 null, 否则返回组对象
     */
    @Override
    public Group joinMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().add(member);
            return value;
        });
    }

    /**
     * 移除聊天室的某个成员
     *
     * @param name 组名
     * @param member 成员名
     * @return
     */
    @Override
    public Group removeMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().remove(member);
            return value;
        });
    }

    /**
     * 删除聊天室
     *
     * @param name 组名
     * @return
     */
    @Override
    public Group removeGroup(String name) {
        return groupMap.remove(name);
    }

    /**
     * 获取指定聊天室的所有成员 没有的话返回空set集合
     *
     * @param name 组名
     * @return
     */
    @Override
    public Set<String> getMembers(String name) {
        return groupMap.getOrDefault(name, Group.EMPTY_GROUP).getMembers();
    }

    /**
     * 获得某个聊天室的所有用户的channel
     *
     * @param groupName 组名
     * @return
     */
    @Override
    public List<Channel> getMembersChannel(String groupName) {
        return getMembers(groupName).stream()
                .map(member -> SessionFactory.getSession().getChannel(member))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
