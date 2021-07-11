package cn.guosgbin.chatroom.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage {

    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    public LoginResponseMessage() {
    }

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}
