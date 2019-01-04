package git.fast.server;

public class Server {
    public static void main(String[] args){
        ChatServer chatServer = new ChatServer(8000);
        chatServer.run();
    }
}
