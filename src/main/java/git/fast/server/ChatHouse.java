package git.fast.server;

import java.util.ArrayList;
import java.util.List;

public class ChatHouse {
    ArrayList<ChatUser> lobby;
    ArrayList<ChatRoom> rooms;

    public ChatHouse(){
        lobby =  new ArrayList<ChatUser>();
        rooms = new ArrayList<ChatRoom>();
    }

    public void addLobby(ChatUser chatUser){
        lobby.add(chatUser);
    }

    public void createRoom(ChatUser chatUser, String title){
        rooms.add(new ChatRoom(chatUser, title));
        chatUser.setRoom(true);
    }

    public void joinRoom(ChatUser chatUser , int index){
        rooms.get(index).addChatUser(chatUser);
        chatUser.setRoom(true);
    }

    public void exitRoom(ChatUser chatUser){
        for(ChatRoom cr : rooms){
            if(cr.existChatUser(chatUser)){
                cr.getChatUsers().remove(chatUser);
                addLobby(chatUser);
                chatUser.setRoom(false);
                // 방에 사람이 한명도 없으면 방 삭제.
                if(cr.getSize() == 0){
                    deleteRoom(cr);
                }
                break;
            }
        }
    }

    public void exitLobby(ChatUser chatUser){
        lobby.remove(chatUser);
    }

    public List<ChatUser> getChatUsers(ChatUser chatUser){
        for(ChatRoom cr : rooms){
            if(cr.existChatUser(chatUser)){
                return cr.getChatUsers();
            }
        }
        return new ArrayList<ChatUser>();
    }

    public List<ChatRoom> getRooms(){
        return rooms;
    }

    public void deleteRoom(ChatRoom chatRoom){
        rooms.remove(chatRoom);
    }

}
