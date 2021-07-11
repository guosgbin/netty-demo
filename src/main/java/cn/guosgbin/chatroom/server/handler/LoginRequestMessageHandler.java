package cn.guosgbin.chatroom.server.handler;


import cn.guosgbin.chatroom.message.LoginRequestMessage;
import cn.guosgbin.chatroom.message.LoginResponseMessage;
import cn.guosgbin.chatroom.server.service.UserService;
import cn.guosgbin.chatroom.server.service.UserServiceFactory;
import cn.guosgbin.chatroom.server.session.Session;
import cn.guosgbin.chatroom.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户登录请求 会在服务端和客户端建立连接之后 客户端会在连接建立的方法里发送LoginRequestMessage消息
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/11 15:13
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();

        UserService userService = UserServiceFactory.getUserService();
        boolean login = userService.login(username, password);
        LoginResponseMessage message = null;
        if (login) {
            // 登录 绑定到map中
            SessionFactory.getSession().bind(ctx.channel(), username);
            message = new LoginResponseMessage(true, "登录成功");
        } else {
            message = new LoginResponseMessage(false, "登录失败, 用户名或密码错误");
        }
        log.debug("是否登录成功: {}", login);
        ctx.writeAndFlush(message);
    }
}
