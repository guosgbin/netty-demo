package cn.guosgbin.cast.netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * netty入门Server
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/1 8:26
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class HelloServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                // 绑定EventLoop组
                .group(new NioEventLoopGroup())
                // NIO
                .channel(NioServerSocketChannel.class)
                // 处理器
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new StringDecoder());
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("读到了一个数据了 = {}", msg);
                            }
                        });
                    }
                })
                .bind(10086);


    }
}
