package cn.guosgbin.cast.netty.packet.phenomenon;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 客户端代码希望发送 10 个消息，每个消息是 16 字节
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/7 22:19
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class PhenomenonClient {
    public static void main(String[] args) throws InterruptedException {
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

                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("sending...");

                                    for (int i = 0; i < 10; i++) {
                                        ByteBuf buffer = ctx.alloc().buffer(16);
                                        buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                        ctx.writeAndFlush(buffer);
                                    }
//                                    super.channelInactive(ctx);
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
