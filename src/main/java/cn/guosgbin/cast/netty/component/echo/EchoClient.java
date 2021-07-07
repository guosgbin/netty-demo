package cn.guosgbin.cast.netty.component.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/7/7 8:58
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class EchoClient {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup(2))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("客户 端接收到消息了, {}", msg);
                                ByteBuf buffer = (ByteBuf) msg;
                                System.out.println(buffer.toString(Charset.defaultCharset()));
                                buffer.release();
//                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress(10086));

        channelFuture.sync();
        Channel channel = channelFuture.channel();
        log.debug("客户端发送消息...");
        channel.writeAndFlush("你好");
    }
}
