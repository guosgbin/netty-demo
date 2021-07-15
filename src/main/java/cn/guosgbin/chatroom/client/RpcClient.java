package cn.guosgbin.chatroom.client;

import cn.guosgbin.chatroom.message.RpcRequestMessage;
import cn.guosgbin.chatroom.protocol.MessageCodecSharable;
import cn.guosgbin.chatroom.protocol.ProcotolFrameDecoder;
import cn.guosgbin.chatroom.server.handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        
        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_RESPONSE_HANDLER = new RpcResponseMessageHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(RPC_RESPONSE_HANDLER);
                }
            });
            Channel channel = bootstrap.connect("localhost", 10086).sync().channel();


            // 模拟发rpc调用
            RpcRequestMessage msg = new RpcRequestMessage(1,
                    "cn.guosgbin.chatroom.server.service.HelloService",
                    "sayHello",
                    String.class,
                    new Class[]{String.class},
                    new Object[]{"张三"}
            );
            channel.writeAndFlush(msg)
                    .addListener(promise -> {
                        if (promise.isSuccess()) {
                            log.debug("发送成功");
                        } else {
                            log.error("发送失败", promise.cause());
                        }
                    });

            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}