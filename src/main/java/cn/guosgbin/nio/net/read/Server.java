package cn.guosgbin.nio.net.read;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugAll;

/**
 * 测试read事件
 */
@Slf4j
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
                        ByteBuffer privateBuffer = ByteBuffer.allocate(4);
                        socketChannel.register(selector, SelectionKey.OP_READ, privateBuffer);
                        log.debug("客户端已连接: " + socketChannel);
                    } else if (key.isReadable()) {
                        try {
                            // channel有可读事件
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            int read = socketChannel.read(buffer);
                            // 如果是客户端正常断开，read方法会返回-1
                            if (read == -1) {
                                key.cancel();
                                socketChannel.close();
                            } else {
                                buffer.flip();
                                String data = Charset.defaultCharset().decode(buffer).toString();
                                log.debug("接收到客户端的数据: " + data);
                                buffer.clear();
                            }
                        } catch (IOException e) {
                            // 客户端异常断开，也会产生一个读事件，比如说直接停止程序了，此时会抛异常
                            e.printStackTrace();
                            // 取消客户端异常断开的读事件
                            key.cancel();
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
