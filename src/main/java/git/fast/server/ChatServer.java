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

class ChatServerHandler extends Thread {
    private ChatHouse chatHouse;
    private ChatUser chatUser;

    public ChatServerHandler(ChatHouse chatHouse, ChatUser chatUser){
        this.chatHouse = chatHouse;
        this.chatUser = chatUser;
    }

    @Override
    public void run() {

        String name = chatUser.read();
        chatUser.setName(name);
        chatHouse.addLobby(chatUser);
        try{
        //메뉴 반복 실행
            while(true) {
                if (chatUser.isRoom) {    //방에 있을 때
                    String msg = chatUser.read();
                    if (msg.indexOf("/exit") == 0) {
                        chatHouse.exitRoom(chatUser);
                    } else {
                        List<ChatUser> userList = chatHouse.getChatUsers(chatUser);
                        for (ChatUser cu : userList) {
                            System.out.println(chatUser.getName() + " : "+ msg);
                            cu.write(chatUser.getName() + " : " + msg);
                        }
                    }


                } else {                  // 로비에 있을 때
                    chatUser.write(chatUser.getName() + "님 // Lobby");
                    chatUser.write("============ 메뉴 ===========");
                    chatUser.write("/create [방이름]");
                    chatUser.write("/list");
                    chatUser.write("/join [방번호]");
                    chatUser.write("/quit");
                    chatUser.write("=============================");

                    String msg = chatUser.read();

                    if (msg.indexOf("/create") == 0) {
                        String title = msg.substring(msg.indexOf(" ") + 1);
                        chatHouse.createRoom(chatUser, title);
                        System.out.println(chatUser.getName() + "님이  " + title + "방에 입장했습니다.");
                        chatUser.write(chatUser.getName() + "님이 방에 입장했습니다.");
                    } else if (msg.indexOf("/list") == 0) {
                        int index = 0;
                        for (ChatRoom cr : chatHouse.getRooms()) {
                            chatUser.write(index + " : " + cr.getTitle());
                            index++;
                        }
                    } else if (msg.indexOf("/join") == 0) {
                        chatHouse.joinRoom(chatUser, Integer.parseInt(msg.substring(msg.indexOf(" ") + 1)));
                        System.out.println(chatUser.getName() + "님이 방에 입장했습니다.");
                        chatUser.write(chatUser.getName() + "님이 방에 입장했습니다.");
                    } else if (msg.indexOf("/quit") == 0) {
                        chatUser.write("종료하시겠습니까? Y / N");
                        // 추가아아ㅏ아
                        if (Character.toUpperCase(chatUser.read().charAt(0)) == 'Y') {
                            chatHouse.exitLobby(chatUser);
                            chatUser.close();
                        }

                    }


                }
            }
        }catch (Exception ex){
            System.out.println(chatUser.getName() + " 소켓 종료");
        }

    }
}