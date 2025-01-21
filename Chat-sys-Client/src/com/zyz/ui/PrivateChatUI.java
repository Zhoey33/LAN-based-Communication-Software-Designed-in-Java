package com.zyz.ui;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PrivateChatUI extends JFrame {
    private Socket socket;
    public String targetUser;  // 私聊的目标用户
    public static JTextArea chatArea;  // 聊天记录区域
    private JTextArea messageField;// 输入消息的文本框
    public JOptionPane optionPane;

    // 构造函数，接受Socket和目标用户的名称
    public PrivateChatUI(Socket socket, String targetUser) {
        super("和 "+targetUser + " 的私聊");
        this.socket = socket;
        this.targetUser = targetUser;
        initUI();
    }

    // 初始化界面
    private void initUI() {
        // 设置窗口大小和关闭操作
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭当前窗口时不会退出应用

        // 设置布局管理器
        setLayout(new BorderLayout());

        // 创建聊天区域：不可编辑，显示私聊消息
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chatArea.setBackground(new Color(245, 245, 245));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // 创建输入框，用于输入消息
        messageField = new JTextArea(3, 30);
        messageField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        messageField.setLineWrap(true);  // 自动换行
        messageField.setWrapStyleWord(true);  // 换行时按单词切割
        messageField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JScrollPane messageScrollPane = new JScrollPane(messageField);
        messageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // 创建发送按钮
        JButton sendButton = new JButton("发送");
        sendButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(100, 40));  // 设置按钮大小

        // 发送按钮点击事件
        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                try {
                    // 发送私聊消息到服务器
                    OutputStream out = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.writeInt(4);  // 表示私聊消息类型
                    dos.writeUTF(targetUser);  // 目标用户
                    dos.writeUTF(message);  // 私聊消息
                    dos.flush();
                    messageField.setText("");  // 清空输入框
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 创建底部面板，包含输入框和发送按钮
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(messageScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // 将聊天区域和底部面板添加到窗口
        add(chatScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 设置窗口居中显示
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateMsg(String msg) {
        chatArea.append(msg + "\n");
    }
}
