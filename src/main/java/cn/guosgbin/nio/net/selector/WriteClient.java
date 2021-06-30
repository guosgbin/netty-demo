package cn.guosgbin.nio.net.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试可写事件的客户端
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/29 22:33
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class WriteClient {
    public static void main(String[] args) throws IOException {
        // 创建客户端
        SocketChannel sc = SocketChannel.open();
        // TODO 放开这个注释就有问题 不知道为什么
//        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 10086));


        AtomicInteger count = new AtomicInteger();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        while (true) {
            int read = sc.read(buffer);
            count.addAndGet(read);
            log.debug("接收的数据: {}", count);
            buffer.clear();
        }
    }
}
