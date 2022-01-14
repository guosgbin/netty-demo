package cn.guosgbin.protocol.demo.protocol.resolver;

import cn.guosgbin.protocol.demo.protocol.message.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/9 23:59
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class MessageResolverFactory {
    private static final MessageResolverFactory resolverFactory = new MessageResolverFactory();
    private static final List<Resolver> resolverList = new CopyOnWriteArrayList<>();

    private MessageResolverFactory() {
    }

    public static MessageResolverFactory getInstanse() {
        return resolverFactory;
    }

    public Resolver getMessageResolver(Message message) {
        for (Resolver resolver : resolverList) {
            if (resolver.support(message)) {
                return resolver;
            }
        }
        throw new RuntimeException("can not find resolve, message type: " + message.getMessageType());
    }

    public void registerResolver(Resolver resolver) {
        resolverList.add(resolver);
    }
}
