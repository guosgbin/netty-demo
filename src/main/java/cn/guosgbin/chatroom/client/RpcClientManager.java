package cn.guosgbin.chatroom.client;

import cn.guosgbin.chatroom.message.RpcRequestMessage;
import cn.guosgbin.chatroom.message.RpcResponseMessage;
import cn.guosgbin.chatroom.protocol.MessageCodecSharable;
import cn.guosgbin.chatroom.protocol.ProcotolFrameDecoder;
import cn.guosgbin.chatroom.server.handler.RpcRequestMessageHandler;
import cn.guosgbin.chatroom.server.handler.RpcResponseMessageHandler;
import cn.guosgbin.chatroom.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RpcClientManager {
    private static Channel channel = null;

    private static final Object LOCK = new Object();

    private static AtomicInteger sequenceId = new AtomicInteger();

    private static Integer nextSequenceId() {
        return sequenceId.incrementAndGet();
    }

    public static void main(String[] args) {

        HelloService helloService = getProxyService(HelloService.class);
        System.out.println(helloService.sayHello("张三"));
        System.out.println(helloService.sayHello("李四"));


//        ChannelFuture channelFuture = getChannel().writeAndFlush(new RpcRequestMessage(1,
//                "cn.guosgbin.chatroom.server.service.HelloService",
//                "sayHello",
//                String.class,
//                new Class[]{String.class},
//                new Object[]{"张三"}
//        ));
    }

    private static <T> T getProxyService(Class<T> serviceClass) {
        Object o = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, ((proxy, method, args) -> {
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(nextSequenceId(),
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args);
            getChannel().writeAndFlush(rpcRequestMessage);

            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISE.put(rpcRequestMessage.getSequenceId(), promise);
            // 等待promise结果
            promise.await();
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        }));
        return (T)o;
    }

    /**
     * 获取单例Channel
     *
     * @return
     */
    private static Channel getChannel() {
        if (channel == null) {
            synchronized (LOCK) {
                if (channel == null) {
                    initChannel();
                }
                return channel;
            }
        }
        return channel;
    }

    // 初始化channel
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_RESPONSE_HANDLER = new RpcResponseMessageHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        ChannelFuture channelFuture = bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_RESPONSE_HANDLER);
            }
        })
                .connect("localhost", 10086);
        try {
            channel = channelFuture.sync().channel();
        } catch (InterruptedException e) {
        }

        channelFuture.addListener(future -> {
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
            }
        });

        channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });

    }
}


//    public static void main(String[] args) throws InterruptedException {
//
//
//        // 模拟发rpc调用
//        RpcRequestMessage msg = new RpcRequestMessage(1,
//                "cn.guosgbin.chatroom.server.service.HelloService",
//                "sayHello",
//                String.class,
//                new Class[]{String.class},
//                new Object[]{"张三"}
//        );
//        channel.writeAndFlush(msg)
//                .addListener(promise -> {
//                    if (promise.isSuccess()) {
//                        log.debug("发送成功");
//                    } else {
//                        log.error("发送失败", promise.cause());
//                    }
//                });
//
//        channel.closeFuture().sync();
//    }
