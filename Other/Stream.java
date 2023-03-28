package Other;

import java.io.Serializable;

public class Stream implements Serializable {

    int msgCode;
    String Message;

    public Stream(int msgCode, String message) {
        this.msgCode = msgCode;
        Message = message;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(int msgCode) {
        this.msgCode = msgCode;
    }
}
