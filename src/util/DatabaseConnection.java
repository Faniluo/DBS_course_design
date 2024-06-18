package util;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/17 14:57
 * @description
 **/
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_employment_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

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
