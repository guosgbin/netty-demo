package cn.guosgbin.chatroom.message;

import lombok.Data;
import lombok.ToString;

/**
 * 响应消息的抽象类
 */
@Data
@ToString(callSuper = true)
public abstract class AbstractResponseMessage extends Message {
    // 是否成功
    private boolean success;
    // 原因
    private String reason;

    public AbstractResponseMessage() {
    }

    public AbstractResponseMessage(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }
}
