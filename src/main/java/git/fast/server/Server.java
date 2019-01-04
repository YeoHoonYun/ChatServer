package git.fast.server;

/**
 * Created by cjswo9207u@gmail.com on 2019-01-02
 * Github : https://github.com/YeoHoonYun
 */
public class Server {
    public static void main(String[] args){
        ChatServer chatServer = new ChatServer(8000);
        chatServer.run();
    }
}
