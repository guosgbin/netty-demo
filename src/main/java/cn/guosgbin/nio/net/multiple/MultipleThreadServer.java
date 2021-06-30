package cn.guosgbin.nio.net.multiple;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugAll;

/**
 * 多线程版的服务端
 * <p>
 * boss负责接收读写事件
 * worker负责处理读写事件
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/30 8:24
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class MultipleThreadServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(10086));
        Selector selector = Selector.open();
        // 注册连接请求事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            Worker worker = new Worker("work-" + i);
            workers[i] = worker;
        }
        AtomicInteger count = new AtomicInteger();
        while (true) {
            int select = selector.select();
            log.debug("有感兴趣的事件...");
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 移除
                iterator.remove();
                // 客户端连接请求事件
                if (key.isAcceptable()) {
                    ServerSocketChannel serversc = (ServerSocketChannel) key.channel();
                    // 客户端channel
                    SocketChannel sc = serversc.accept();
                    sc.configureBlocking(false);
                    log.debug("{} connected", sc.getRemoteAddress());
                    // 轮询获得一个worker
                    Worker worker = workers[count.getAndIncrement() % workers.length];
                    worker.register(sc);
                }
            }
        }

    }

    /**
     * worker线程，主要处理读写事件
     */
    @Slf4j
    static class Worker implements Runnable {
        // 线程名
        private String name;
        // 线程对象
        private Thread thread;
        private Selector selector;
        private volatile boolean start = false;

        // 队列
        private BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>();

        public Worker(String name) {
            this.name = name;
        }

        /**
         * 初始化Worker
         */
        public void register(SocketChannel sc) {
            if (!start) {
                try {
                    thread = new Thread(this, name);
                    // 开启线程
                    selector = Selector.open();
                    thread.start();
                    start = true;
                } catch (IOException e) {
                }
            }

            // 添加到任务队列
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException ignored) {
                }
            });
            // 唤醒选择器
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    log.debug("worker 前 | work name = {}", name);
                    int select = selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        // 异步注册Selector
                        task.run();
                    }
                    log.debug("worker 遇到了一个感兴趣的事件");
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        // 可读事件
                        if (key.isReadable()) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            int read = sc.read(buffer);
                            log.debug("{} read", sc.getRemoteAddress());
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }


                } catch (IOException e) {

                }

            }
        }
    }
}
