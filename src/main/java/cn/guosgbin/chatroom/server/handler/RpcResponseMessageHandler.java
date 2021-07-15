package cn.guosgbin.chatroom.server.handler;

import cn.guosgbin.chatroom.message.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/7/14 8:42
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    public static Map<Integer, Promise<Object>> PROMISE = new ConcurrentHashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("服务端发来的数据：{}", msg);
        int sequenceId = msg.getSequenceId();
        Promise<Object> promise = PROMISE.get(sequenceId);
        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if (returnValue != null) {
                promise.setSuccess(returnValue);
            } else {
                promise.setFailure(exceptionValue);
            }
        }
    }
}
