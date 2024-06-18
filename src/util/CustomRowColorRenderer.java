package util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/17 23:39
 * @description
 **/

public class CustomRowColorRenderer extends DefaultTableCellRenderer {
    private int highlightRowIndex = -1; // 用于存储要高亮显示的行索引

    public void setHighlightRow(int rowIndex) {
        this.highlightRowIndex = rowIndex;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (row == highlightRowIndex) {
            cellComponent.setBackground(new Color(73, 104, 197, 190)); // 设置高亮行的背景色
        } else {
            cellComponent.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground()); // 非高亮行使用默认背景色
        }

        return cellComponent;
    }
}

