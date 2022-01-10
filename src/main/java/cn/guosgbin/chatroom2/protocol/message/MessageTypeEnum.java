package cn.guosgbin.chatroom2.protocol.message;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/9 0:02
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 *
 * Ping和Pong消息的作用一般是，在服务处于闲置状态达到一定时长，
 * 比如2s时，客户端服务会向服务端发送一个Ping消息，则会返回一个Pong消息，
 * 这样才表示客户端与服务端的连接是完好的。
 *
 * 如果服务端没有返回相应的消息，客户端就会关闭与服务端的连接或者是重新建立与服务端的连接。
 * 这样的优点在于可以防止突然会产生的客户端与服务端的大量交互。
 */
public enum MessageTypeEnum {
    REQUEST((byte) 1), // 请求消息
    RESPONSE((byte) 2), // 响应消息
    PING((byte) 3),
    PONG((byte) 4),
    EMPTY((byte) 5), // 表示当前是一个空消息，该消息不会写入数据管道中
    ;

    private byte type;

    MessageTypeEnum(byte type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static MessageTypeEnum get(byte type) {
        for (MessageTypeEnum value : values()) {
            if (value.type == type) {
                return value;
            }
        }

        throw new RuntimeException("unsupported type: " + type);
    }

}
