package cn.guosgbin.chatroom2.protocol.handler;

import cn.guosgbin.chatroom2.protocol.message.Message;
import cn.guosgbin.chatroom2.protocol.message.MessageTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/10 8:45
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class ClientMessageHandler extends ServerMessageHandler {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 对于客户端，在建立连接之后，在一个独立线程中模拟用户发送数据给服务端
        executor.execute(new MessageSender(ctx));
//        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 一定时间内，当前读物没有发生读取事件，也就是没有消息发送到当前服务来时
                // 其会发送一个 Ping 消息到服务器，以等待其响应 Pong 消息
                Message message = new Message();
                message.setMessageType(MessageTypeEnum.PING);
                ctx.writeAndFlush(message);
            } else if (event.state() == IdleState.WRITER_IDLE) {
                // 如果当前服务在指定时间没有写入消息到管道，则关闭当前管道
                ctx.close();
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    private static final class MessageSender implements Runnable {
        private static final AtomicLong counter = new AtomicLong(1);
        private volatile ChannelHandlerContext ctx;

        public MessageSender(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 模拟随机发送消息的过程
                    TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 3);
                    Message message = new Message();
                    message.setMessageType(MessageTypeEnum.REQUEST);
                    message.setBody("this is my " + counter.getAndIncrement() + " message.");
                    message.addAttachment("name", "xufeng");
                    ctx.writeAndFlush(message);
                }
            } catch (InterruptedException e) {
                log.error("Message sender error: ", e);
            }
        }
    }
}
