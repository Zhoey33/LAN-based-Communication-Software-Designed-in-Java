package com.zyz.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class ChatClientThread extends Thread{
    private Socket socket;
    private ChatUI chatUI;
    private DataInputStream dis;

    public ChatClientThread(Socket socket, ChatUI UI) {
        this.socket = socket;
        this.chatUI = UI;
    }

    public void run() {
        try {
            //用特殊数据流读数据
            InputStream is = socket.getInputStream();
            dis = new DataInputStream(is);
            while (true) {
                //先读一个整数，用于判断是登录还是发送消息
                int type = dis.readInt();
                //判断是登录还是发送消息
                switch (type) {
                    case 1:
                        receiveOnlineUser();
                        break;
                    case 2:
                        recevieMeg();
                        break;
                    case 3:
                        receviePoke();
                    break;
                    case 4:
                        receivePrivateMsg();
                        break;
                    case 5:
                        receiveRefuseMsg();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveRefuseMsg() {
        try {
            String refuseMsg = dis.readUTF();
            chatUI.showRefuse(refuseMsg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void receivePrivateMsg() throws Exception {
        String name = dis.readUTF();
        String msg = dis.readUTF();
        if(name.equals(chatUI.name)||(chatUI.privateChatUI !=null && name.equals(chatUI.privateChatUI.targetUser))){
            chatUI.updatePrviateMsg(msg);
        }else{
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            if(chatUI.privateChatUI != null){
                dos.writeInt(5);
                dos.writeInt(1);
                dos.writeUTF(name);
                dos.flush();
            }else{
                //对方未打开和你的私聊窗口
                dos.writeInt(5);
                dos.writeInt(2);
                dos.writeUTF(name);
            }

        }
//        String name = dis.readUTF();
//        String msg = dis.readUTF();
//        chatUI.updatePrviateMsg(msg);
    }

    private void receviePoke() throws Exception {
        String poke = dis.readUTF();
        chatUI.updateMsg(poke);
    }

    private void recevieMeg() throws Exception {
        String msg = dis.readUTF();
        chatUI.updateMsg(msg);
    }

    private void receiveOnlineUser() throws Exception {
        int size = dis.readInt();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = dis.readUTF();
        }
        chatUI.showOnlineUser(names);
    }
}
