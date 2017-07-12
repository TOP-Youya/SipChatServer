package sipchat.dao;


import com.mysql.jdbc.ConnectionProperties;

import javax.sql.ConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leeshun on 2017/7/11.
 */
public class SimpleOperator {
    private static Connection connection;

    public synchronized static Connection getConnection() {
        if(connection == null) {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/sipchatroom";
            String username = "root";
            String password = "root";
            try {
                Class.forName(driver);
                connection = DriverManager.getConnection(url,username,password);
            } catch (Throwable throwable) {
                System.err.println("sql connection initialized error " + throwable.getMessage());
            }
        }
        return connection;
    }

    public static int executeINT(String sql, List<String> params) {
        Connection connection = SimpleOperator.getConnection();
        int result = 0;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(sql);
            for(int i = 0;i < params.size();++i) {
                preparedStatement.setString(i,params.get(i));
            }
            result = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Throwable throwable) {
            System.err.println(throwable.getMessage());
        }

        return result;
    }

    public static String executeSTRING(String sql) {
        Connection connection = SimpleOperator.getConnection();
        PreparedStatement preparedStatement;
        String result = "";
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                result = set.getString(1);
            }
        } catch (Throwable throwable) {
            System.err.println(throwable.getMessage());
        }
        return result;
    }


    public static List<String> executeSTRINGLIST(String sql) {
        Connection connection = SimpleOperator.getConnection();
        PreparedStatement preparedStatement;
        List<String> result = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                result.add(set.getString(1));
            }
        } catch (Throwable throwable) {
            System.err.println(throwable.getMessage());
        }
        return result;
    }
}
