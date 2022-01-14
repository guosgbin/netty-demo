package cn.guosgbin.protocol.demo.protocol.resolver;

import cn.guosgbin.protocol.demo.protocol.message.Message;
import cn.guosgbin.protocol.demo.protocol.message.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 响应消息处理器
 */
@Slf4j
public class ResponseMessageResolver implements Resolver {

  private static final AtomicInteger counter = new AtomicInteger(1);

  @Override
  public boolean support(Message message) {
    return message.getMessageType() == MessageTypeEnum.RESPONSE;
  }

  @Override
  public Message resolve(Message message) {
    // 接收到对方服务的响应消息之后，对响应消息进行处理，这里主要是将其打印出来
    int index = counter.getAndIncrement();
    log.debug("[trx: {} ] {}. receive response: {}", message.getSessionId(), index, message.getBody());
    log.debug("[trx: {} ] {}. attachments: {}", message.getSessionId(), index, message.getAttachments());

    // 响应消息不需要向对方服务再发送响应，因而这里写入一个空消息
    Message empty = new Message();
    empty.setMessageType(MessageTypeEnum.EMPTY);
    return empty;
  }
}