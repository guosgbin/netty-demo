package cn.guosgbin.chatroom.client;

import cn.guosgbin.chatroom.message.*;
import cn.guosgbin.chatroom.protocol.MessageCodecSharable;
import cn.guosgbin.chatroom.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 聊天室客户端
 */
@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 日志Handler 无状态 可共享
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        // 自定义协议 编码解码器 无状态 可共享
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        // 线程通信
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 协议解码器 客户端 <- 服务端 入站 入站解码
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(new IdleStateHandler(0,2,1));
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了写空闲事件
                            if (event.state() == IdleState.WRITER_IDLE) {
                                log.debug("3s 没有写数据了，发送一个心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        // 会在服务端和客户端发生连接时触发
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(() -> {
                                Scanner sc = new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String username = sc.nextLine();
                                System.out.println("请输入密码");
                                String password = sc.nextLine();
                                // 构造登录消息
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                // 发送登录消息
                                ctx.writeAndFlush(message);
                                System.out.println("等待后续操作...");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if (!LOGIN.get()) {
                                    // 登录失败
                                    ctx.channel().close();
                                    return;
                                }

                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = sc.nextLine();
                                    String[] cs = command.split(" ");
                                    switch (cs[0]) {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, cs[1], cs[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, cs[1], cs[2]));
                                            break;
                                        case "gcreate":
                                            HashSet<String> members = new HashSet<>(Arrays.asList(cs[2].split(",")));
                                            // 添加自己到群组
                                            members.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(cs[1], members));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(cs[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, cs[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, cs[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                    }

                                }
                            }, "systemIn").start();
                        }

                        // 获取来自服务端的消息时 触发
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}", msg);
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage message = (LoginResponseMessage) msg;
                                if (message.isSuccess()) {
                                    // 登录成功
                                    LOGIN.set(true);

                                }
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }
                    });

                }
            });
            Channel channel = bootstrap.connect("localhost", 10086).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
