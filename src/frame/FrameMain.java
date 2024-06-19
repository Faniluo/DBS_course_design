package frame;

import util.CustomRowColorRenderer;
import util.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/17 15:00
 * @description
 **/

public class FrameMain extends JFrame implements onChildFrameCloseListener {
    private JComboBox<String> comboBoxDept;
    private JComboBox<String> comboBoxMajor;
    private DefaultTableModel defaultTableModel;
    private JTable table;

    public FrameMain() {
        initView();
    }

    /**
     * 初始化界面
     */
    public void initView() {
        setTitle("高校学生就业管理系统");
        setSize(610, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuUpdate = new JMenu("修改");
        menuBar.add(menuUpdate);

        JMenu menuQuery = new JMenu("查询");
        menuBar.add(menuQuery);

        JMenu menuAdd = new JMenu("添加");
        menuBar.add(menuAdd);

        JMenu menuBackup = new JMenu("备份与恢复");
        menuBar.add(menuBackup);

        JMenu menuExit = new JMenu("退出");
        menuBar.add(menuExit);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel labelDept = new JLabel("学院:");
        labelDept.setBounds(50, 40, 80, 25);
        panel.add(labelDept);

        comboBoxDept = new JComboBox<>();
        comboBoxDept.setBounds(150, 40, 160, 25);
        loadDeptComboBoxData(); // 从数据库加载数据
        panel.add(comboBoxDept);

        JLabel labelMajor = new JLabel("专业:");
        labelMajor.setBounds(50, 90, 80, 25);
        panel.add(labelMajor);

        comboBoxMajor = new JComboBox<>();
        comboBoxMajor.setBounds(150, 90, 160, 25);
        panel.add(comboBoxMajor);

        JButton btnMajorConfirm = new JButton("确定");
        btnMajorConfirm.setBounds(390, 90, 80, 25);
        panel.add(btnMajorConfirm);

        table = new JTable();
        CustomRowColorRenderer renderer = new CustomRowColorRenderer();
        table.setBounds(50, 150, 500, 200);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setDefaultRenderer(Object.class, renderer);
        renderer.setHighlightRow(0);
        table.repaint();
        panel.add(table);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 确保只在选择完成时触发
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // renderer.setHighlightRow(selectedRow);
                    table.repaint(); // 强制重绘表格以应用新的背景色
                }
            }
        });

        add(panel);
        setVisible(true);

        loadDetails(table, (String) comboBoxMajor.getSelectedItem());

        // 根据选择的院系加载相应的专业
        comboBoxDept.addActionListener(e -> loadMajorComboBoxData((String) comboBoxDept.getSelectedItem()));

        // 显示选择的专业的相关信息
        btnMajorConfirm.addActionListener(e -> loadDetails(table, (String) comboBoxMajor.getSelectedItem()));

        JMenuItem itemUpdate = new JMenuItem("已就业学生信息");
        itemUpdate.addActionListener(e -> {
            // 修改选中记录的信息
            if (!table.getSelectionModel().isSelectionEmpty()) {
                int selectedRow = table.getSelectedRow();
                new FrameUpdateInfo(defaultTableModel, selectedRow, this);
            } else {
                JOptionPane.showMessageDialog(FrameMain.this, "请选择要修改的记录");
            }
        });
        menuUpdate.add(itemUpdate);

        JMenuItem itemInsertJob = new JMenuItem("工作");
        itemInsertJob.addActionListener(e -> new FrameAddJob());
        menuAdd.add(itemInsertJob);

        JMenuItem itemInsertGraduate = new JMenuItem("毕业生");
        itemInsertGraduate.addActionListener(e -> new FrameAddStu());
        menuAdd.add(itemInsertGraduate);

        JMenuItem itemInsertEmpRecord = new JMenuItem("就业记录");
        itemInsertEmpRecord.addActionListener(e -> new FrameAddEmpRecord());
        menuAdd.add(itemInsertEmpRecord);

        JMenuItem itemQueryMajorEmpRate = new JMenuItem("专业就业率");
        itemQueryMajorEmpRate.addActionListener(e -> new FrameMajorEmpRate());
        menuQuery.add(itemQueryMajorEmpRate);

        JMenuItem itemQueryGraduatesInfo = new JMenuItem("毕业生就业统计");
        itemQueryGraduatesInfo.addActionListener(e -> getGraduateStatistics());
        menuQuery.add(itemQueryGraduatesInfo);

        JMenuItem itemBackup = new JMenuItem("备份");
        itemBackup.addActionListener(e -> backupDB());
        menuBackup.add(itemBackup);

        JMenuItem itemRestore = new JMenuItem("恢复");
        itemRestore.addActionListener(e -> restoreDB());
        menuBackup.add(itemRestore);

        JMenuItem itemLogout = new JMenuItem("退出登录");
        itemLogout.addActionListener(e -> {
            new FrameLogin();
            dispose();
        });
        menuExit.add(itemLogout);

        JMenuItem itemExitSys = new JMenuItem("退出系统");
        itemExitSys.addActionListener(e -> dispose());
        menuExit.add(itemExitSys);
    }

    /**
     * 备份数据库
     */
    private void backupDB() {
        try {
            String command = "mysqldump -u " + DBUtil.USER + " --password=" + DBUtil.PASSWORD + " student_employment_system -r ./backupDB.sql";
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                JOptionPane.showMessageDialog(FrameMain.this, "数据库备份成功");
            } else {
                JOptionPane.showMessageDialog(FrameMain.this, "数据库备份失败，退出码: " + exitCode);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(FrameMain.this, "数据库备份异常");
            throw new RuntimeException(ex);
        }
    }

    /**
     * 恢复数据库
     */
    private void restoreDB() {
        try {
            String[] command = {"cmd.exe", "/c", "mysql -u " + DBUtil.USER + " --password=" + DBUtil.PASSWORD + " student_employment_system < ./backupDB.sql"};
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                JOptionPane.showMessageDialog(FrameMain.this, "数据库恢复成功");
            } else {
                JOptionPane.showMessageDialog(FrameMain.this, "数据库恢复失败，退出码: " + exitCode);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(FrameMain.this, "数据库恢复异常");
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据专业查询，将查询信息加载到表格中
     *
     * @param table         目标表格
     * @param selectedMajor 选中专业
     */
    private void loadDetails(JTable table, String selectedMajor) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "select students.student_id, student_name, gender, employment_status, job_title, job_type, company_name\n" +
                    "from students\n" +
                    "         left join employment_records on students.student_id = employment_records.student_id\n" +
                    "         left join jobs on employment_records.job_id = jobs.job_id\n" +
                    "where major_id in (select major_id from majors where major_name = ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, selectedMajor);
            resultSet = statement.executeQuery();

            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metadata.getColumnName(i));
            }

            defaultTableModel = new DefaultTableModel(null, columnNames);
            table.setModel(defaultTableModel);
            TableColumn column = table.getColumnModel().getColumn(0);
            column.setPreferredWidth(90);

            Vector<String> names = new Vector<>();
            names.add("学号");
            names.add("姓名");
            names.add("性别");
            names.add("就业状态");
            names.add("工作");
            names.add("工作类型");
            names.add("公司名称");
            defaultTableModel.addRow(names);

            Vector<Object> row;
            while (resultSet.next()) {
                row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                defaultTableModel.addRow(row);
            }

            defaultTableModel.fireTableDataChanged();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    /**
     * 加载学院名
     */
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

    /**
     * 根据学院名，加载专业名
     *
     * @param deptName 学院名
     */
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

    /**
     * 调用存储过程，获取毕业生统计信息
     */
    private void getGraduateStatistics() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "call get_graduate_statistics()";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            int totalStu = 0, unEmpStu = 0, empStu = 0;
            double empRate = 0;

            while (resultSet.next()) {
                totalStu = resultSet.getInt(1);
                unEmpStu = resultSet.getInt(2);
                empStu = resultSet.getInt(3);
                empRate = resultSet.getDouble(4);
            }

            String formattedEmpRate = String.format("%.2f", empRate);

            String msg = "毕业生共 " + totalStu + " 人\n" +
                    "未就业毕业生 " + unEmpStu + " 人\n" +
                    "已就业毕业生 " + empStu + " 人\n" +
                    "统计就业率 " + formattedEmpRate + "%";

            JOptionPane.showMessageDialog(null, msg);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    /**
     * 回调接口，监听 Update 子窗口关闭，触发刷新列表
     */
    @Override
    public void onUpdateFrameClosed() {
        loadDetails(table, (String) comboBoxMajor.getSelectedItem());
    }
}


