package cn.guosgbin.nio.net.single;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugRead;

/**
 * 测试非阻塞的服务端
 *
 * * 非阻塞模式下，相关方法都会不会让线程暂停
 *   * 在 ServerSocketChannel.accept 在没有连接建立时，会返回 null，继续运行
 *   * SocketChannel.read 在没有数据可读时，会返回 0，但线程不必阻塞，可以去执行其它 SocketChannel 的 read 或是去执行 ServerSocketChannel.accept
 *   * 写数据时，线程只是等待数据写入 Channel 即可，无需等 Channel 通过网络把数据发送出去
 * * 但非阻塞模式下，即使没有连接建立，和可读数据，线程仍然在不断运行，白白浪费了 cpu
 * * 数据复制过程中，线程实际还是阻塞的（AIO 改进的地方）
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/24 22:19
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class NoBlockServerTest {
    public static void main(String[] args) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 创建服务端socket 默认是阻塞模式
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        // 设置非阻塞模式
        serverChannel.configureBlocking(false);

        // 绑定端口
        serverChannel.bind(new InetSocketAddress(10086));

        List<SocketChannel> channelList = new ArrayList<>();
        while (true) {
            // 获取客户端的channel，阻塞状态
            SocketChannel clientChannel = serverChannel.accept();

            if (clientChannel != null) {
                log.debug("连接成功... {}", clientChannel);
                clientChannel.configureBlocking(false);
                channelList.add(clientChannel);
            }
            for (SocketChannel channel : channelList) {
                // 从channel里面读取数据到buffer里,返回的是读取到的字节数
                int readLen = channel.read(buffer);
                if (readLen > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}", channel);
                }
            }
        }

    }
}
