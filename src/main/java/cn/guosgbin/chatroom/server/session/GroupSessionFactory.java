package cn.guosgbin.chatroom.server.session;

/**
 * GroupSession的创建工厂
 */
public abstract class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return session;
    }
}
