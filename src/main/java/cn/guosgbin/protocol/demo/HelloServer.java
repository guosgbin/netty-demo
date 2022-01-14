package cn.guosgbin.protocol.demo;

import cn.guosgbin.protocol.demo.handler.MyLoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

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
                        pipeline.addLast(new FixedLengthFrameDecoder(6));
//                        pipeline.addLast(STRING_DECODER);
                        pipeline.addLast(LOGGING_HANDLER);
                        pipeline.addLast(new SimpleChannelInboundHandler() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf)msg;
                                byteBuf.readableBytes();
                                byte[] bytes = new byte[byteBuf.readableBytes()];
                                byteBuf.readBytes(bytes);
                                log.debug("读到了一个数据了 = {}", new String(bytes, StandardCharsets.UTF_8));
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
