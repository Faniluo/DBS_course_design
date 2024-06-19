package frame;

import util.DBUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/18 19:54
 * @description
 **/
public class FrameAddJob extends JFrame {
    private JTextField textFieldJobTitle;
    private JTextField textFieldComName;
    private JTextField textFieldJobType;
    private JTextField textFieldDemandNum;
    private JTextField textFieldHireNum;

    public FrameAddJob() {
        initView();
    }

    private void initView() {
        setTitle("添加工作信息");
        setSize(355, 270);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel labelComName = new JLabel("公司名:");
        labelComName.setBounds(45, 20, 70, 25);
        panel.add(labelComName);

        textFieldComName = new JTextField(20);
        textFieldComName.setBounds(120, 20, 160, 25);
        panel.add(textFieldComName);

        JLabel labelJobTitle = new JLabel("工    作:");
        labelJobTitle.setBounds(45, 50, 70, 25);
        panel.add(labelJobTitle);

        textFieldJobTitle = new JTextField(20);
        textFieldJobTitle.setBounds(120, 50, 160, 25);
        panel.add(textFieldJobTitle);

        JLabel labelJobType = new JLabel("工作类型:");
        labelJobType.setBounds(45, 80, 70, 25);
        panel.add(labelJobType);

        textFieldJobType = new JTextField(20);
        textFieldJobType.setBounds(120, 80, 160, 25);
        panel.add(textFieldJobType);

        JLabel labelDemandNum = new JLabel("需求人数:");
        labelDemandNum.setBounds(45, 110, 70, 25);
        panel.add(labelDemandNum);

        textFieldDemandNum = new JTextField(20);
        textFieldDemandNum.setBounds(120, 110, 160, 25);
        panel.add(textFieldDemandNum);

        JLabel labelHireNum = new JLabel("已聘人数:");
        labelHireNum.setBounds(45, 140, 70, 25);
        panel.add(labelHireNum);

        textFieldHireNum = new JTextField(20);
        textFieldHireNum.setBounds(120, 140, 160, 25);
        panel.add(textFieldHireNum);

        JButton btnAddJob = new JButton("添加工作");
        btnAddJob.setBounds(130, 180, 80, 25);
        panel.add(btnAddJob);

        add(panel);
        setVisible(true);

        btnAddJob.addActionListener(e -> addJob());
    }

    public void addJob() {
        Connection connection = null;
        PreparedStatement statementAddJob = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "insert into jobs(job_title, job_type, demand_number, hire_number, company_name)" +
                    "VALUES (?, ?, ?, ?, ?);";

            String jobTitle = textFieldJobTitle.getText();
            String jobType = textFieldJobType.getText();
            String strDemandNum = textFieldDemandNum.getText();
            String strHireNum = textFieldHireNum.getText();
            String comName = textFieldComName.getText();

            if (jobTitle != null && jobType != null && strDemandNum != null && strHireNum != null && comName != null) {
                int demandNum = Integer.parseInt(strDemandNum);
                int hireNum = Integer.parseInt(strHireNum);

                connection.setAutoCommit(false);
                statementAddJob = connection.prepareStatement(sql);
                statementAddJob.setString(1, jobTitle);
                statementAddJob.setString(2, jobType);
                statementAddJob.setInt(3, demandNum);
                statementAddJob.setInt(4, hireNum);
                statementAddJob.setString(5, comName);
                statementAddJob.executeUpdate();
                connection.commit();

                JOptionPane.showMessageDialog(null, "添加工作成功");
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "请检查并填写完整信息");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "记录已存在或信息错误");
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(null, statementAddJob, connection);
        }
    }

    public static void main(String[] args) {
        new FrameAddJob();
    }

}
