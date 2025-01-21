package com.zyz.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class LoginUI extends JFrame {

    // 构造函数
    public LoginUI() {
        // 设置窗口标题
        super("登录");

        // 设置窗口的大小
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建面板，使用BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // 垂直布局

        // 设置窗口背景颜色
        panel.setBackground(new Color(240, 240, 240));

        // 字体
        Font labelFont = new Font("微软雅黑", Font.PLAIN, 14);
        Font buttonFont = new Font("微软雅黑", Font.BOLD, 14);

        // 创建用户名面板，使用FlowLayout（使标签和输入框在同一行上显示）
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));  // 左对齐，设置水平间距

        // 创建标签
        JLabel label = new JLabel("请输入用户名:");
        label.setFont(labelFont);

        // 创建输入框
        JTextField usernameField = new JTextField(15);  // 保持默认大小
        usernameField.setFont(labelFont);
        usernameField.setPreferredSize(new Dimension(150, 30));  // 设置输入框宽度为150
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        usernamePanel.add(label);  // 添加标签
        usernamePanel.add(usernameField);  // 添加输入框

        // 创建按钮面板，使用FlowLayout（使按钮在一行上显示）
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));  // 水平排列，按钮间距20px

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(buttonFont);
        loginButton.setBackground(new Color(70, 130, 180));  // 按钮颜色
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);  // 去除按钮焦点
        loginButton.setPreferredSize(new Dimension(100, 35));

        // 取消按钮
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(buttonFont);
        cancelButton.setBackground(new Color(200, 70, 70));  // 按钮颜色
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);  // 去除按钮焦点
        cancelButton.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // 将控件添加到面板
        panel.add(Box.createRigidArea(new Dimension(0, 20)));  // 添加空白区域，调整控件间距
        panel.add(usernamePanel);  // 添加用户名面板
        panel.add(Box.createRigidArea(new Dimension(0, 20)));  // 输入框和按钮之间的间距
        panel.add(buttonPanel);  // 添加按钮面板

        // 登录按钮事件处理
        loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginUI.this, "用户名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                } else {
                    // 登录成功后的逻辑
                    JOptionPane.showMessageDialog(LoginUI.this, "登录成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    // 在此处可以添加进入下一步的代码
                    try {
                        Socket socket = new Socket(Constant.SERVER_IP, Constant.SERVER_PORT);
                        //发送信息
                        OutputStream os = socket.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeInt(1);
                        dos.writeUTF(username);
                        dos.flush();

                        ChatUI chatUI = new ChatUI(socket,username);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    this.dispose();  // 关闭登录界面
                }
            }
        );

        // 取消按钮事件处理
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // 退出程序
            }
        });

        // 将面板添加到窗口
        add(panel);
        setLocationRelativeTo(null);  // 窗口居中
        setVisible(true);
    }

    // 主方法
    public static void main(String[] args) {
        new LoginUI();  // 创建并显示登录界面
    }
}
