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
 * 测试阻塞式
 *
 * 在阻塞模式下，下面两个方法会导致线程暂停
 * 1.ServerSocketChannel.accept  会在没有连接建立时让线程暂停
 * 2.SocketChannel.read  会在没有数据可读时让线程暂停
 * 阻塞的表现其实就是线程暂停了，暂停期间不会占用 cpu，但线程相当于闲置
 *
 * 单线程下，阻塞方法之间相互影响，几乎不能正常工作，需要多线程支持
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/24 21:42
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class ServerTest {
    /**
     * 使用nio来理解阻塞模式，单线程
     *
     * 现象 --->
     * 1.首先开启一个客户端1，能够连接到服务端，而且打印"连接成功"日志，此时没有什么问题
     * 2.然后再开启一个客户端2，此时因为客户端1未发送数据到服务端，阻塞在服务端的channel.read(buffer)，压根不会走到下一次循环的serverChannel.accept()
     * 3.让客户端1发送几个字节的数据，此时服务端的控制台就会打印出 接收到 客户端发来的 字节数据了， 此时服务端就显示  连接到客户端2 成功，打印"连接成功"日志
     *
     */
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 创建服务端socket 默认是阻塞模式
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // 绑定端口
        serverChannel.bind(new InetSocketAddress(10086));

        List<SocketChannel> channelList = new ArrayList<>();
        while (true) {
            log.debug("尝试连接...");
            // 获取客户端的channel，阻塞状态
            SocketChannel clientChannel = serverChannel.accept();
            log.debug("连接成功... {}", clientChannel);
            channelList.add(clientChannel);
            for (SocketChannel channel : channelList) {
                log.debug("before read... {}", channel);
                // 从channel里面读取数据到buffer里,返回的是读取到的字节数
                int readLen = channel.read(buffer);
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.debug("after read...{}", channel);
            }
        }
    }
}
