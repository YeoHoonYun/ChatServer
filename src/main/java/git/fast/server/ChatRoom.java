package git.fast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatRoom {

    private String title;
    private List<ChatUser> chatUsers;

    public ChatRoom(ChatUser chatUser, String title) {
        this.title = title;
        chatUsers = new ArrayList<ChatUser>();
        chatUsers.add(chatUser);

    }

    public synchronized boolean existsUser(ChatUser chatUser) {
        return chatUsers.contains(chatUser);
    }

    public String getTitle(){
        return title;
    }

    /*public void setTitle(String title){
        this.title = title;
    }*/

    public synchronized List<ChatUser> getChatUsers(){
        return chatUsers;

    }

    /*public synchronized void setChatUsers(List<ChatUser> chatUsers) {
        this.chatUsers = chatUsers;
    }*/

    public synchronized void addChatUser(ChatUser chatUser){
        chatUsers.add(chatUser);
    }

}
