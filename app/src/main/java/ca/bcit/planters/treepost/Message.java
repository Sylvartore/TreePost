package ca.bcit.planters.treepost;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message {
    public String msgId;
    public Date timeStamp;
    public User owner;
    public User receiver;
    public String content;
    public Map<String, Message> replies;

    public Message() {
    }

    public Message(String msgId, Date timeStamp, User owner, String content) {
        this.msgId = msgId;
        this.timeStamp = timeStamp;
        this.owner = owner;
        this.content = content;
        replies = new HashMap<>();
        receiver = null;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("msgId", msgId);
        result.put("timeStamp", timeStamp);
        result.put("owner", owner);
        result.put("content", content);
        result.put("replies", replies);
        if (receiver != null) result.put("receiver", receiver);
        return result;
    }

    public String getOwnerEmail() {
        if (owner != null)
            return owner.email;
        else
            return null;
    }
}
