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
        chatUser.write("별명을 입력하세요. ");
        String name = chatUser.read();
        // "/nick 닉네임" 으로 온다.
        chatUser.setName(name.substring(name.indexOf(" ") + 1));
        chatHouse.addLobby(chatUser);
        chatUser.write("100///닉네임 : "+chatUser.getName());
        try{
            //메뉴 반복 실행
            while (true){
                if(chatUser.isRoom()){
                    chatUser.write("300///현재 "+chatHouse.getChatRoom(chatUser).getTitle()+"방에 접속해 있습니다. (현재 인원 수 : "+ chatHouse.getChatRoom(chatUser).getChatUsers().size() + "명)");
                }else{
                    chatUser.write("300///현재 로비에 있습니다." +"(현재 인원 수 : "+ chatHouse.getLobby().size() + "명)");
                }
                chatUser.write("/help      : 도움말 , 메뉴를 확인하세요 ");
                if (chatUser.isRoom()) {    //방에 있을 때
                    String msg = chatUser.read();
                    if(!chatUser.isRoom()){
                        chatUser.write("당신은 로비에 있습니다.");
                    }
                    else if (msg.indexOf("/exit") == 0) {
                        chatUser.setIsAdmin(false);
                        chatHouse.exitRoom(chatUser);
                    } else if (msg.indexOf("/help") == 0) {
                        if (chatUser.isAdmin()) {
                            chatUser.write("======== 메뉴(방장) =========");
                            chatUser.write(" /exit   : 방나가기");
                            chatUser.write(" /kick   : 유저 강퇴");
                            chatUser.write(" /help   : 도움말");
                            chatUser.write(" /talk : 귓속말");
                            chatUser.write(" /visi 로비유저번호 : 초대하기");
                            chatUser.write("============================");
                        } else {
                            chatUser.write("============ 메뉴 ===========");
                            chatUser.write(" /exit   : 방나가기");
                            chatUser.write(" /help   : 도움말");
                            chatUser.write(" /talk : 귓속말");
                            chatUser.write("============================");
                        }
                    } else if (chatUser.isAdmin() && msg.indexOf("/kick") == 0) {
                        if (chatUser.isAdmin()) {
                            int index = 0;
                            List<ChatUser> users = chatHouse.getChatUsers(chatUser);
                            for (ChatUser cu : chatHouse.getChatUsers(chatUser)) {
                                if (index != 0) {
                                    chatUser.write(index + " | 이름 : " + cu.getName());
                                }
                                index++;
                            }
                            if (users.size() == 1) {
                                chatUser.write("강퇴할 유저가 없습니다.");
                                continue;
                            }
                            try {
                                chatUser.write("강퇴할 유저 번호를 입력하세요.");
                                index = Integer.parseInt(chatUser.read());
                                users.get(index).write("강퇴당했습니다 ㅠㅠ");
                                chatHouse.exitRoom(users.get(index));
                            }catch (Exception e){
                                chatUser.write("해당 유저 번호가 없습니다.");
                            }
                        }
                    }else if (chatUser.isAdmin() && msg.indexOf("/visi") == 0) {
                        try {
                            chatUser.write("============로비에 있는 유저정보===============");
                            int num = 0;
                            if (chatHouse.getLobby().size() != 0) {
                                for (ChatUser cr : chatHouse.getLobby()) {
                                    if (cr == chatUser) {
//                                        chatUser.write(num + " : " + cr.getName() + "(나)");
//                                        num++;
                                        continue;
                                    } else {
                                        chatUser.write(num + " : " + cr.getName());

                                    }
                                    num++;
                                }
                            }
                            try {
                                chatUser.write("초대할 유저 번호를 입력하세요.");
                                int index = Integer.parseInt(chatUser.read());
                                ChatUser visiUser = chatHouse.getLobby().get(index);
                                chatHouse.getLobby().remove(visiUser);
                                chatHouse.getChatRoom(chatUser).addChatUser(visiUser);
                                visiUser.setisRoom(true);
                                chatUser.write(visiUser.getName() + "님을 초대하였습니다..");
                                visiUser.write(chatUser.getName() + "님이 초대하였습니다..");

                                List<ChatUser> userList = chatHouse.getChatUsers(chatUser);
                                for (ChatUser cu : userList) {
                                    cu.write("300///"+visiUser.getName() + "님이 방에 입장했습니다.");
                                }
                            }catch (Exception e){
                                System.out.println("해당 유저 번호가 없습니다.");
                            }
                        }catch (Exception e){

                            chatUser.write("300///유저번호를 입력하세요.");
                        }
                    }else if (msg.indexOf("/talk") == 0) {
                        int index = 0;
                        if (chatHouse.getChatUsers(chatUser).size() != 0) {
                            for (ChatUser cr : chatHouse.getChatUsers(chatUser)) {
                                if(cr == chatUser) {
//                                    chatUser.write(index + " : " + cr.getName()+"(나)");
                                    index++;
                                }else{
                                    chatUser.write(index + " : " + cr.getName());
                                    index++;
                                }
                            }
                        }
                        try {
                            chatUser.write("300///입력하고자 하는 유저번호를 넣으세요.");
                            ChatUser inUser = chatHouse.getChatRoom(chatUser).getChatUsers().get(Integer.parseInt(chatUser.read()));
                            chatUser.write("전달할 메시지를 적어주세요.");
                            String string = chatUser.read();
                            chatHouse.chatTwo(inUser, chatUser.getName() + " : " + string);
                        }catch (Exception e){
                            chatUser.write("유저 번호가 없습니다.");
                        }
                    } else {
                        List<ChatUser> userList = chatHouse.getChatUsers(chatUser);
                        for (ChatUser cu : userList) {
                            cu.write("200///"+chatUser.getName() + " : " + msg);
                        }
                    }
                } else { // 로비에 있을 때
                    String msg = chatUser.read();
                    if(chatUser.isRoom()){
                        chatUser.write("당신은 방에 있습니다.");
                    }
                    if (msg.indexOf("/create") == 0) {
                        try {
                            String title = msg.substring(msg.indexOf(" ") + 1);
                            chatHouse.createRoom(chatUser, title);
                            chatUser.write(chatUser.getName() + "님이 " + title + " 방에 입장했습니다.");
                            chatUser.setIsAdmin(true);
                        }catch (Exception e){
                            chatUser.write("300///방이름을 입력하세요.");
                        }
                    } else if (msg.indexOf("/list") == 0) {
                        int index = 0;
                        if (chatHouse.getRooms().size() != 0) {
                            for (ChatRoom cr : chatHouse.getRooms()) {
                                chatUser.write(index + " : " + cr.getTitle() + " | 방장 : " + chatHouse.roomCreater(cr).getName() + " | 인원수 : " + cr.getChatUsers().size() + "명");
                                index++;
                            }
                        } else {
                            chatUser.write("생성된 방이 없습니다.");
                        }
                    } else if (msg.indexOf("/join") == 0) {
                        try {
                            chatHouse.joinRoom(chatUser, Integer.parseInt(msg.substring(msg.indexOf(" ") + 1)));
                            chatUser.write(chatUser.getName() + "님이 방에 입장했습니다.");
                        }catch (Exception e){
                            chatUser.write("300///방번호를 입력해주세요.");
                        }
                    } else if (msg.indexOf("/quit") == 0) {
                        chatUser.write("종료하시겠습니까? Y / N");
                        if (Character.toUpperCase(chatUser.read().charAt(0)) == 'Y') {
                            chatUser.write("400///quit 종료되었습니다.");
                            chatHouse.exitLobby(chatUser);
                            chatUser.close();
                        }
                    } else if (msg.indexOf("/talk") == 0) {
                        int index = 0;
                        if (chatHouse.getLobby().size() != 0) {
                            for (ChatUser cr : chatHouse.getLobby()) {
                                if (cr == chatUser) {
//                                    chatUser.write(index + " : " + cr.getName() + "(나)");
                                    index++;
                                } else {
                                    chatUser.write(index + " : " + cr.getName());
                                    index++;
                                }

                            }
                        }
                        try {
                            chatUser.write("300///입력하고자 하는 유저번호를 넣으세요.");
                            ChatUser inUser = chatHouse.getLobby().get(Integer.parseInt(chatUser.read()));
                            chatUser.write("전달할 메시지를 적어주세요.");
                            String string = chatUser.read();
                            chatHouse.chatTwo(inUser, chatUser.getName() + " : " + string);
                        }catch (Exception e){
                            chatUser.write("유저 번호가 없습니다.");
                        }
                    } else if (msg.indexOf("/help") == 0) {
                        chatUser.write("============ 메뉴 ===========");
                        chatUser.write("/create [방이름]   : 방을 생성합니다.");
                        chatUser.write("/list            : 방 리스트를 입력하세요. ");
                        chatUser.write("/join [방번호]     : 방번호를 입력하세요.");
                        chatUser.write("/quit            : 종료합니다.");
                        chatUser.write("/talk : 귓속말");
                        chatUser.write("=============================");
                    } else {
                        List<ChatUser> lobbyList = chatHouse.getLobby();
                        for (ChatUser cu : lobbyList) {
                            System.out.println(chatUser.getName() + " : " + msg);
                            cu.write("200///"+chatUser.getName() + " : " + msg);
                        }
                    }
                }
            }
        }catch (Exception ex){
            chatHouse.exitLobby(chatUser);
            chatHouse.exitRoom(chatUser);
            System.out.println(chatUser.getName() + "연결 종료");
        }

    }
}