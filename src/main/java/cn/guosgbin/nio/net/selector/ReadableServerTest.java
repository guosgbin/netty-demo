package cn.guosgbin.nio.net.selector;

import lombok.extern.slf4j.Slf4j;
import sun.security.jgss.wrapper.GSSCredElement;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 测试可写事件
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/29 8:44
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class ReadableServerTest {
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
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
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
                    sc.register(selector, SelectionKey.OP_READ);
                    // 向客户端发送数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }

                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int write = sc.write(buffer);
                    log.debug("实际写入数据数: " + write);



                }

            }
        }



    }
}
