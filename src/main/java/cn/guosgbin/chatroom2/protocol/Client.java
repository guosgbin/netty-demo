package cn.guosgbin.chatroom2.protocol;

import cn.guosgbin.chatroom2.protocol.handler.ClientMessageHandler;
import cn.guosgbin.chatroom2.protocol.protocol.MessageProtocolCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/10 8:58
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class Client {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加用于解决粘包和拆包问题的处理器
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            // 添加用于进行心跳检测的处理器
                            pipeline.addLast(new IdleStateHandler(1, 2, 0));
                            // 添加用于根据自定义协议将消息与字节流进行相互转换的处理器
                            pipeline.addLast(new MessageProtocolCodec());
                            // 添加客户端消息处理器
                            pipeline.addLast(new ClientMessageHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect("127.0.0.1", 10086).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
