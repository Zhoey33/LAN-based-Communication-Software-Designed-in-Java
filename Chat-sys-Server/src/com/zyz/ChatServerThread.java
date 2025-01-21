package com.zyz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatServerThread extends Thread{
    private Socket socket;

    public ChatServerThread(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        try {
            //用特殊数据流读数据
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            while (true) {
                //先读一个整数，用于判断是登录还是发送消息
                int type = dis.readInt();
                //判断是登录还是发送消息
                switch (type) {
                    case 1:
                        String name = dis.readUTF();
                        //将登录信息存到map中
                        ChatServer.socketStringMap.put(socket, name);
                        //将最新的用户在线消息广播给其他用户
                        updateUserList();
                        break;
                    case 2:
                        String msg = dis.readUTF();
                        updataMsg(msg);
                        break;
                    case 3:
                        String poke = dis.readUTF();
                        updataPoke(poke);
                        break;
                    case 4:
                        String toUser = dis.readUTF();
                        String toUserMsg = dis.readUTF();
                        updataPrivateMsg(toUser, toUserMsg);
                        break;
                    case 5:
                        int type5 = dis.readInt();
                        String refuseUsername = dis.readUTF();
                        sendRefuseMsg(type5,refuseUsername);
                        break;
                }
            }
        } catch (Exception e) {
            //表示用户下线了
            //删除当前的Socket
            ChatServer.socketStringMap.remove(socket);
            try {
                updateUserList();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    private void sendRefuseMsg(int type5, String refuseUsername) {
        for (Socket socket : ChatServer.socketStringMap.keySet()) {
            if (ChatServer.socketStringMap.get(socket).equals(refuseUsername)) {
                try {
                    OutputStream os = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    dos.writeInt(5);
                    if(type5 == 1){
                        dos.writeUTF("对方正在和其他人私聊，请稍等");
                    }else{
                        dos.writeUTF("对方暂未打开与你的私聊窗口，请联系打开！");
                    }
                    dos.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updataPrivateMsg(String toUser, String toUserMsg) {
        String name = ChatServer.socketStringMap.get(socket);

        //封装字符串
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE");
        String time = dtf.format(now);

        //拼接字符串
        StringBuilder sb = new StringBuilder();
        String conmsg =  sb.append(name).append(" ").append(time).append("\r\n").append("   ").append(toUserMsg).append("\r\n").toString();

        //只发送给自己以及toUser的Socket
        //发送给toUser的Socket
        //根据值找键
        for (Socket socket : ChatServer.socketStringMap.keySet()) {
            if (ChatServer.socketStringMap.get(socket).equals(toUser)||ChatServer.socketStringMap.get(socket).equals(name)) {
                //用特殊数据流写数据
                try {
                    OutputStream os = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    //先写一个整数，代表要接收登录人数的数据
                    dos.writeInt(4);
                    dos.writeUTF(name);
                    dos.writeUTF(conmsg);
                    dos.flush();
               } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void updataPoke(String poke) {
        String name = ChatServer.socketStringMap.get(socket);

        //封装字符串
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE");
        String time = dtf.format(now);

        //拼接字符串
        StringBuilder sb = new StringBuilder();
        String conmsg =  sb.append("         ").append(time).append("\r\n").append("**********").append(poke).append("**********").append("\r\n").toString();

        for (Socket socket : ChatServer.socketStringMap.keySet()) {
            //用特殊数据流写数据
            try {
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                //先写一个整数，代表要接收登录人数的数据
                dos.writeInt(3);
                dos.writeUTF(conmsg);
                dos.flush();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updataMsg(String msg) {
        String name = ChatServer.socketStringMap.get(socket);

        //封装字符串
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE");
        String time = dtf.format(now);

        //拼接字符串
        StringBuilder sb = new StringBuilder();
        String conmsg =  sb.append(name).append(" ").append(time).append("\r\n").append("   ").append(msg).append("\r\n").toString();

        for (Socket socket : ChatServer.socketStringMap.keySet()) {
            //用特殊数据流写数据
            try {
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                //先写一个整数，代表要接收登录人数的数据
                dos.writeInt(2);
                dos.writeUTF(conmsg);
                dos.flush();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUserList() {
        /**
         * 需要给客户端三个参数
         * 一个整数1，代表要接收登录人数的数据
         * 一个整数，代表登录人数
         * 一个字符串，代表登录人的名字
         */
        for (Socket socket : ChatServer.socketStringMap.keySet()) {
            //用特殊数据流写数据
            try {
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                //先写一个整数，代表要接收登录人数的数据
                dos.writeInt(1);
                //再写一个整数，代表登录人数
                dos.writeInt(ChatServer.socketStringMap.size());
                //再写一个字符串，代表登录人的名字
                for (String name : ChatServer.socketStringMap.values()) {
                    dos.writeUTF(name);
                }
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
