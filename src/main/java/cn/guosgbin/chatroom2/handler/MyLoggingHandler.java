package cn.guosgbin.chatroom2.handler;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/5 22:58
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
@ChannelHandler.Sharable
public class MyLoggingHandler extends ChannelDuplexHandler {

    /* ======== 出站事件开始 ======== */

    /**
     * 当请求将 Channel 绑定到本地地址时被调用
     *
     * @param ctx
     * @param localAddress
     * @param promise
     * @throws Exception
     */
    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.debug("Channel [{}] 绑定到本地地址 : [{}]", ctx.channel(), localAddress);
        super.bind(ctx, localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.deregister(ctx, promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
    }

    /* ======== 出站事件结束 ======== */



    /* ======== 入站事件开始 ======== */

    /**
     * 当 Channel 已经注册到它的 EventLoop 并且能够处理 I/O 时被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("Channel [{}] 已经注册到对应的 EventLoop [{}]", channel, ctx.executor());
        super.channelRegistered(ctx);
    }

    /**
     * 当 Channel 从它的 EventLoop 注销并且无法处理任何 I/O 时被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NioEventLoop nioEventLoop = (NioEventLoop)ctx.executor();
        log.debug("Channel [{}] 已经从对应的 EventLoop [{}] 注销", channel, nioEventLoop);
        super.channelUnregistered(ctx);
    }

    /**
     * 当 Channel 处于活动状态时被调用； Channel 已经连接/绑定并且已经就绪
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("Channel [{}] 处于活动状态", channel);
        super.channelActive(ctx);
    }

    /**
     * 当 Channel 离开活动状态并且不再连接它的远程节点时被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * 当从 Channel 读取数据时被调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        log.debug("channel [{}] 接收到消息 {}", channel, msg);
        super.channelRead(ctx, msg);
    }

    /**
     * 当Channel上的一个读操作完成时被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("",cause);
        super.exceptionCaught(ctx, cause);
    }

    /* ======== 入站事件结束 ======== */

}
