package frame;

import util.CheckLegalityUtil;
import util.DBUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/19 0:15
 * @description
 **/
public class FrameAddEmpRecord extends JFrame {

    private JTextField textFieldStuId;
    private JTextField textFieldStuName;
    private JComboBox<String> comboBoxComName;
    private JComboBox<String> comboBoxJobTitle;

    public FrameAddEmpRecord() {
        initView();
    }

    private void initView() {
        setTitle("添加毕业生就业记录");
        setSize(355, 240);
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

        JLabel labelComName = new JLabel("公司名:");
        labelComName.setBounds(45, 80, 70, 25);
        panel.add(labelComName);

        comboBoxComName = new JComboBox<>();
        comboBoxComName.setBounds(120, 80, 160, 25);
        panel.add(comboBoxComName);
        loadComboBoxComName();

        JLabel labelJobTitle = new JLabel("工    作:");
        labelJobTitle.setBounds(45, 110, 70, 25);
        panel.add(labelJobTitle);

        comboBoxJobTitle = new JComboBox<>();
        comboBoxJobTitle.setBounds(120, 110, 160, 25);
        panel.add(comboBoxJobTitle);
        loadComboBoxJobTitle((String) comboBoxComName.getSelectedItem());

        JButton btnAddEmpRecord = new JButton("添加");
        btnAddEmpRecord.setBounds(130, 150, 80, 25);
        panel.add(btnAddEmpRecord);

        add(panel);
        setVisible(true);

        comboBoxComName.addActionListener(e -> loadComboBoxJobTitle((String) comboBoxComName.getSelectedItem()));

        btnAddEmpRecord.addActionListener(e -> insertEmpRecord());

        textFieldStuId.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }

            private void textChanged() {
                loadStuName();
            }
        });
    }

    private void loadStuName() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "SELECT student_name FROM students where student_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(textFieldStuId.getText()));
            resultSet = statement.executeQuery();

            String stuName = null;
            while (resultSet.next()) {
                stuName = resultSet.getString("student_name");
            }

            if (stuName != null)
                textFieldStuName.setText(stuName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    private void insertEmpRecord() {
        // todo: 添加就业记录
        Connection connection = null;
        PreparedStatement statementUpdateStu = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "insert into employment_records(student_id, job_id)" +
                    "VALUES (?, ?);";

            String jobTitle = String.valueOf(comboBoxJobTitle.getSelectedItem());
            String comName = String.valueOf(comboBoxComName.getSelectedItem());

            if (CheckLegalityUtil.checkHireNum(comName, jobTitle)) {
                connection.setAutoCommit(false);
                statementUpdateStu = connection.prepareStatement(sql);
                statementUpdateStu.setInt(1, Integer.parseInt(textFieldStuId.getText()));
                statementUpdateStu.setInt(2, getJobId(jobTitle, comName));
                statementUpdateStu.executeUpdate();
                connection.commit();

                JOptionPane.showMessageDialog(null, "添加成功");
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "招聘人数已满或信息填写错误");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "记录已存在或信息错误");
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(null, statementUpdateStu, connection);
        }
    }

    private int getJobId(String jobTitle, String comName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "SELECT job_id FROM jobs where job_title = ? and company_name = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, jobTitle);
            statement.setString(2, comName);
            resultSet = statement.executeQuery();

            int jobId = 0;
            while (resultSet.next()) {
                jobId = resultSet.getInt("job_id");
            }

            return jobId;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    private void loadComboBoxComName() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "SELECT DISTINCT company_name FROM jobs";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            comboBoxComName.addItem(null);

            while (resultSet.next()) {
                String comName = resultSet.getString("company_name");
                comboBoxComName.addItem(comName);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    private void loadComboBoxJobTitle(String comName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "SELECT * FROM jobs WHERE company_name = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, comName);
            resultSet = statement.executeQuery();

            comboBoxJobTitle.removeAllItems();
            comboBoxJobTitle.addItem(null);
            while (resultSet.next()) {
                comboBoxJobTitle.addItem(resultSet.getString("job_title"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    public static void main(String[] args) {
        new FrameAddEmpRecord();
    }

}
