package com.zyz.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatUI extends JFrame {
    private Socket socket;
    private JList<String> userList;
    private JTextArea chatArea;
    private JTextArea messageField;
    public String name;
    private String privatemsg;
    public PrivateChatUI privateChatUI;

    // 构造函数
    public ChatUI(Socket socket, String name) {
        super(name + "的群聊界面");
        this.socket = socket;
        this.name = name;
        ChatFrameInit();
        // 创建子线程，接收当前的聊天和在线用户
        new ChatClientThread(socket, this).start();
    }

    // 显示在线用户
    public void showOnlineUser(String[] names) {
        userList.setListData(names);
    }

    // 初始化界面
    private void ChatFrameInit() {
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 群聊信息区域
        chatArea = new JTextArea();
        chatArea.setEditable(false);  // 不允许编辑聊天记录
        chatArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chatArea.setBackground(new Color(245, 245, 245));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // 在线用户列表区域
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createTitledBorder("在线用户"));
        userList = new JList<>();
        userList.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 250));
        userPanel.add(userScrollPane);

        // 输入框和发送按钮区域
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        messageField = new JTextArea(3, 30);
        messageField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        messageField.setLineWrap(true);  // 自动换行
        messageField.setWrapStyleWord(true);  // 按单词换行
        messageField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JScrollPane messageScrollPane = new JScrollPane(messageField);
        messageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JButton sendButton = new JButton("发送");
        sendButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(100, 40));

        // 发送按钮点击事件
        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                try {
                    OutputStream out = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.writeInt(2);  // 标记群聊消息
                    dos.writeUTF(message);
                    dos.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                messageField.setText("");  // 清空输入框
            }
        });

        bottomPanel.add(messageScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // 将面板添加到主界面
        add(chatScrollPane, BorderLayout.CENTER);  // 群聊区域
        add(userPanel, BorderLayout.EAST);  // 在线用户区域
        add(bottomPanel, BorderLayout.SOUTH);  // 输入区域

        setLocationRelativeTo(null);  // 居中显示
        setVisible(true);

        // 添加右键菜单功能
        addRightClickMenu();
    }

    // 更新聊天区域消息
    public void updateMsg(String msg) {
        chatArea.append(msg + "\n");
    }

    // 添加右键点击菜单
    private void addRightClickMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem privateChatItem = new JMenuItem("私聊");
        JMenuItem pokeItem = new JMenuItem("拍一拍");

        // 私聊菜单项
        privateChatItem.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                openPrivateChat(selectedUser);
            }
        });

        // 拍一拍菜单项
        pokeItem.addActionListener(e -> {

            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                sendPoke(selectedUser);
            }
        });

        popupMenu.add(privateChatItem);
        popupMenu.add(pokeItem);

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = userList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        userList.setSelectedIndex(index);  // 选择用户
                        popupMenu.show(userList, e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = userList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        userList.setSelectedIndex(index);
                        popupMenu.show(userList, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    // 打开私聊界面
    private void openPrivateChat(String user) {
        // 如：new PrivateChatUI(user);
        if(user.equals(name)){
            //提示不能和自己私聊
            JOptionPane.showMessageDialog(this, "不能和自己私聊！", "提示", JOptionPane.INFORMATION_MESSAGE);
        }else{
            privateChatUI = new PrivateChatUI(socket, user);
        }

    }

    // 发送拍一拍消息
    private void sendPoke(String user) {
        // 发送拍一拍消息
        try {
            OutputStream out = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeInt(3);  // 标记为拍一拍消息
            StringBuilder sb = new StringBuilder();
            String msg = sb.append(name).append("拍了拍").append(user).append("!").toString();
            dos.writeUTF(msg);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePrviateMsg(String msg) {
        privatemsg = msg;
        PrivateChatUI.chatArea.append(msg+"\n");
    }

    public void showRefuse(String refuseMsg) {
        //弹出一个界面，提示拒绝
        privateChatUI.optionPane.showMessageDialog(this, refuseMsg, "提示", JOptionPane.INFORMATION_MESSAGE);
        privateChatUI.dispose();
    }
}
