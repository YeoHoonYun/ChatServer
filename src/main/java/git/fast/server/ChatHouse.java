package git.fast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatHouse {
    List<ChatUser> lobby;
    List<ChatRoom> rooms;

    public ChatHouse(){
        lobby = Collections.synchronizedList(new ArrayList<>());
        rooms = Collections.synchronizedList(new ArrayList<>());
    }

    public void addLobby(ChatUser chatUser){
        lobby.add(chatUser);
    }

    public List<ChatUser> getLobby(){   return lobby; }

    public void createRoom(ChatUser chatUser, String title){
        rooms.add(new ChatRoom(chatUser, title));
        chatUser.setisRoom(true);
        chatUser.setIsAdmin(true);
        exitLobby(chatUser);
    }

    public void joinRoom(ChatUser chatUser , int index){
        rooms.get(index).addChatUser(chatUser);
        chatUser.setisRoom(true);
        exitLobby(chatUser);
    }

    public void exitRoom(ChatUser chatUser){
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


    public void exitLobby(ChatUser chatUser){
        lobby.remove(chatUser);
    }

    public List<ChatUser> getChatUsers(ChatUser chatUser){
        for(ChatRoom cr : rooms){
            if(cr.existsUser(chatUser)){
                return cr.getChatUsers();
            }
        }
        return new ArrayList<ChatUser>();
    }

    public ChatRoom getChatRoom(ChatUser chatUser){
        for(ChatRoom cr : rooms){
            if(cr.existsUser(chatUser)){
                return cr;
            }
        }
        return null;
    }

    public List<ChatRoom> getRooms(){
        return rooms;
    }

    public void deleteRoom(ChatRoom chatRoom){
        rooms.remove(chatRoom);
    }

    public ChatUser roomCreater(ChatRoom chatRoom){
        for(ChatUser chatUser : chatRoom.getChatUsers()){
            if(chatUser.isAdmin()){
                return chatUser;
            }
        }
        return null;
    }
    public void chatTwo(ChatUser to, String text){
        to.write("200///"+text);
    }
}
