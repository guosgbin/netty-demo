package cn.guosgbin.protocol.demo.protocol.resolver;


import cn.guosgbin.protocol.demo.protocol.message.Message;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/10 0:01
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public interface Resolver {
    // 判断当前 Resolver 是否支持当前消息
    boolean support(Message message);

    // 针对一个消息进行处理
    Message resolve(Message message);
}
