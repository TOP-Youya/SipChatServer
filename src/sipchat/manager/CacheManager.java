package sipchat.manager;

import sipchat.dao.FriendEvent;
import sipchat.dao.GroupEvent;
import sipchat.dao.UserEvent;
import sipchat.enity.Friend;
import sipchat.enity.GroupMember;
import sipchat.enity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leeshun on 2017/7/11.
 */
public class CacheManager {
    private List<Friend> friends;
    private List<GroupMember> groupMembers;
    private List<User> users;

    private boolean isFriendChanged;
    private boolean isGroupMembersChanged;
    private boolean isUserChanged;

    private static CacheManager cacheManager;

    private CacheManager() {
        friends = FriendEvent.getAllFriendList();
        groupMembers = GroupEvent.getAllGroupMembers();
        users = UserEvent.getAllUser();

        isFriendChanged = false;
        isGroupMembersChanged = false;
        isUserChanged = false;
    }


    public synchronized static CacheManager getInstance() {
        if(cacheManager == null) {
            cacheManager = new CacheManager();
        }
        return cacheManager;
    }


    public boolean hasUser(String username) {
        for (User each : users) {
            if(each.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFriend(String owner,String frinedName) {
        if(isFriendChanged) {
            friends = FriendEvent.getAllFriendList();
            isFriendChanged = !isFriendChanged;
        }
        for (Friend each : friends) {
            if(each.getOwner().equals(owner) && each.getFriendName().equals(frinedName)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getAllFriends() {
        List<String> result = new ArrayList<>();
        if(isUserChanged) {
            users = UserEvent.getAllUser();
            isUserChanged = !isUserChanged;
        }
        for(User each : users) {
            result.add(each.getUsername());
            result.add(each.getIpAddress());
        }
        return result;
    }

    public List<String> getAllGroups() {
        List<String> result = new ArrayList<>();
        if(isGroupMembersChanged) {
            groupMembers = GroupEvent.getAllGroupMembers();
            isGroupMembersChanged = !isGroupMembersChanged;
        }
        for(GroupMember each : groupMembers) {
            if(!result.contains(each.getGroupName())) {
                result.add(each.getMemberName());
            }
        }
        return result;
    }

    public List<String> getFriendList(String owner) {
        List<String> result = new ArrayList<>();
        if(isFriendChanged) {
            friends = FriendEvent.getAllFriendList();
            isFriendChanged = !isFriendChanged;
        }
        for(Friend each : friends) {
            if(each.getOwner().equals(owner)) {
                result.add(each.getFriendName());
                result.add(getIPAddress(each.getFriendName()));
            }
        }
        return result;
    }

    public List<String> getGroupList(String username) {
        List<String> result = new ArrayList<>();
        if(isGroupMembersChanged) {
            groupMembers = GroupEvent.getAllGroupMembers();
            isGroupMembersChanged = !isGroupMembersChanged;
        }
        for(GroupMember each : groupMembers) {
            if(each.getMemberName().equals(username)) {
                result.add(each.getGroupName());
            }
        }
        return result;
    }

    public List<String> getGroupMember(String groupName) {
        List<String> result = new ArrayList<>();

        if(isGroupMembersChanged) {
            groupMembers = GroupEvent.getAllGroupMembers();
            isGroupMembersChanged = !isGroupMembersChanged;
        }

        for(GroupMember each : groupMembers) {
            if(each.getGroupName().equals(groupName)) {
                result.add(each.getMemberName());
            }
        }

        return result;
    }

    public List<String> getGroupMemberSipAddress(String groupName) {
        List<String> result = new ArrayList<>();
        if(isGroupMembersChanged || isUserChanged) {
            groupMembers = GroupEvent.getAllGroupMembers();
            users = UserEvent.getAllUser();
            isUserChanged = false;
            isGroupMembersChanged = false;
        }

        for(GroupMember each : groupMembers) {
            if(each.getMemberName().equals(groupName)) {
                for(User user : users) {
                    if(user.getUsername().equals(each.getMemberName())) {
                        result.add("sip:" + each.getMemberName() + "@" + user.getIpAddress() + ":5060");
                        break;
                    }
                }
            }
        }
        return result;
    }


    public List<String> getFriendsSipAddress(String username) {
        List<String> result = new ArrayList<>();
        if(isFriendChanged || isUserChanged) {
            friends = FriendEvent.getAllFriendList();
            users = UserEvent.getAllUser();
            isUserChanged = false;
            isFriendChanged = false;
        }

        for(Friend each : friends) {
            if(each.getOwner().equals(username)) {
                for(User user : users) {
                    if(user.getUsername().equals(each.getFriendName())) {
                        result.add("sip:" + each.getFriendName() + "@" + user.getIpAddress() + ":5060");
                        break;
                    }
                }
            }
        }
        return result;
    }

    public boolean matchPassword(String username,String password) {
        if(isUserChanged) {
            users = UserEvent.getAllUser();
            isUserChanged = !isUserChanged;
        }
        for(User each : users) {
            if(each.getUsername().equals(username)) {
                System.err.println(each.getPassword() + ":" + password);
                return each.getPassword().equals(password);
            }
        }
        return false;
    }

    public String getIPAddress(String username) {
        if(isUserChanged) {
            users = UserEvent.getAllUser();
            isUserChanged = !isUserChanged;
        }
        for(User each : users) {
            if(each.getUsername().equals(username)) {
                return each.getIpAddress();
            }
        }
        return "";
    }

    public void setFriendChanged(boolean friendChanged) {
        isFriendChanged = friendChanged;
    }

    public void setGroupMembersChanged(boolean groupMembersChanged) {
        isGroupMembersChanged = groupMembersChanged;
    }

    public void setUserChanged(boolean userChanged) {
        isUserChanged = userChanged;
    }
}
