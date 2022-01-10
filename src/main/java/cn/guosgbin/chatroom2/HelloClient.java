package cn.guosgbin.chatroom2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import javafx.scene.chart.PieChart;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HelloClient {

    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private static final StringEncoder STRING_ENCODER = new StringEncoder();


    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup workGroup = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture f = bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(LOGGING_HANDLER);
//                                .addLast(STRING_ENCODER);
                    }
                })
                .connect(new InetSocketAddress(10086)).sync();

        Channel channel = f.channel();
//        channel.writeAndFlush("12345612345");
//        channel.writeAndFlush("61234");
//        channel.writeAndFlus、h("56");
        ByteBufAllocator alloc = channel.alloc();
        ByteBuf byteBuf = alloc.directBuffer();
        byteBuf.writeBytes("1234".getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(byteBuf);

        ByteBuf byteBuf2 = alloc.directBuffer();
        byteBuf2.writeBytes("56123456123".getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(byteBuf2);



        ChannelFuture future = f.channel().closeFuture().sync();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                workGroup.shutdownGracefully();
                log.debug("channel: {}, 链路关闭", future.channel());
            }
        });
    }
}
