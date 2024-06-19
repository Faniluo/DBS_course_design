package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/19 13:35
 * @description
 **/
public class CheckLegalityUtil {
    public static boolean checkHireNum(String comName, String jobTitle) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "SELECT * FROM jobs where company_name = ? and job_title = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, comName);
            statement.setString(2, jobTitle);
            resultSet = statement.executeQuery();

            int demandNum = 0;
            int hireNum = 0;
            while (resultSet.next()) {
                demandNum = resultSet.getInt("demand_number");
                hireNum = resultSet.getInt("hire_number");
            }

            return demandNum > hireNum;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }
}
