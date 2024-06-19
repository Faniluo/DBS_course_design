package util;

import javax.swing.*;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/19 12:39
 * @description
 **/
public class ComboBoxUtil {
    /**
     * 根据内容查找JComboBox中元素的下标
     *
     * @param comboBox JComboBox对象
     * @param content  要查找的内容
     * @return 元素的下标，如果未找到返回-1
     */
    public static int findIndexOfContent(JComboBox<String> comboBox, String content) {
        // 第一个 item 为空，从下标 1 开始遍历
        for (int i = 1; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(content)) {
                return i;
            }
        }
        return -1; // 如果没有找到内容，则返回-1
    }

}
