package cn.guosgbin.chatroom2.protocol.resolver;

import cn.guosgbin.chatroom2.protocol.message.Message;
import cn.guosgbin.chatroom2.protocol.message.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/10 8:34
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class PingMessageResolver implements Resolver{
    @Override
    public boolean support(Message message) {
        return MessageTypeEnum.PING == message.getMessageType();
    }

    @Override
    public Message resolve(Message message) {
        // 接收到 PING 消息后返回一个 PONG 消息
        log.debug("receive ping message: " + System.currentTimeMillis());
        Message pong = new Message();
        pong.setMessageType(MessageTypeEnum.EMPTY);
        return pong;
    }
}
