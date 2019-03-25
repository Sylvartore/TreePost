package ca.bcit.planters.treepost;

public class User {
    public String userId;
    public String email;
    public String nickname;

    public User(){}

    public User(String userId, String email, String nickname) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
    }

    void SetNickName(String nickname) {
        this.nickname = nickname;
    }
}
