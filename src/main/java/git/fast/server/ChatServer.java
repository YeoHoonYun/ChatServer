package git.fast.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ChatServer {
    private int port;
    private ChatHouse chatHouse;

    public ChatServer(int port){
        this.port = port;
        chatHouse = new ChatHouse();
    }

    public void run(){

        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port);

            while(true){
                //accept ...대기
                Socket socket = serverSocket.accept();
                // 입력시 chatUser 생성 , ChatServerHandler 생성(스레드) start()
                ChatUser chatUser = new ChatUser(socket);
                ChatServerHandler chatServerHandler = new ChatServerHandler(chatHouse, chatUser);
                chatServerHandler.start();

            }

        }catch (Exception ex){
            System.out.println("port 번호 문제");
        }finally {
            try{    serverSocket.close();   }catch (Exception ignore){  }
        }

    }
}

