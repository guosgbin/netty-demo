package cn.guosgbin.chatroom2.protocol.resolver;

import cn.guosgbin.chatroom2.protocol.message.Message;
import cn.guosgbin.chatroom2.protocol.message.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * pong消息处理器
 */
@Slf4j
public class PongMessageResolver implements Resolver {

  @Override
  public boolean support(Message message) {
    return message.getMessageType() == MessageTypeEnum.PONG;
  }

  @Override
  public Message resolve(Message message) {
    // 接收到pong消息后，不需要进行处理，直接返回一个空的message
    log.debug("receive pong message: {}", System.currentTimeMillis());
    Message empty = new Message();
    empty.setMessageType(MessageTypeEnum.EMPTY);
    return empty;
  }
}