package cn.guosgbin.protocol.demo.protocol.resolver;

import cn.guosgbin.protocol.demo.protocol.message.Message;
import cn.guosgbin.protocol.demo.protocol.message.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/10 0:06
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 *
 * Request 类型的消息
 */
@Slf4j
public class RequesetMessageResolver implements Resolver {
    private static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public boolean support(Message message) {
        if (message != null) {
            return Objects.equals(MessageTypeEnum.REQUEST, message.getMessageType());
        }
        return false;
    }

    @Override
    public Message resolve(Message message) {
        // 接收到 request 消息后，对消息进行处理，这里主要是将其打印出来
        int index = counter.getAndIncrement();
        log.debug("[trx: {} ] {}. receive request: {}", message.getSessionId(), index, message.getBody());
        log.debug("[trx: {} ] {}. attachments: {}", message.getSessionId(), index, message.getAttachments());

        // 处理完成后，生成一个响应消息返回
        Message response = new Message();
        response.setMessageType(MessageTypeEnum.RESPONSE);
        response.setBody("nice to meet you");
        response.addAttachment("name", "Simon");
        response.addAttachment("hometown", "HuangGang");
        return response;
    }
}
