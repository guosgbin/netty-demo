package cn.guosgbin.advance;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Case02 {
    public static void main(String[] args) throws InterruptedException {
        // 服务端TCP连接接入线程池
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 处理客户端网络IO读写的线程池
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            ChannelFuture channelFuture = serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    })
                    // 同步方式绑定监听端口
                    .bind(10086).sync();



        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

/**
 * JDK的优雅退出 注册ShutdownHook
 */
class JdkShutdownHookTest {
    public static void main(String[] args) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("ShutdownHook execute start...");
            System.out.println("Netty NioEventLoopGroup shutdownGracefully...");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ShutdownHook execute end...");
        }));
        TimeUnit.SECONDS.sleep(7);
        System.exit(0);
    }
}

class JdkSignalTest {
    public static void main(String[] args) throws InterruptedException {
        // 获取当前操作系统的对应的指令
        String osCommand = System.getProperties().getProperty("os.name").toLowerCase().startsWith("win") ? "INT" : "TERM";
//        System.out.println(win);
        Signal signal = new Signal(osCommand);
        Signal.handle(signal, new SignalHandler() {
            @Override
            public void handle(Signal signal) {
                System.out.println("Signal handle start...");
                try {
                    TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("ShutdownHook execute start...");
            System.out.println("Netty NioEventLoopGroup shutdownGracefully...");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ShutdownHook execute end...");
        }));
//
    }
}

