package cn.guosgbin.cast.netty.component.eventloop;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 测试EventLoop
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/4 16:07
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        // 创建一个事件循环组， 可以处理 io事件，普通任务和定时任务
        EventLoopGroup group = new NioEventLoopGroup(2);

        // 默认的事件循环组，可以处理普通任务和定时任务
        EventLoopGroup defaultGroup = new DefaultEventLoop();

        //
//        int count = NettyRuntime.availableProcessors() * 2;
//        System.out.println(Runtime.getRuntime().availableProcessors());
//        System.out.println(count);

        // 获取下一个事件循环对象
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 执行普通任务
        group.next().submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }

            log.debug("children...");
        });
        log.debug("main...");


        // 执行定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.debug("ok...");
        }, 3, 1, TimeUnit.SECONDS);

    }
}
