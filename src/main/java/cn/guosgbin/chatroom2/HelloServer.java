package cn.guosgbin.chatroom2;

import cn.guosgbin.chatroom2.handler.ServerPrintInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServer {
    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2);

        ServerBootstrap b = new ServerBootstrap();
        ChannelFuture f = b.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(LOGGING_HANDLER)
//                .handler(new ServerPrintInboundHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast(LOGGING_HANDLER)
                        pipeline.addLast(new ServerPrintInboundHandler());
                    }
                })
                .bind(10086).sync();

        ChannelFuture future = f.channel().closeFuture().sync();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                workGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
                log.debug("channel: {}, 链路关闭...",future.channel());
            }
        });
    }
}
