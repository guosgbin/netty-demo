package cn.guosgbin.cast.netty.component.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 测试channel客户端的连接 同步异步问题
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/4 19:01
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class TestChannelClient {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup(2))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress(10086));

        log.debug("channel, {}", channelFuture.channel());
        // 1.使用sync方法同步处理结果
//        channelFuture.sync();
        log.debug("channel, {}", channelFuture.channel());


        // 2.使用addListener回调对象 方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            // 在nio线程建立连接之后，会调用operationComplete
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                channel.writeAndFlush("你好啊");
            }
        });

    }
}
