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

import java.sql.Time;
import java.util.concurrent.TimeUnit;


/**
 * 当系统退出时，建议通过 EventLoopGroup 的 shurdownGracefully
 * 来完成内存队列中积压消息的处理、链路的关闭和 EventLoop 线程的退出，以实现停机不中断业务。
 */
@Slf4j
public class Case01 {
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


            // 异步变同步
            ChannelFuture f = channelFuture.channel().closeFuture().sync();
            // 添加Channel关闭通知监听
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    // 业务处理代码，此处省略
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                    log.info(future.channel().toString() + " 链路关闭");
                }
            });

        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
        }
    }
}

/**
 * 守护线程测试
 *
 * 守护线程就是运行在程序后台的线程，通常守护线程是由JVM创建的，用于辅助用户线程或者JVM工作，
 * 比较典型的如GC线程。用户创建的线程也可以设置为守护线程（通常需要谨慎设置），程序的main线程不是守护线程。
 * 守护现在在Java里面的定义是，假如虚拟机中只有守护线程允许，则虚拟机退出。
 */
class DaemonThreadTest {
    public static void testDaemonThread() throws InterruptedException {
        long startTime = System.nanoTime();
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.DAYS.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Daemon-T");
        thread.setDaemon(true);
        thread.start();

        TimeUnit.SECONDS.sleep(15);
        System.out.println("系统退出，程序执行" + (System.nanoTime() - startTime) / 1000/1000/1000 + "s");
    }

    public static void testThread() throws InterruptedException {
        long startTime = System.nanoTime();
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.DAYS.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Daemon-T");
        // 设置非守护线程
        thread.setDaemon(false);
        thread.start();

        TimeUnit.SECONDS.sleep(15);
        System.out.println("系统退出，程序执行" + (System.nanoTime() - startTime) / 1000/1000/1000 + "s");
    }

    public static void main(String[] args) throws InterruptedException {
//        testDaemonThread();
        testThread();
    }
}
