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
        // "/nick 닉네임" 으로 온다.
        chatUser.setName(name.substring(name.indexOf(" ") + 1));
        chatHouse.addLobby(chatUser);
        try{
        //메뉴 반복 실행
            chatUser.write(" /help      : 도움말 , 메뉴를 확인하세요 ");
            while(true) {
                if (chatUser.isRoom()) {    //방에 있을 때

                    String msg = chatUser.read();
                    if (msg.indexOf("/exit") == 0) {
                        chatUser.setIsAdmin(false);
                        chatHouse.exitRoom(chatUser);
                    }else if(msg.indexOf("/help") == 0){
                        if(chatUser.isAdmin()){
                            chatUser.write(" /exit   : 방나가기");
                            chatUser.write(" /kick   : 유저 강퇴");
                            chatUser.write(" /help   : 도움말");
                        }else{
                            chatUser.write(" /exit   : 방나가기");
                            chatUser.write(" /help   : 도움말");
                        }

                    }else if(msg.indexOf("/kick") == 0){
                        if(chatUser.isAdmin()){
                            int index = 0;
                            List<ChatUser> users = chatHouse.getChatUsers(chatUser);
                            for(ChatUser cu : chatHouse.getChatUsers(chatUser)){
                                if(index != 0){
                                    chatUser.write(index + " : "+ cu.getName());
                                }
                                index ++;
                            }
                            if(users.size() == 1){
                                chatUser.write("강퇴할 유저가 없습니다.");
                                continue;
                            }
                            chatUser.write("강퇴할 유저 번호를 입력하세요.");
                            index = Integer.parseInt(chatUser.read());
                            users.get(index).write("강퇴당했습니다 ㅠㅠ");
                            System.out.println(users.get(index).isRoom());
                            chatHouse.exitRoom(users.get(index));

                        }
                    }
                    else {
                        List<ChatUser> userList = chatHouse.getChatUsers(chatUser);
                        for (ChatUser cu : userList) {
                            System.out.println(chatUser.getName() + " : "+ msg);
                            cu.write(chatUser.getName() + " : " + msg);
                        }
                    }


                } else { // 로비에 있을 때
                    String msg = chatUser.read();

                    if (msg.indexOf("/create") == 0) {
                        String title = msg.substring(msg.indexOf(" ") + 1);
                        chatHouse.createRoom(chatUser, title);
                        System.out.println(chatUser.getName() + "님이  " + title + "방에 입장했습니다.");
                        chatUser.write(chatUser.getName() + "님이 방에 입장했습니다.");
                        chatUser.setIsAdmin(true);
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

                        if (Character.toUpperCase(chatUser.read().charAt(0)) == 'Y') {
                            chatUser.write("400///quit 종료되었습니다.");
                            chatHouse.exitLobby(chatUser);
                            chatUser.close();
                        }

                    } else if(msg.indexOf("/help") == 0){
                        chatUser.write("============ 메뉴 ===========");
                        chatUser.write("/create [방이름]   : 방을 생성합니다.");
                        chatUser.write("/list            : 방 리스트를 입력하세요. ");
                        chatUser.write("/join [방번호]     : 방번호를 입력하세요.");
                        chatUser.write("/quit            : 종료합니다.");
                        chatUser.write("=============================");
                    }

                    else{
                        List<ChatUser> lobbyList = chatHouse.getLobby();
                        for(ChatUser cu : lobbyList){
                            cu.write(chatUser.getName()+ " : " +msg);
                        }
                    }


                }
            }
        }catch (Exception ex){
            System.out.println(chatUser.getName() + "연결 종료");
        }

    }
}