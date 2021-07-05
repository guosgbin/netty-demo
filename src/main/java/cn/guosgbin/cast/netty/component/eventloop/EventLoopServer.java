package cn.guosgbin.cast.netty.component.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 测试
 * <p>
 * 继续分工 分为boss和work
 * 针对处理耗时长的handler使用DefalutEventLoop中的线程去处理
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/4 16:36
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        //
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        EventLoopGroup defaultGroup = new DefaultEventLoopGroup(2);
        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("handler1", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug("handler1, {}", buf.toString(Charset.defaultCharset()));
                                        super.channelRead(ctx, msg);
                                    }
                                })
                                .addLast(defaultGroup,"handler2", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug("handler2, {}", buf.toString(Charset.defaultCharset()));
                                        super.channelRead(ctx, msg);
                                    }
                                });
                    }
                })
                .bind(10086);
    }
}
