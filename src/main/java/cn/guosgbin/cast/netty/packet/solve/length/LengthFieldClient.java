package cn.guosgbin.cast.netty.packet.solve.length;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * 测试预设长度
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/8 22:18
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class LengthFieldClient {
    private static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        sendMsg();
    }

    private static void sendMsg() {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        ChannelFuture channelFuture = null;
        try {
            channelFuture = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            log.debug("connected...");
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("sending...");
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    buffer.writeByte(12);
                                    buffer.writeBytes("HELLO, WORLD".getBytes());
                                    buffer.writeByte(6);
                                    buffer.writeBytes("NI HAO".getBytes());
                                    ctx.writeAndFlush(buffer);
                                }
                            });
                        }
                    })
                    .connect(new InetSocketAddress(10086));
            Channel channel = channelFuture.sync().channel();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
