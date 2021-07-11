package cn.guosgbin.chatroom.server;

import cn.guosgbin.chatroom.protocol.MessageCodecSharable;
import cn.guosgbin.chatroom.protocol.ProcotolFrameDecoder;
import cn.guosgbin.chatroom.server.handler.ChatRequestMessageHandler;
import cn.guosgbin.chatroom.server.handler.GroupChatRequestMessageHandler;
import cn.guosgbin.chatroom.server.handler.GroupCreateRequestMessageHandler;
import cn.guosgbin.chatroom.server.handler.LoginRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天室服务端
 */
@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        // 登录
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        // 单聊
        ChatRequestMessageHandler PRIVATE_CHAT_HANDLER = new ChatRequestMessageHandler();
        // 创建群组
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        // 群组聊天
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();



        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(LOGIN_HANDLER);
                    ch.pipeline().addLast(PRIVATE_CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                }
            });
            Channel channel = serverBootstrap.bind(10086).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
