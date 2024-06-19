package frame;

import util.CustomRowColorRenderer;
import util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/17 15:00
 * @description
 **/

public class FrameMain extends JFrame {
    private JComboBox<String> comboBoxDept;
    private JComboBox<String> comboBoxMajor;
    private DefaultTableModel defaultTableModel;

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

        JTable table = new JTable();
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

        JMenuItem itemUpdate = new JMenuItem("修改信息");
        itemUpdate.addActionListener(e -> {
            // 修改选中记录的信息
            if (!table.getSelectionModel().isSelectionEmpty()) {
                int selectedRow = table.getSelectedRow();
                new FrameUpdateInfo(defaultTableModel, selectedRow);
            } else {
                JOptionPane.showMessageDialog(FrameMain.this, "请选择要修改的记录");
            }
        });
        menuUpdate.add(itemUpdate);

        JMenuItem itemInsertJob = new JMenuItem("添加工作");
        itemInsertJob.addActionListener(e -> new FrameAddJob());
        menuAdd.add(itemInsertJob);

        JMenuItem itemInsertGraduate = new JMenuItem("添加毕业生");
        itemInsertGraduate.addActionListener(e -> new FrameAddStu());
        menuAdd.add(itemInsertGraduate);

        JMenuItem itemInsertEmpRecord = new JMenuItem("添加就业记录");
        itemInsertEmpRecord.addActionListener(e -> new FrameAddEmpRecord());
        menuAdd.add(itemInsertEmpRecord);

        JMenuItem itemQueryMajorEmpRate = new JMenuItem("查看专业就业率");
        itemQueryMajorEmpRate.addActionListener(e -> {
            // todo: 存储过程查询各专业的毕业生就业率
        });
        menuQuery.add(itemQueryMajorEmpRate);

        JMenuItem itemQueryGraduatesInfo = new JMenuItem("查看毕业生就业统计");
        itemQueryGraduatesInfo.addActionListener(e -> {
            // todo: 存储过程查询毕业生的人数、待业人数、就业人数和就业率
        });
        menuQuery.add(itemQueryGraduatesInfo);

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

    private void loadDetails(JTable table, String selectedMajor) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
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
            DatabaseConnection.close(resultSet, statement, connection);
        }
    }

    private void loadDeptComboBoxData() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
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
            DatabaseConnection.close(resultSet, statement, connection);
        }
    }

    private void loadMajorComboBoxData(String deptName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
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
            DatabaseConnection.close(resultSet, statement, connection);
        }
    }

    public static void main(String[] args) {
        new FrameMain();
    }

}
