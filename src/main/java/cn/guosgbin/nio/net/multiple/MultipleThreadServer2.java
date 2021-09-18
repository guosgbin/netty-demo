package cn.guosgbin.nio.net.multiple;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程版本
 * Boss线程负责接收 读写事件
 * Work线程负责处理 读写事件
 */
@Slf4j
public class MultipleThreadServer2 {
    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(10086));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            Worker[] workers = new Worker[2];
            for (int i = 0; i < workers.length; i++) {
                workers[i] = new Worker("work-" + i);
            }

            AtomicInteger i = new AtomicInteger(0);
            while (true) {
                int count = selector.select();
                if (count <= 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);
                        log.debug("client connected... {}", sc.getRemoteAddress());
                        Worker worker = workers[i.getAndIncrement() % 2];
                        worker.initWoker(sc);
                    }
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Worker任务对象，用于处理 读写事件
     */
    @Slf4j
    static class Worker implements Runnable {
        // 线程名
        private String name;
        // 选择器
        private Selector selector;
        // 线程对象
        private Thread thread;
        private volatile boolean start = false;

        // 任务队列
        private BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>();

        public Worker(String name) {
            this.name = name;
        }

        public void initWoker(SocketChannel sc) {

            try {
                if (!start) {
                    this.name = name;
                    thread = new Thread(this, name);
                    // 获得选择器
                    selector = Selector.open();
                    // 开启线程
                    thread.start();
                    start = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 添加任务到任务队列
            queue.add(() -> {
                try {
                    // 将boss获得到的SocketChannel，注册到当前线程的选择器上
                    sc.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException ignored) {
                }
            });
            // 唤醒selector
            selector.wakeup();

        }

        @Override
        public void run() {
            while (true) {
                try {
                    int select = selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        // 注册可读事件到selector
                        task.run();
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isReadable()) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            int read = sc.read(buffer);
                            buffer.flip();
                            String data = StandardCharsets.UTF_8.decode(buffer).toString();
                            log.debug("read data: {}", data);
                        }
                        iterator.remove();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
