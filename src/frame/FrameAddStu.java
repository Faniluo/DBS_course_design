package frame;

import util.DBUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/19 0:15
 * @description
 **/
public class FrameAddStu extends JFrame {

    private JTextField textFieldStuId;
    private JTextField textFieldStuName;
    private JComboBox<String> comboBoxMajor;
    private JComboBox<String> comboBoxDept;
    private JComboBox<String> comboBoxGender;

    public FrameAddStu() {
        initView();
    }

    private void initView() {
        setTitle("添加学生信息");
        setSize(355, 270);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel labelStuId = new JLabel("学    号:");
        labelStuId.setBounds(45, 20, 70, 25);
        panel.add(labelStuId);

        textFieldStuId = new JTextField(20);
        textFieldStuId.setBounds(120, 20, 160, 25);
        panel.add(textFieldStuId);

        JLabel labelStuName = new JLabel("姓    名:");
        labelStuName.setBounds(45, 50, 70, 25);
        panel.add(labelStuName);

        textFieldStuName = new JTextField(20);
        textFieldStuName.setBounds(120, 50, 160, 25);
        panel.add(textFieldStuName);

        JLabel labelGender = new JLabel("性    别:");
        labelGender.setBounds(45, 80, 70, 25);
        panel.add(labelGender);

        comboBoxGender = new JComboBox<>();
        comboBoxGender.setBounds(120, 80, 160, 25);
        comboBoxGender.addItem(null);
        comboBoxGender.addItem("男");
        comboBoxGender.addItem("女");
        panel.add(comboBoxGender);

        JLabel labelDept = new JLabel("学    院:");
        labelDept.setBounds(45, 110, 80, 25);
        panel.add(labelDept);

        comboBoxDept = new JComboBox<>();
        comboBoxDept.setBounds(120, 110, 160, 25);
        loadDeptComboBoxData();
        panel.add(comboBoxDept);

        JLabel labelMajor = new JLabel("专    业:");
        labelMajor.setBounds(45, 140, 70, 25);
        panel.add(labelMajor);

        comboBoxMajor = new JComboBox<>();
        comboBoxMajor.setBounds(120, 140, 160, 25);
        panel.add(comboBoxMajor);

        JButton btnAddGraduate = new JButton("添加工作");
        btnAddGraduate.setBounds(130, 180, 80, 25);
        panel.add(btnAddGraduate);

        add(panel);
        setVisible(true);

        btnAddGraduate.addActionListener(e -> addStu());

        comboBoxDept.addActionListener(e -> loadMajorComboBoxData((String) comboBoxDept.getSelectedItem()));
    }

    private void addStu() {
        Connection connection = null;
        PreparedStatement statementAddStu = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "INSERT INTO students(student_id, student_name, gender, major_id) " +
                    " VALUES (?, ?, ?, ?);";

            String strStuId = textFieldStuId.getText();
            String stuName = textFieldStuName.getText();
            String gender = (String) comboBoxGender.getSelectedItem();
            String majorName = (String) comboBoxMajor.getSelectedItem();

            if (strStuId != null && stuName != null && gender != null && majorName != null) {
                int stuId = Integer.parseInt(strStuId);
                int majorId = getMajorId(majorName);

                connection.setAutoCommit(false);
                statementAddStu = connection.prepareStatement(sql);
                statementAddStu.setInt(1, stuId);
                statementAddStu.setString(2, stuName);
                statementAddStu.setString(3, gender);
                statementAddStu.setInt(4, majorId);
                statementAddStu.executeUpdate();
                connection.commit();

                JOptionPane.showMessageDialog(null, "添加毕业生成功");
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "请检查并填写完整信息");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "记录已存在或信息错误");
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(null, statementAddStu, connection);
        }
    }

    private int getMajorId(String majorName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "SELECT DISTINCT major_id from majors where major_name = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, majorName);
            resultSet = statement.executeQuery();

            int majorId = 0;
            while (resultSet.next()) {
                majorId = resultSet.getInt("major_id");
            }

            return majorId;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    private void loadDeptComboBoxData() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "SELECT * FROM departments";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            comboBoxDept.addItem(null);

            while (resultSet.next()) {
                String deptName = resultSet.getString("dept_name");
                comboBoxDept.addItem(deptName);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    private void loadMajorComboBoxData(String deptName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "SELECT * FROM majors WHERE dept_id IN (SELECT dept_id FROM departments WHERE dept_name = ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, deptName);
            resultSet = statement.executeQuery();

            comboBoxMajor.removeAllItems();
            comboBoxMajor.addItem(null);
            while (resultSet.next()) {
                comboBoxMajor.addItem(resultSet.getString("major_name"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    public static void main(String[] args) {
        new FrameAddStu();
    }
}
