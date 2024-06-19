package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Properties;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/17 14:57
 * @description
 **/
public class DBUtil {
    public static final String URL;
    public static final String USER;
    public static final String PASSWORD;
    public static final String DRIVER;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src\\mysql.properties"));
            // 获取相关属性值
            USER = properties.getProperty("user");
            PASSWORD = properties.getProperty("password");
            URL = properties.getProperty("url");
            DRIVER = properties.getProperty("driver");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Connection failed!");
        }
        return connection;
    }

    /**
     * 释放资源
     *
     * @param resultSet  结果集
     * @param statement  查询
     * @param connection 连接
     */
    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
        try {
            // 判断是否为空
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
