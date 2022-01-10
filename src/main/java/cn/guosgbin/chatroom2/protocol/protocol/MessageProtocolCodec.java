package cn.guosgbin.chatroom2.protocol.protocol;

import cn.guosgbin.chatroom2.protocol.message.Message;
import cn.guosgbin.chatroom2.protocol.message.MessageTypeEnum;
import cn.guosgbin.chatroom2.protocol.message.SessionIdGenerator;
import cn.guosgbin.chatroom2.protocol.constants.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/8 22:15
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 *
 * <p>
 * 协议规定
 * 1. 魔数 magicNumber 4 一个固定的数据，一般用于指定当前字节序列时当前类型的协议
 * 2. 主版本号  mainVersion 1 当前服务器版本代码的主版本号
 * 3. 次版本号  subVersion 1 当前服务器版本的次版本号
 * 4. 修订版本号 modifyVersion 1 当前服务器版本的修订版本号
 * 5. 会话ID sessionId 8 当前请求的会话id，用于将请求和响应串联到一起
 * 6. 消息类型 messageType 1
 * 7. 附加数据 attachments 不定 附加消息时字符串类型的键值对来表示的，这里首先使用2个字节记录键值对的个数，
 *                            然后对于每个键和值，都首先使用4个字节记录其长度，然后是具体的数据，
 *                            其形式如：键值对个数+键长度+键数据+值长度+值数据
 * 8. 消息体长度 length 4 记录了消息体的长度
 * 9. 消息体 body 不定 消息体，服务之间交互所发送或接受的数据，这个长度有前面的length指定
 * </p>
 */
public class MessageProtocolCodec extends ByteToMessageCodec<Message> {


    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        if (Objects.equals(msg.getMessageType(), MessageTypeEnum.EMPTY)) {
            return;
        }
        // 魔数
        out.writeBytes(Constants.MAGIC_NUMBER.getBytes(Constants.CHARSET));
        // 主版本号
        out.writeByte(Constants.MAIN_VERSION);
        // 次版本号
        out.writeByte(Constants.SUB_VERSION);
        // 修订版本号
        out.writeByte(Constants.MODIFY_VERSION);
        // 会话ID
        String sessionId = msg.getSessionId();
        if (sessionId == null || Objects.equals("", sessionId.trim())) {
            sessionId = SessionIdGenerator.generate();
            out.writeCharSequence(sessionId, Constants.CHARSET);
        }
        // 消息类型
        out.writeByte(msg.getMessageType().getType());
        // 附加数据
        Map<String, String> attach = msg.getAttachments();
        // 1.当前附加消息的键值对个数
        out.writeShort(attach.size());
        attach.forEach((key, value) -> {
            // 2.某个 key 的长度
            out.writeInt(key.length());
            // 3.某个 key 的值
            out.writeCharSequence(key, Constants.CHARSET);
            // 4.某个 value 的长度
            out.writeInt(value.length());
            // 5.某个 value 的值
            out.writeCharSequence(value, Constants.CHARSET);
        });
        // 消息体长度和消息体
        if (msg.getBody() != null) {
            out.writeInt(msg.getBody().length());
            out.writeCharSequence(msg.getBody(), Constants.CHARSET);
        } else {
            out.writeInt(0);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Message message = new Message();
        // 读取魔数
        message.setMagicNumber(in.readByte());
        // 读取主版本号
        message.setMainVersion(in.readByte());
        // 读取次版本号
        message.setSubVersion(in.readByte());
        // 读取修订版本号
        message.setModifyVersion(in.readByte());
        // 读取会话 ID
        CharSequence charSequence = in.readCharSequence(Constants.SESSION_ID_LENGTH, Constants.CHARSET);
        message.setSessionId(charSequence.toString());
        // 读取消息类型
        message.setMessageType(MessageTypeEnum.get(in.readByte()));
        // 读取附加数据
        // 1.读取附加数据的Map的键值对个数
        short attachSize = in.readShort();
        Map<String, String> attach = new HashMap<>();
        for (int i = 0; i < attachSize; i++) {
            int keyLength = in.readInt();
            String key = in.readCharSequence(keyLength, Constants.CHARSET).toString();
            int valueLength = in.readInt();
            String value = in.readCharSequence(valueLength, Constants.CHARSET).toString();
            attach.put(key, value);
        }
        message.setAttachments(attach);
        // 读取消息体长度和消息体
        int messageBodyLength = in.readInt();
        String body = in.readCharSequence(messageBodyLength, Constants.CHARSET).toString();
        message.setBody(body);
        // 添加
        out.add(message);
    }
}
