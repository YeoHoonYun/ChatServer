package git.fast.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ChatUser {
    private String name;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isRoom;


    public ChatUser(Socket socket) {
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            throw new RuntimeException("   ");
        }

    }

    public void close() {
        try {in.close();} catch (Exception ignore) {}
        try {out.close();} catch (Exception ignore) {}
        try {socket.close();} catch (Exception ignore) {}
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public boolean isRoom() {
        return isRoom;
    }

    public void setRoom(boolean room) {
        isRoom = room;
    }

    public void wrtie(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (Exception ex) {
            throw new RuntimeException("메시지 전송시 오류");
        }
    }

    public String read() {
        try {
            return in.readUTF();
        } catch (Exception ex) {
            throw new RuntimeException("메시지 읽을떄 오류");
        }
    }
}
