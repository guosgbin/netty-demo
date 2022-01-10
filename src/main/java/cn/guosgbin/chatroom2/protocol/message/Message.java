package cn.guosgbin.chatroom2.protocol.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/9 0:02
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class Message {
    // 魔数
    private int magicNumber;
    // 主版本
    private byte mainVersion;
    // 次版本
    private byte subVersion;
    // 修订版本号
    private byte modifyVersion;
    // 会话 ID
    private String sessionId;
    // 消息类型
    private MessageTypeEnum messageType;
    // 附加信息
    private Map<String, String> attachments = new HashMap<>();
    // 消息主体
    private String body;

    public Map<String, String> getAttachments() {
        return Collections.unmodifiableMap(attachments);
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments.clear();
        if (null != attachments) {
            this.attachments.putAll(attachments);
        }
    }

    public void addAttachment(String key, String value) {
        attachments.put(key, value);
    }

    // getter setter
    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public byte getMainVersion() {
        return mainVersion;
    }

    public void setMainVersion(byte mainVersion) {
        this.mainVersion = mainVersion;
    }

    public byte getSubVersion() {
        return subVersion;
    }

    public void setSubVersion(byte subVersion) {
        this.subVersion = subVersion;
    }

    public byte getModifyVersion() {
        return modifyVersion;
    }

    public void setModifyVersion(byte modifyVersion) {
        this.modifyVersion = modifyVersion;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public MessageTypeEnum getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
