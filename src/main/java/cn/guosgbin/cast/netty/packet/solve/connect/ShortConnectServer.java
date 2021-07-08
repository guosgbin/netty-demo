package cn.guosgbin.cast.netty.packet.solve.connect;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 短连接 黏包可以解决
 * 半包用这种办法还是不好解决，因为接收方的缓冲区大小是有限的
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/7 22:59
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class ShortConnectServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2);
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    // 系统接收缓冲区大小 滑动窗口
//                    .option(ChannelOption.SO_RCVBUF, 10)
                    // 调整netty的接收缓冲区大小
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16, 16, 16))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                // 连接成功调用
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("connected {}", ctx.channel());
                                    super.channelActive(ctx);
                                }

                                // 连接断开调用
                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("disconnect {}", ctx.channel());
                                    super.channelInactive(ctx);
                                }
                            });
                        }
                    })
                    .bind(new InetSocketAddress(10086));

            channelFuture.sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.debug("stop...");
        }

    }
}
