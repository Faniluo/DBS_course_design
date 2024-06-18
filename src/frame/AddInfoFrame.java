package frame;

import javafx.beans.value.ChangeListener;

import javax.swing.*;

/**
 * @author Hanit
 * @version v1.0.0
 * @date 2024/6/18 19:54
 * @description
 **/
public class AddInfoFrame extends JFrame {

    public AddInfoFrame() {
        initView();
    }

    private void initView() {
        setTitle("修改毕业生信息");
        setSize(350, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel label1 = new JLabel("学    号:");
        label1.setBounds(50, 60, 70, 25);
        panel.add(label1);

        JTextField textField1 = new JTextField(20);
        textField1.setBounds(120, 60, 160, 25);
        panel.add(textField1);

        JLabel label2 = new JLabel("姓    名:");
        label2.setBounds(50, 90, 70, 25);
        panel.add(label2);

        JTextField textField2 = new JTextField(20);
        textField2.setBounds(120, 90, 160, 25);
        panel.add(textField2);

        JLabel label3 = new JLabel("性    别:");
        label3.setBounds(50, 120, 70, 25);
        panel.add(label3);

        JComboBox<String> comboBox3 = new JComboBox<>();
        comboBox3.setBounds(120, 120, 160, 25);
        comboBox3.addItem(null);
        comboBox3.addItem("男");
        comboBox3.addItem("女");
        panel.add(comboBox3);

        JLabel label4 = new JLabel("就业状态:");
        label4.setBounds(50, 150, 70, 25);
        panel.add(label4);

        JComboBox<String> comboBox4 = new JComboBox<>();
        comboBox4.setBounds(120, 150, 160, 25);
        comboBox4.addItem(null);
        comboBox4.addItem("待业");
        comboBox4.addItem("就业");
        panel.add(comboBox4);

        JLabel label5 = new JLabel("工    作:");
        label5.setBounds(50, 180, 70, 25);
        panel.add(label5);

        JTextField textField5 = new JTextField(20);
        textField5.setBounds(120, 180, 160, 25);
        panel.add(textField5);

        JLabel label6 = new JLabel("工作类型:");
        label6.setBounds(50, 210, 70, 25);
        panel.add(label6);

        JTextField textField6 = new JTextField(20);
        textField6.setBounds(120, 210, 160, 25);
        panel.add(textField6);

        JLabel label7 = new JLabel("公司名:");
        label7.setBounds(50, 240, 70, 25);
        panel.add(label7);

        JTextField textField7 = new JTextField(20);
        textField7.setBounds(120, 240, 160, 25);
        panel.add(textField7);

        JButton modifyButton = new JButton("添加");
        modifyButton.setBounds(130, 280, 80, 25);
        panel.add(modifyButton);

        add(panel);
        setVisible(true);


    }

    public void insertInfo() {

    }

    public static void main(String[] args) {
        new AddInfoFrame();
    }

}
