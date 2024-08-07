package frame;

import util.DBUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/17 15:00
 * @description
 **/

public class FrameLogin extends JFrame {
    private JTextField textFieldUsername;
    private JPasswordField textFieldPassword;

    public FrameLogin() {
        initView();
    }

    /**
     * 初始化界面
     */
    public void initView() {
        setTitle("高校学生就业管理系统");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel labelUser = new JLabel("用户名:");
        labelUser.setBounds(50, 50, 80, 25);
        panel.add(labelUser);

        textFieldUsername = new JTextField(20);
        textFieldUsername.setBounds(150, 50, 160, 25);
        panel.add(textFieldUsername);

        JLabel labelPassword = new JLabel("密码:");
        labelPassword.setBounds(50, 100, 80, 25);
        panel.add(labelPassword);

        textFieldPassword = new JPasswordField(20);
        textFieldPassword.setBounds(150, 100, 160, 25);
        panel.add(textFieldPassword);

        JButton btnLogin = new JButton("登录");
        btnLogin.setBounds(50, 150, 80, 25);
        panel.add(btnLogin);

        JButton btnReset = new JButton("重置");
        btnReset.setBounds(150, 150, 80, 25);
        panel.add(btnReset);

        add(panel);
        setVisible(true);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textFieldUsername.getText();
                String password = new String(textFieldPassword.getPassword());

                // 验证用户名和密码
                // TODO: 添加实际的验证逻辑
                if (verifyLogin(username, password)) {
                    // JOptionPane.showMessageDialog(null, "登录成功");
                    new FrameMain();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "用户名或密码错误");
                }
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldUsername.setText("");
                textFieldPassword.setText("");
            }
        });
    }


    /**
     * 登录：用户名、密码验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录验证结果
     */
    public boolean verifyLogin(String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();

            // 创建 PreparedStatement 对象
            String query = "SELECT * FROM users WHERE username = ? and password = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            // 执行查询语句
            resultSet = statement.executeQuery();

            // 获取查询结果
            return resultSet.next();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }
}

