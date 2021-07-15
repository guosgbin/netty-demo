package cn.guosgbin.chatroom.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * RPC响应消息
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/13 23:51
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Getter
@Setter
@ToString(callSuper = true)
public class RpcResponseMessage extends Message{
    /**
     * 返回值
     */
    private Object returnValue;

    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
