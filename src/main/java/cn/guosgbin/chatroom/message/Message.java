package cn.guosgbin.chatroom.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息的抽象类
 */
@Data
public abstract class Message implements Serializable {

    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    /**
     * 请求序列ID
     */
    private int sequenceId;

    /**
     * 业务消息类型
     */
    private int messageType;

    public abstract int getMessageType();

    // 登录请求
    public static final int LoginRequestMessage = 0;
    // 登录响应
    public static final int LoginResponseMessage = 1;
    // 发送聊天消息请求
    public static final int ChatRequestMessage = 2;
    // 发送聊天消息响应
    public static final int ChatResponseMessage = 3;
    // 创建聊天组请求
    public static final int GroupCreateRequestMessage = 4;
    // 创建聊天组响应
    public static final int GroupCreateResponseMessage = 5;
    // 加入聊天组请求
    public static final int GroupJoinRequestMessage = 6;
    // 加入聊天组响应
    public static final int GroupJoinResponseMessage = 7;
    // 退出聊天组请求
    public static final int GroupQuitRequestMessage = 8;
    // 退出聊天组响应
    public static final int GroupQuitResponseMessage = 9;
    // 发送聊天组消息请求
    public static final int GroupChatRequestMessage = 10;
    // 发送聊天组消息响应
    public static final int GroupChatResponseMessage = 11;
    // 获取聊天组的成员请求
    public static final int GroupMembersRequestMessage = 12;
    // 获取聊天组的成员响应
    public static final int GroupMembersResponseMessage = 13;

    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(LoginRequestMessage, LoginRequestMessage.class);
        messageClasses.put(LoginResponseMessage, LoginResponseMessage.class);
        messageClasses.put(ChatRequestMessage, ChatRequestMessage.class);
        messageClasses.put(ChatResponseMessage, ChatResponseMessage.class);
        messageClasses.put(GroupCreateRequestMessage, GroupCreateRequestMessage.class);
        messageClasses.put(GroupCreateResponseMessage, GroupCreateResponseMessage.class);
        messageClasses.put(GroupJoinRequestMessage, GroupJoinRequestMessage.class);
        messageClasses.put(GroupJoinResponseMessage, GroupJoinResponseMessage.class);
        messageClasses.put(GroupQuitRequestMessage, GroupQuitRequestMessage.class);
        messageClasses.put(GroupQuitResponseMessage, GroupQuitResponseMessage.class);
        messageClasses.put(GroupChatRequestMessage, GroupChatRequestMessage.class);
        messageClasses.put(GroupChatResponseMessage, GroupChatResponseMessage.class);
        messageClasses.put(GroupMembersRequestMessage, GroupMembersRequestMessage.class);
        messageClasses.put(GroupMembersResponseMessage, GroupMembersResponseMessage.class);
    }
}
