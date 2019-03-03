package ca.bcit.planters.treepost;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message {
    public Date timeStamp;
    public User owner;
    public String content;

    public Message() {}

    public Message(Date timeStamp, User owner, String content) {
        this.timeStamp = timeStamp;
        this.owner = owner;
        this.content = content;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("timeStamp", timeStamp);
        result.put("owner", owner);
        result.put("content", content);

        return result;
    }
}
