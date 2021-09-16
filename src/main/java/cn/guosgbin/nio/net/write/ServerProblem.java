package cn.guosgbin.nio.net.write;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * 有问题的案例
 */
@Slf4j
public class ServerProblem {
    public static void main(String[] args) {
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(10086));

            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int count = selector.select();
                if (count <= 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = serverChannel.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);

                        // 连接成功后，往客户端发送大量数据
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < 30000000; i++) {
                            sb.append("a");
                        }
                        ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
                        /* ---------- 问题处开始 --------*/
                        // 问题原因：假如此次写入到channel一次写不完，网络发送能力是有限的
                        // 所以可能在某些时刻，channel写满了，就写不进去了，
                        // 而此时又在while循环里，导致线程一直卡在这个SelectionKey里
                        // 不满足nio的非阻塞的思想
                        while (buffer.hasRemaining()) {
                            // 数据发送到Channel中
                            int write = sc.write(buffer);
                            log.debug("此次一共写入数据: " + write);
                        }
                        /* ---------- 问题处结束 --------*/
                    }
                    iterator.remove();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
