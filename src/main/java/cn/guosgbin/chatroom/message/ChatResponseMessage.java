package cn.guosgbin.chatroom.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatResponseMessage extends AbstractResponseMessage {
    // 消息发送方法
    private String from;
    // 消息内容
    private String content;

    public ChatResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    public ChatResponseMessage(String from, String content) {
        this.from = from;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return ChatResponseMessage;
    }
}
