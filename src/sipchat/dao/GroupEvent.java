package sipchat.dao;

import sipchat.enity.GroupMember;
import sipchat.manager.CacheManager;

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
public class GroupEvent {

    public static int addGroupMember(String groupName,String memberName) {
        String sql = "insert into groupmember (groupName,memberName) values(?,?)";
        List<String> list = new ArrayList<>();
        System.err.println("222222" + groupName);
        list.add(groupName);
        list.add(memberName);
        return SimpleOperator.executeINT(sql,list);
    }

    public static List<GroupMember> getAllGroupMembers() {
        Connection conn = SimpleOperator.getConnection();
        String sql = "select *  from groupmember ";
        PreparedStatement pstmt;
        List<GroupMember> lu=new LinkedList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                GroupMember g=new GroupMember(rs.getString(2),rs.getString(3));
                lu.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lu;
    }

    public static int deleteMember(String groupName, String memberName) {
        String sql = "delete from groupmember where groupName = ? and memberName = ?";
        List<String> ls=new LinkedList<String>();
        return SimpleOperator.executeINT(sql,ls);
    }
}
