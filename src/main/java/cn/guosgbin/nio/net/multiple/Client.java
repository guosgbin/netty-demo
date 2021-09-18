package cn.guosgbin.nio.net.multiple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/6/30 8:50
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class Client {
    public static void main(String[] args) throws IOException {

        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(10086));
        sc.write(Charset.defaultCharset().encode("1234567890ab11c"));
        System.in.read();
    }
}

