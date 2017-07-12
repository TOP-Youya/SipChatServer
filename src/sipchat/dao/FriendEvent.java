package sipchat.dao;

import sipchat.enity.Friend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by leeshun on 2017/7/11.
 */
public class FriendEvent {
    public static int addFriend(String ownerName, String friendName)  {
        String sql="insert into friendlist (owner,friendName) values(?,?)";
        List<String> list = new ArrayList<>();
        list.add(ownerName);
        list.add(friendName);
        return SimpleOperator.executeINT(sql,list);
    }

    public static int deleteFriend(String ownerName, String friendName) {
        String sql = "delete from friendlist where owner = ? and friendName = ?";
        List<String> list = new ArrayList<>();
        list.add(ownerName);
        list.add(friendName);
        return SimpleOperator.executeINT(sql,list);
    }

    public static List<Friend> getAllFriendList() {
        Connection conn = SimpleOperator.getConnection();
        String sql = "select * from friendlist";
        PreparedStatement pstmt;
        List<Friend> lu=new LinkedList<Friend>();
        try {
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Friend f=new Friend(rs.getString(2),rs.getString(3));
                lu.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lu;
    }

    public static List<String> getFriends(String owner) {
        String sql = "select friendlist.friendName from user where owner='"+owner+"'";
        return SimpleOperator.executeSTRINGLIST(sql);
    }

}
