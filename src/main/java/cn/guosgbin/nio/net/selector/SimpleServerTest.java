package cn.guosgbin.nio.net.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugRead;

/**
 * 测试 selector
 *
 * channel 必须工作在非阻塞模式
 * FileChannel 没有非阻塞模式，因此不能配合 selector 一起使用
 *
 * 感兴趣的事件
 * 1.accept - 服务器端成功接受连接时触发 连接请求时触发
 * 2.connect - 客户端连接成功时触发
 * 3.read - 数据可读入时触发，有因为接收能力弱，数据暂不能读入的情况
 * 4 write - 数据可写出时触发，有因为发送能力弱，数据暂不能写出的情况
 *
 * 注意： selector 在拿到事件后未处理的话，是不会阻塞的，你没有处理就还在集合里
 *      假如我们拿到事件以后，可以用cancel()方法取消，这样selector就不会不阻塞了
 *
 * 注意：客户端正常关闭时，会产生一个读事件，这是Selector捕捉到了，此时 int read = channel.read(buffer);会返回0
 *
 * 注意：半包和黏包问题，可以使用Http2.0协议的方式，例如LTV length type value
 *      此处暂时还是使用 解析 \n 的方式做demo
 *
 *      假如依次发送的消息比分配的buffer的长度长，就会有两次 可读 事件，
 *
 *      attachment的参数可以给每个channel一个附件对象
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/24 22:50
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class SimpleServerTest {
    public static void main(String[] args) throws IOException {

        // 创建服务端
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // 设置非阻塞模式
        serverChannel.configureBlocking(false);

        // 创建Selector
        Selector selector = Selector.open();
        // 服务端channel注册到Selector上
        // 参数1：要注册到哪一个Selector上 参数2：感兴趣的时间
        SelectionKey selectionKey = serverChannel.register(selector, 0, null);
        // 设置感兴趣的事件
        selectionKey.interestOps(SelectionKey.OP_ACCEPT); // 设置为客户端连接请求时触发

        // 绑定端口
        serverChannel.bind(new InetSocketAddress(10086));

        while (true) {
            // 阻塞直到绑定事件发生
            // 注意： selector 在拿到事件后未处理的话，是不会阻塞的，你没有处理就还在集合里
            int select = selector.select();
            log.debug("有 {} 个感兴趣的事件", select);
            // 到此处说明有感兴趣的时间了，就需要去处理了
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                // 拿到了一个感兴趣的事件
                SelectionKey key = iterator.next();
                log.debug("key: {}", key);
                if (key.isAcceptable()) { // 说明是accept事件
                    // 获取这个channel
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = channel.accept();
                    // clientChannel设置成非阻塞模式
                    clientChannel.configureBlocking(false);
                    log.debug("获得了一个客户端的连接: {}", clientChannel);
                    // 给得到的客户端的clientChannel注册到Selector上
                    // 给每个添加一个自己的Buffer
                    ByteBuffer privateBuffer = ByteBuffer.allocate(4);
                    SelectionKey clientKey = clientChannel.register(selector, 0, privateBuffer);
                    clientKey.interestOps(SelectionKey.OP_READ); // 设置事件为可读，因为是客户端发起的连接请求事件
                } else if (key.isReadable()) { // 说明是可读时间
                    try {
                        // 获得客户端的channel
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 获得每个Channel自己的buffer,可能容量不够读取此次数据哦
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        // 如果是客户端正常断开，read方法会返回-1
                        int read = channel.read(buffer);
                        if (read > 0) {
                            buffer.flip();
//                            debugRead(buffer);
//                            String msg = Charset.defaultCharset().decode(buffer).toString();
//                            split();
//                            log.debug("buffer info : {}", msg);
                        } else if (read == -1) {
                            key.cancel(); // 取消客户端正常断开的读事件
                            channel.close();
                        }
                    } catch (IOException e) {
                        // windows程序： 客户端异常断开，也会产生一个读事件，比如说直接停止程序了，此时会抛异常
                        e.printStackTrace();
                        key.cancel();  // 取消客户端异常断开的读事件
                    }
                }

                // 每次处理完删除selectedKeys的set集合中的元素，否则下次还会遍历到已经处理过的selectedKey，就会导致出现问题
                iterator.remove();


                // 取消感兴趣的事件的执行，你可以视为已经处理了，这样selector就会阻塞了
//                key.cancel();
            }
        }
    }

    /**
     * 使用客户端消息的 /n 来读取消息
     * @param source
     */
    public static void split(ByteBuffer source) {
        int limit = source.limit();
        for (int i = 0; i < limit; i++) {
            if (source.get(i) == '\n') {
                ByteBuffer tempBuffer = ByteBuffer.allocate(4);
                // 读取传入的buffer到临时的buffer中
                source.flip();
                // 此次读取字节数
                int readLen = i + 1 - source.position();
                for (int j = 0; j < readLen; j++) {
                    tempBuffer.put(source.get());
                }
            }
        }
        // 可能没读完
        source.compact();
    }
}
