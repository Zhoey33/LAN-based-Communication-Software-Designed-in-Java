package com.zyz;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    public final static Map<Socket, String> socketStringMap = new HashMap<>();
    public static void main(String[] args) {
        try {
            System.out.println("==============服务器启动===============");
            ServerSocket serverSocket = new ServerSocket(Constant.SERVER_PORT);

            while (true) {
                System.out.println("等待客户端连接...");
                Socket socket = serverSocket.accept();
                System.out.println("客户端连接成功");
                new ChatServerThread(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
