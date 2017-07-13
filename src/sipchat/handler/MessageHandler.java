package sipchat.handler;

import javax.sip.header.FromHeader;

/**
 * Created by leeshun on 2017/7/11.
 */
public interface MessageHandler {
    String onFriendList(String rawMessage);
    String onGroupList(String rawMessage);
    String onGroupMessage(FromHeader header, String rawMessage);
    String onUpdatePassword(String username, String rawMessage);
    String onJoinGroup(FromHeader username, String rawMessage);
    String onExitGroup(FromHeader username, String rawMessage);
    String onCreateGroup(String displayName, String rawMessage);
    String onLogin(String displayName, String rawMessage);
    String onAllFriends();
    String onAllGroups();
}
