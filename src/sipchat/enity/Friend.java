package sipchat.enity;

/**
 * Created by leeshun on 2017/7/11.
 */
public class Friend {
    private String owner;
    private String friendName;

    public Friend(String owner, String friendName) {
        this.owner = owner;
        this.friendName = friendName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
