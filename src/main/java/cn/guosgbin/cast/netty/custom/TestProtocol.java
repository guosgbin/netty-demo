package cn.guosgbin.cast.netty.custom;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 测试自定义协议
 *
 * 1.当netty发送或者接受一个消息的时候，就将会发生一次数据转换，
 *      入站消息会被解码，从字节转换为另一种格式（比如java对象）；
 *      如果是出站消息，它会被编码成字节。
 * 2.netty提供一系列实用的编码解码器，他们都实现了ChannelInboundHandler或者ChannelOutboundHandler接口。在这些类中，channelRead方法已经被重写了，以入站为例，对于每个从入站Channel读取的消息，这个方法会被调用，随后，它将调用由解码器所提供的decode()方法进行解码，并将已经解码的字节转发给ChannelPipeline中的下一个ChannelInboundHandler
 *
 * 入站写 就是编码 入站： 客户端 -> 服务端
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/10 16:39
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class TestProtocol {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LoggingHandler(LogLevel.DEBUG),
                new LengthFieldBasedFrameDecoder(1024, 28, 4, 0, 0),
                new MessageCodec()
        );

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");

        // 客户端 -> 服务端  出站  出站是编码器
        boolean b = embeddedChannel.writeOutbound(message);

        // 测试解码  服务端 -> 客户端 入站
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buffer);

        ByteBuf buffer1 = buffer.slice(0, 100);
        ByteBuf buffer2 = buffer.slice(100, buffer.readableBytes() - 100);

        buffer1.retain();
        boolean b1 = embeddedChannel.writeInbound(buffer1);
        boolean b12 = embeddedChannel.writeInbound(buffer2);


    }
}
