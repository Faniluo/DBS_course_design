package frame;

import util.CheckLegalityUtil;
import util.ComboBoxUtil;
import util.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/17 22:14
 * @description
 **/
public class FrameUpdateInfo extends JFrame {
    private final DefaultTableModel tableModel;
    private final int selectedRowIndex;
    private JTextField textFieldStuId;
    private JComboBox<String> comboBoxEmpStatus;
    private JComboBox<String> comboBoxComName;
    private JComboBox<String> comboBoxJobTitle;

    private final onChildFrameCloseListener onChildFrameCloseListener;

    public FrameUpdateInfo(DefaultTableModel tableModel, int selectedRowIndex, onChildFrameCloseListener onChildFrameCloseListener) {
        this.tableModel = tableModel;
        this.selectedRowIndex = selectedRowIndex;
        this.onChildFrameCloseListener = onChildFrameCloseListener;
        initView();
    }

    private void initView() {
        setTitle("修改毕业生信息");
        setSize(350, 370);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel.getValueAt(selectedRowIndex, 0);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel label1 = new JLabel("学    号:");
        label1.setBounds(50, 60, 70, 25);
        panel.add(label1);

        textFieldStuId = new JTextField(20);
        textFieldStuId.setBounds(120, 60, 160, 25);
        textFieldStuId.setText(String.valueOf((int) tableModel.getValueAt(selectedRowIndex, 0)));
        textFieldStuId.setEditable(false);
        panel.add(textFieldStuId);

        JLabel label2 = new JLabel("姓    名:");
        label2.setBounds(50, 90, 70, 25);
        panel.add(label2);

        JTextField textFieldStuName = new JTextField(20);
        textFieldStuName.setBounds(120, 90, 160, 25);
        textFieldStuName.setText((String) tableModel.getValueAt(selectedRowIndex, 1));
        panel.add(textFieldStuName);

        JLabel labelGender = new JLabel("性    别:");
        labelGender.setBounds(50, 120, 70, 25);
        panel.add(labelGender);

        JComboBox<String> comboBoxGender = new JComboBox<>();
        comboBoxGender.setBounds(120, 120, 160, 25);
        comboBoxGender.addItem("男");
        comboBoxGender.addItem("女");
        if ("男".equals(tableModel.getValueAt(selectedRowIndex, 2)))
            comboBoxGender.setSelectedItem(comboBoxGender.getItemAt(0));
        else
            comboBoxGender.setSelectedItem(comboBoxGender.getItemAt(1));
        panel.add(comboBoxGender);

        JLabel labelEmpStatus = new JLabel("就业状态:");
        labelEmpStatus.setBounds(50, 150, 70, 25);
        panel.add(labelEmpStatus);

        comboBoxEmpStatus = new JComboBox<>();
        comboBoxEmpStatus.setBounds(120, 150, 160, 25);
        comboBoxEmpStatus.addItem("待业");
        comboBoxEmpStatus.addItem("就业");
        if ("待业".equals(tableModel.getValueAt(selectedRowIndex, 3)))
            comboBoxEmpStatus.setSelectedItem(comboBoxEmpStatus.getItemAt(0));
        else
            comboBoxEmpStatus.setSelectedItem(comboBoxEmpStatus.getItemAt(1));
        panel.add(comboBoxEmpStatus);

        JLabel labelComName = new JLabel("公司名:");
        labelComName.setBounds(50, 180, 70, 25);
        panel.add(labelComName);

        comboBoxComName = new JComboBox<>();
        comboBoxComName.setBounds(120, 180, 160, 25);
        panel.add(comboBoxComName);
        loadComboBoxComName();

        JLabel labelJobTitle = new JLabel("工    作:");
        labelJobTitle.setBounds(50, 210, 70, 25);
        panel.add(labelJobTitle);

        comboBoxJobTitle = new JComboBox<>();
        comboBoxJobTitle.setBounds(120, 210, 160, 25);
        panel.add(comboBoxJobTitle);
        loadComboBoxJobTitle((String) comboBoxComName.getSelectedItem());

        JButton btnModify = new JButton("修改");
        btnModify.setBounds(130, 240, 80, 25);
        panel.add(btnModify);

        add(panel);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (onChildFrameCloseListener != null) {
                    onChildFrameCloseListener.onUpdateFrameClosed();
                }
            }
        });

        comboBoxComName.addActionListener(e -> loadComboBoxJobTitle((String) comboBoxComName.getSelectedItem()));

        btnModify.addActionListener(e -> {
            if ("就业".equals(String.valueOf(comboBoxEmpStatus.getSelectedItem()))) {
                updateEmpRecord();
            } else {
                deleteEmpRecord();
            }
        });
    }

    private void updateEmpRecord() {
        // todo: 更改就业记录
        Connection connection = null;
        PreparedStatement statementUpdateStu = null;
        String comName = (String) comboBoxComName.getSelectedItem();
        String jobTitle = (String) comboBoxJobTitle.getSelectedItem();

        try {
            connection = DBUtil.getConnection();
            String sql = "update employment_records\n" +
                    "set job_id = ?\n" +
                    "where student_id = ?";

            if (CheckLegalityUtil.checkHireNum(comName, jobTitle)) {
                connection.setAutoCommit(false);
                statementUpdateStu = connection.prepareStatement(sql);
                statementUpdateStu.setInt(1, getJobId(jobTitle, comName));
                statementUpdateStu.setInt(2, Integer.parseInt(textFieldStuId.getText()));
                statementUpdateStu.executeUpdate();
                connection.commit();

                JOptionPane.showMessageDialog(null, "修改成功");
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "招聘人数已满");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "请输入正确信息");
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(null, statementUpdateStu, connection);
        }
    }

    private void deleteEmpRecord() {
        // todo: 删除就业记录
        Connection connection = null;
        PreparedStatement statementUpdateStu = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "delete\n" +
                    "from employment_records\n" +
                    "where student_id = ?";

            connection.setAutoCommit(false);
            statementUpdateStu = connection.prepareStatement(sql);
            statementUpdateStu.setInt(1, Integer.parseInt(textFieldStuId.getText()));
            statementUpdateStu.executeUpdate();
            connection.commit();

            JOptionPane.showMessageDialog(null, "删除就业记录成功");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "请输入正确信息");
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

            // 设置界面打开时选中选项
            String selectedContent = (String) tableModel.getValueAt(selectedRowIndex, 6);
            int selectedContentIndex = ComboBoxUtil.findIndexOfContent(comboBoxComName, selectedContent);
            comboBoxComName.setSelectedItem(comboBoxComName.getItemAt(selectedContentIndex));

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

            // 设置界面打开时选中选项
            String selectedContent = (String) tableModel.getValueAt(selectedRowIndex, 4);
            int selectedContentIndex = ComboBoxUtil.findIndexOfContent(comboBoxJobTitle, selectedContent);
            comboBoxJobTitle.setSelectedItem(comboBoxJobTitle.getItemAt(selectedContentIndex));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

}
