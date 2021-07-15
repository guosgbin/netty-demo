package cn.guosgbin.chatroom.server.handler;

import cn.guosgbin.chatroom.message.RpcRequestMessage;
import cn.guosgbin.chatroom.message.RpcResponseMessage;
import cn.guosgbin.chatroom.server.service.HelloService;
import cn.guosgbin.chatroom.server.service.ServicesFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/7/14 0:10
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    // 服务端接收到客户端的请求会触发
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());
        try {
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(message.getInterfaceName()));

            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(e);
        }
        ctx.writeAndFlush(response);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage msg = new RpcRequestMessage(1,
                "cn.guosgbin.chatroom.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );
        HelloService service = (HelloService) ServicesFactory.getService(Class.forName(msg.getInterfaceName()));

        Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
        Object invoke = method.invoke(service, msg.getParameterValue());
        System.out.println(invoke);
    }
}
