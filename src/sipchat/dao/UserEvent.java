package sipchat.dao;

import sipchat.enity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by leeshun on 2017/7/11.
 */
public class UserEvent {

    public static int addUser(String userName,String password,String ipAddress){
        System.err.println("into adduser");
        String sql="insert into user (userName,password,ipAddress) values(?,?,?)";   //SQL语句
        List<String> list = new LinkedList<>();
        list.add(userName);
        list.add(password);
        list.add(ipAddress);
        return SimpleOperator.executeINT(sql,list);
    }//增加用户

    public static int updatePassword(String userName,String newPassword){
        String sql="update user set password = ? where userName = ?";
        List<String> list=new LinkedList<>();
        list.add(newPassword);
        list.add(userName);
        return SimpleOperator.executeINT(sql,list);
    }//更新密码,0是失败，1是成功

    public static int updateIPAddress(String userName,String IPAddress) {
        String sql="update user set ipAddress = ? where userName = ?";
        List<String> list=new LinkedList<>();
        list.add(IPAddress);
        list.add(userName);
        return SimpleOperator.executeINT(sql,list);
    }

    public static List<User> getAllUser(){
        Connection conn = SimpleOperator.getConnection();
        String sql = "select * from user";
        PreparedStatement pstmt;
        List<User> lu=new LinkedList<User>();
        try {
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                User u=new User(rs.getString(1),rs.getString(2),rs.getString(3));
                lu.add(u);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return lu;
    }//获得用户表所有数据

    public static int deleteUser(String userName){
        String sql = "delete from user where userName = ?";
        List<String> ls=new LinkedList<>();
        ls.add(userName);
        return SimpleOperator.executeINT(sql,ls);
    }//删除一个用户


    public static String getPassword(String userName){
        String sql = "select user.password from user where userName='"+userName+"'";
        return SimpleOperator.executeSTRING(sql);
    }//获得特定用户的密码

    public static String getIpAddress(String userName){
        String sql = "select user.ipAddress from user where userName='"+userName+"'";
        return SimpleOperator.executeSTRING(sql);
    }//获得特定用户的ip地址

}
