package cn.guosgbin.nio.net.multiple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 多线程版本
 * Boss线程负责接收 读写事件
 * Work线程负责处理 读写事件
 */
public class MultipleThreadServer2 {
    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(10086));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

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
                        ssc.configureBlocking(false);

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Worker任务对象，用于处理 读写事件
     */
    class Worker implements Runnable {
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
            if (!start) {
                try {
                    this.name = name;
                    thread = new Thread(this, name);
                    // 获得选择器
                    selector = Selector.open();
                    // 开启线程
                    thread.start();
                    start = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 添加任务到任务队列
            queue.add(() -> {
                try {
                    // 将boss获得到的SocketChannel，注册到当前线程的选择器上
                    sc.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException ignored) {
                }
            });

        }

        @Override
        public void run() {
            while (true) {
                try {
                    int select = selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        // 异步注册Selector
                        task.run();
                    }
                } catch (IOException e) {

                }
            }
        }
    }
}
