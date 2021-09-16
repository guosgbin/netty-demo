package cn.guosgbin.nio.net.write;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 *
 */
@Slf4j
public class Server {
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
                        log.debug("client connecting...");
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = serverChannel.accept();
                        sc.configureBlocking(false);
                        SelectionKey sKey = sc.register(selector, SelectionKey.OP_READ);

                        // 连接成功后，往客户端发送大量数据
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < 30000000; i++) {
                            sb.append("a");
                        }
                        ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
                        // 数据发送到Channel中
                        int write = sc.write(buffer);
                        log.debug("begin connect write data count: " + write);
                        if (buffer.hasRemaining()) {
                            // 假如此时Buffer中的数据未读完,注册一个可读事件
                            log.debug("ops = {}", sKey.interestOps());
                            sKey.interestOps(sKey.interestOps() + SelectionKey.OP_WRITE);
                            // 将未读完的数据绑定
                            sKey.attach(buffer);
                        }
                    } else if (key.isWritable()) {
                        log.debug("channel is writeable...");
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int write = sc.write(buffer);
                        log.debug("{} byte data was written this time", write);
                        if (!buffer.hasRemaining()) {
                            log.debug("write over, cancel listening Writable event...");
                            // 数据已经读取完了，取消关注可读事件，清空buffer
                            key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                            key.attach(null);
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
