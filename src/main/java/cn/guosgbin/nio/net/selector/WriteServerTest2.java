package cn.guosgbin.nio.net.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 测试可写事件
 *
 * 没问题的客户端
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/29 8:44
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class WriteServerTest2 {
    public static void main(String[] args) throws IOException {

        // 打开服务端
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 非阻塞
        ssc.configureBlocking(false);
        // 绑定端口
        ssc.bind(new InetSocketAddress(10086));
        // 注册事件
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int select = selector.select();
            log.debug("感兴趣事件");
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            log.debug("{}", selectionKeys);
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                // 连接事件
                if (key.isAcceptable()) {
                    // 接受客户端的信息
                    ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
                    SocketChannel sc = serverSocket.accept();
                    sc.configureBlocking(false);
                    // 客户端sc 关注可读事件
                    SelectionKey selectionKey = sc.register(selector, SelectionKey.OP_READ);
                    // 向客户端发送数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 10000000; i++) {
                        sb.append("a");
                    }

                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int write = sc.write(buffer);
                    log.debug("写入数据1, {}", write);
                    //
                    if (buffer.hasRemaining()) {
                        // 说明数据还没发完,所以需要服务端关注可写事件
                        selectionKey.interestOps(selectionKey.interestOps() + SelectionKey.OP_WRITE);
                        // 未写完的数据放到selectionKey中
                        selectionKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    // 可写事件
                    // SocketChannel是因为是 SocketChannel注册的OP_WRITE事件
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    int write = socketChannel.write(buffer);
                    log.debug("写入数据, {}", write);
                    if (!buffer.hasRemaining()) {
                        key.attach(null);
                        // 取消关注可写事件
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
