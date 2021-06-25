package cn.guosgbin.nio.net.single;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/6/24 21:52
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ClientTest {
    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        // connect
        channel.connect(new InetSocketAddress("127.0.0.1",10086));

        // 测试粘包黏包问题
//        channel.write(Charset.defaultCharset().encode("hi"));
//        channel.write(Charset.defaultCharset().encode("中国"));

        channel.write(Charset.defaultCharset().encode("hi\n"));
        channel.write(Charset.defaultCharset().encode("中国\n"));

        System.in.read();
    }
}
