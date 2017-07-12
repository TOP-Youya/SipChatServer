package sipchat.enity;

/**
 * Created by leeshun on 2017/7/11.
 */
public class GroupMember {
    private String groupName;
    private String memberName;

    public GroupMember(String groupName, String memberName) {
        this.groupName = groupName;
        this.memberName = memberName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
