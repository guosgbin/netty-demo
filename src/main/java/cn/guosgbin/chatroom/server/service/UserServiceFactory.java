package cn.guosgbin.chatroom.server.service;

/**
 * 用户service工厂，可根据不同UserService实现
 */
public abstract class UserServiceFactory {

    private static UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService() {
        return userService;
    }
}
