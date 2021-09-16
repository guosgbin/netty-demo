package cn.guosgbin.nio.net.read;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 测试read事件
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(10086));

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int count = selector.select();
                if (count <= 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        // 有客户端连接
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        // 客户端通道注册可读事件
                        ByteBuffer privateBuffer = ByteBuffer.allocate(16);
                        socketChannel.register(selector, SelectionKey.OP_READ, privateBuffer);
                    } else if (key.isReadable()) {
                        // channel有可读事件
//                        key,ch
                    }
                    iterator.remove();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
