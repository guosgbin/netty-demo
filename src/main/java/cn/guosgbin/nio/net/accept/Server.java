package cn.guosgbin.nio.net.accept;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/9/15 8:19
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class Server {
    public static void main(String[] args) {
        try (ServerSocketChannel channel = ServerSocketChannel.open()) {
            // 设置非阻塞
            channel.configureBlocking(false);
            // 绑定端口
            channel.bind(new InetSocketAddress(10086));
            // 获取选择器对象
            Selector selector = Selector.open();
            // 将通道注册到选择器上，设置感兴趣的事件为 accept
            // accept 服务端成功接收连接时触发
            channel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int count = selector.select();
                log.debug("有 {} 个感兴趣的事件", count);
                if (count <= 0) {
                    continue;
                }
                // 获取已发生的事件
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                // 遍历事件进行处理
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        // 获取客户端的SocketChannel
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        // 设置非阻塞
                        socketChannel.configureBlocking(false);
                        log.debug("获得了一个客户端的连接: {}", socketChannel);
                        // 给得到的客户端的clientChannel注册到Selector上
                        // 给每个添加一个自己的Buffer
                        ByteBuffer privateBuffer = ByteBuffer.allocate(16);
                        // 设置事件为可读，数据可读入时触发，因为是客户端发起的连接请求事件,
                        socketChannel.register(selector, SelectionKey.OP_READ, privateBuffer);
                        log.debug("客户端Channel绑定选择器成功: {}", socketChannel);
                    } else if (key.isReadable()) {
                        log.debug("客户端Channel可读..,取消读取");
                        // 本案例不考虑读操作，取消掉
                        key.cancel();
                    }
                    // 移除已经处理的事件
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
