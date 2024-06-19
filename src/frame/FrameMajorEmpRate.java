package frame;

import util.CustomRowColorRenderer;
import util.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/19 15:08
 * @description
 **/
public class FrameMajorEmpRate extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;

    public FrameMajorEmpRate() {
        initView();
    }

    public FrameMajorEmpRate(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
        initView();
    }

    private void initView() {
        setTitle("高校学生就业管理系统");
        setSize(555, 270);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        table = new JTable();
        CustomRowColorRenderer renderer = new CustomRowColorRenderer();
        table.setBounds(20, 10, 500, 200);
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

        getMajorEmpRate();
    }

    private void getMajorEmpRate() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "call get_major_employment_rate()";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            tableModel = new DefaultTableModel(null, columnNames);
            table.setModel(tableModel);

            Vector<String> names = new Vector<>();
            names.add("专业");
            names.add("毕业生人数");
            names.add("已就业毕业生");
            names.add("就业率 ( %)");
            tableModel.addRow(names);

            Vector<Object> row;
            while (resultSet.next()) {
                row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                tableModel.addRow(row);
            }

            tableModel.fireTableDataChanged();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(resultSet, statement, connection);
        }
    }

    public static void main(String[] args) {
        new FrameMajorEmpRate();
    }
}
