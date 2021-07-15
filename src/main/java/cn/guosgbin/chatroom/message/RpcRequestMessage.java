package cn.guosgbin.chatroom.message;

import lombok.Getter;
import lombok.ToString;

/**
 * RPC请求消息
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/13 23:41
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Getter
@ToString(callSuper = true)
public class RpcRequestMessage extends Message{

    /**
     * 调用接口的全限定名
     * 服务端根据这个找到对应的实现
     */
    private String interfaceName;

    /**
     * 调用接口中的方法名
     */
    private String methodName;

    /**
     * 方法返回类型
     */
    private Class<?> returnType;

    /**
     * 方法参数类型数组
     */
    private Class[] parameterTypes;

    /**
     * 方法参数值数组
     */
    private Object[] parameterValue;

    public RpcRequestMessage(int sequenceId,String interfaceName, String methodName, Class<?> returnType,
                             Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
