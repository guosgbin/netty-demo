package cn.guosgbin.cast.netty.custom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/7/10 15:44
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    /**
     * 编码
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 自定义一个魔数，用来在第一时间判定是否是无效数据包
        out.writeBytes("歪比歪比".getBytes(StandardCharsets.UTF_8)); // 12
        // 版本号，可以支持协议的升级
        out.writeByte(1); // 1
        // 序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk
        // 使用一个字节来标识使用哪一种序列化算法，假定0是jdk
        out.writeByte(0); // 1
        // 指令类型，是登录、注册、单聊、群聊... 跟业务相关
        out.writeInt(msg.getMessageType()); // 4
        // 请求序号，为了双工通信，提供异步能力
        out.writeInt(msg.getSequenceId()); // 4

        // 为了组装自定义协议的数据为16的倍数 12 + 1 + 1 + 4 + 4 + 4 = 26 还差6个
        out.writeBytes(new byte[]{127,127,127,127,127,127});

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 正文长度
        out.writeInt(bytes.length);
        // 消息正文
        out.writeBytes(bytes);
    }

    /**
     * 解码
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 魔数
        byte[] magicNumBytes = new byte[12];
        in.readBytes(magicNumBytes);
        String magicNum = new String(magicNumBytes, StandardCharsets.UTF_8);
        // 版本
        byte version = in.readByte();
        // 序列化类型
        byte serializerType = in.readByte();
        // 业务类型
        int messageType = in.readInt();
        // 请求序号
        int sequenceId = in.readInt();
        // 读取为了对其的无意义的字节 6个
        in.readBytes(6);
        // 读取正文长度
        int length = in.readInt();
        // 读取正文
        byte[] bytes = new byte[length];
        ByteBuf buf = in.readBytes(bytes, 0, length);

        // 反序列化
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        out.add(messageType);
    }

    public static void main(String[] args) {
        byte[] bytes = "歪比歪比".getBytes(StandardCharsets.UTF_8);

       StringBuilder sb = new StringBuilder();
       sb.append("[");
        for (Byte b : bytes) {
            String str = Integer.toHexString((b & 0x000000ff) | 0xffffff00).substring(6);
            sb.append(str + " ,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        System.out.println(sb);
        System.out.println(Arrays.toString(bytes));

    }
}
