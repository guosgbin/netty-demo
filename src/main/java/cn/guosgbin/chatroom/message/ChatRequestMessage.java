package cn.guosgbin.chatroom.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatRequestMessage extends Message {
    // 消息
    private String content;
    // 消息接收方
    private String to;
    // 消息发送方
    private String from;

    public ChatRequestMessage() {
    }

    public ChatRequestMessage(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return ChatRequestMessage;
    }
}
