package git.fast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatHouse {
    List<ChatUser> lobby;
    List<ChatRoom> rooms;

    public ChatHouse(){
        lobby = new ArrayList<>();
        rooms = new ArrayList<>();
    }

    public synchronized void addLobby(ChatUser chatUser){
        lobby.add(chatUser);
    }

    public synchronized List<ChatUser> getLobby(){   return lobby; }

    public synchronized void createRoom(ChatUser chatUser, String title){
        rooms.add(new ChatRoom(chatUser, title));
        chatUser.setisRoom(true);
        chatUser.setIsAdmin(true);
        exitLobby(chatUser);
    }

    public synchronized void joinRoom(ChatUser chatUser , int index){
        rooms.get(index).addChatUser(chatUser);
        chatUser.setisRoom(true);
        exitLobby(chatUser);
    }

    public synchronized void exitRoom(ChatUser chatUser){
        for(ChatRoom cr : rooms){
            if(cr.existsUser(chatUser)){
                cr.getChatUsers().remove(chatUser);
                addLobby(chatUser);
                chatUser.setisRoom(false);
                //방장 바꾸기..
                chatUser.setIsAdmin(false);

                // 방에 사람이 한명도 없으면 방 삭제.
                if(cr.getChatUsers().size() == 0){
                    deleteRoom(cr);
                }else{
                    cr.getChatUsers().get(0).setIsAdmin(true);
                }
                break;
            }
        }
    }


    public synchronized void exitLobby(ChatUser chatUser){
        lobby.remove(chatUser);
    }

    public synchronized List<ChatUser> getChatUsers(ChatUser chatUser){
        for(ChatRoom cr : rooms){
            if(cr.existsUser(chatUser)){
                return cr.getChatUsers();
            }
        }
        return new ArrayList<ChatUser>();
    }

    public synchronized ChatRoom getChatRoom(ChatUser chatUser){
        for(ChatRoom cr : rooms){
            if(cr.existsUser(chatUser)){
                return cr;
            }
        }
        return null;
    }

    public synchronized List<ChatRoom> getRooms(){
        return rooms;
    }

    public synchronized void deleteRoom(ChatRoom chatRoom){
        rooms.remove(chatRoom);
    }

    public synchronized ChatUser roomCreater(ChatRoom chatRoom){
        for(ChatUser chatUser : chatRoom.getChatUsers()){
            if(chatUser.isAdmin()){
                return chatUser;
            }
        }
        return null;
    }
    public synchronized void chatTwo(ChatUser to, String text){
        to.write("200///"+text);
    }

    public void printHelp(int option, ChatUser chatUser){
        switch(option) {
            case 0 : chatUser.write("======== 메뉴(방장) =========");
                     chatUser.write(" /exit   : 방나가기");
                     chatUser.write(" /kick   : 유저 강퇴");
                     chatUser.write(" /help   : 도움말");
                     chatUser.write(" /talk : 귓속말");
                     chatUser.write(" /visi 로비유저번호 : 초대하기");
                     chatUser.write("============================");
                     break;

            case 1 : chatUser.write("============ 메뉴 ===========");
                     chatUser.write(" /exit   : 방나가기");
                     chatUser.write(" /help   : 도움말");
                     chatUser.write(" /talk : 귓속말");
                     chatUser.write("============================");
                     break;
        }
    }

    public void printChatUsers(ChatUser chatUser) {
        int index = 0;
        for (ChatUser cu : getChatUsers(chatUser)) {
            if (index != 0) {
                chatUser.write(index + " | 이름 : " + cu.getName());
            }
            index++;
        }
    }
}
