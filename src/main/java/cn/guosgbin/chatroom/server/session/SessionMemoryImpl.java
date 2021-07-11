package cn.guosgbin.chatroom.server.session;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionMemoryImpl implements Session {

    // key -> 用户名 value -> channel
    private final Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    // key -> channel  alue -> 用户名
    private final Map<Channel, String> channelUsernameMap = new ConcurrentHashMap<>();
    // channel的附加信息
    private final Map<Channel,Map<String,Object>> channelAttributesMap = new ConcurrentHashMap<>();

    /**
     * 绑定会话
     *
     * @param channel 哪个 channel 要绑定会话
     * @param username 会话绑定用户
     */
    @Override
    public void bind(Channel channel, String username) {
        log.debug("{} 登录成功...", username);
        usernameChannelMap.put(username, channel);
        channelUsernameMap.put(channel, username);
        channelAttributesMap.put(channel, new ConcurrentHashMap<>());
    }

    /**
     * 解绑会话
     *
     * @param channel 哪个 channel 要解绑会话
     */
    @Override
    public void unbind(Channel channel) {
        String username = channelUsernameMap.remove(channel);
        usernameChannelMap.remove(username);
        channelAttributesMap.remove(channel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return channelAttributesMap.get(channel).get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        channelAttributesMap.get(channel).put(name, value);
    }

    @Override
    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }

    @Override
    public String toString() {
        return usernameChannelMap.toString();
    }
}
