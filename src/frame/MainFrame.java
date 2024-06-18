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

public class MainFrame extends JFrame {
    private JComboBox<String> deptComboBox;
    private JComboBox<String> majorComboBox;
    private DefaultTableModel defaultTableModel;

    public MainFrame() {
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

        JMenu modifyMenu = new JMenu("修改");
        menuBar.add(modifyMenu);

        JMenu queryMenu = new JMenu("查询");
        menuBar.add(queryMenu);

        JMenu addMenu = new JMenu("添加");
        menuBar.add(addMenu);

        JMenu exitMenu = new JMenu("退出");
        menuBar.add(exitMenu);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel deptLabel = new JLabel("学院:");
        deptLabel.setBounds(50, 40, 80, 25);
        panel.add(deptLabel);

        deptComboBox = new JComboBox<>();
        deptComboBox.setBounds(150, 40, 160, 25);
        // 从数据库加载数据
        loadDeptComboBoxData();
        panel.add(deptComboBox);

        JLabel majorLabel = new JLabel("专业:");
        majorLabel.setBounds(50, 90, 80, 25);
        panel.add(majorLabel);

        majorComboBox = new JComboBox<>();
        majorComboBox.setBounds(150, 90, 160, 25);
        // 从数据库加载数据
        loadMajorComboBoxData((String) deptComboBox.getSelectedItem());
        panel.add(majorComboBox);

        JButton majorConfirmButton = new JButton("确定");
        majorConfirmButton.setBounds(390, 90, 80, 25);
        panel.add(majorConfirmButton);

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

        loadDetails(table, (String) majorComboBox.getSelectedItem());

        deptComboBox.addActionListener(e -> {
            // 根据选择的院系加载相应的专业
            loadMajorComboBoxData((String) deptComboBox.getSelectedItem());
        });

        majorConfirmButton.addActionListener(e -> {
            // 显示选择的专业的相关信息  (String) deptComboBox.getSelectedItem(),
            loadDetails(table, (String) majorComboBox.getSelectedItem());
        });

        // modifyMenu.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         // 修改选中记录的信息
        //         if (!table.getSelectionModel().isSelectionEmpty()) {
        //             int selectedRow = table.getSelectedRow();
        //             // String selectedDept = (String) table.getValueAt(selectedRow, 0);
        //             // String selectedMajor = (String) table.getValueAt(selectedRow, 1);
        //             modifyRecord(selectedRow);
        //         }
        //     }
        // });

        JMenuItem modifyItem = new JMenuItem("修改信息");
        modifyItem.addActionListener(e -> {
            // 修改选中记录的信息
            if (!table.getSelectionModel().isSelectionEmpty()) {
                int selectedRow = table.getSelectedRow();
                new ModifyInfoFrame(defaultTableModel, selectedRow);
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "请选择要修改的记录");
            }
        });
        modifyMenu.add(modifyItem);

        JMenuItem insertItem = new JMenuItem("添加信息");
        insertItem.addActionListener(e -> {
            new AddInfoFrame();
        });
        addMenu.add(insertItem);
    }

    // private void insertRecord() {
    //
    // }
    //
    // private void modifyRecord(int selectedRow) {
    //
    // }

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

            deptComboBox.addItem(null);

            while (resultSet.next()) {
                String deptName = resultSet.getString("dept_name");
                deptComboBox.addItem(deptName);
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

            majorComboBox.removeAllItems();
            majorComboBox.addItem(null);
            while (resultSet.next()) {
                majorComboBox.addItem(resultSet.getString("major_name"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(resultSet, statement, connection);
        }

    }

    public static void main(String[] args) {
        new MainFrame();
    }

}
