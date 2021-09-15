package cn.guosgbin.nio.net.read;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 测试read事件
 */
public class Client {
    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(10086));

            ByteBuffer buffer = ByteBuffer.allocate(16).put("憨八龟".getBytes());
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
