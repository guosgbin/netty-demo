package cn.guosgbin.chatroom2;

import cn.guosgbin.chatroom2.handler.MyLoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServer {
    private static final MyLoggingHandler LOGGING_HANDLER = new MyLoggingHandler();
    private static final StringDecoder STRING_DECODER = new StringDecoder();

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2);

        ServerBootstrap b = new ServerBootstrap();
        ChannelFuture f = b.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(LOGGING_HANDLER)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline/*.addLast(STRING_DECODER)*/
                                .addLast(new FixedLengthFrameDecoder(6))
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                        log.debug("读到了一个数据了 = {}", msg);
                                    }
                                });
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
