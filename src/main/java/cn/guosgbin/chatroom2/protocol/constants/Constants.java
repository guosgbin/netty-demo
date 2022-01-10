package cn.guosgbin.chatroom2.protocol.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/9 17:53
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class Constants {
    public static final int MAGIC_NUMBER = 2130;
    public static final int MAIN_VERSION = 1;
    public static final int SUB_VERSION = 1;
    public static final int MODIFY_VERSION = 1;

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final int SESSION_ID_LENGTH = 36;
}
