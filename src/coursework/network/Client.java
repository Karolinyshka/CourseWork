package coursework.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client{
    private static Client connection;
    private Socket clientSock;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    private String message;

    public Client(){
        try {
            clientSock = new Socket(InetAddress.getLocalHost(), 7878);
            outStream = new ObjectOutputStream(clientSock.getOutputStream());
            inStream = new ObjectInputStream(clientSock.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Client getInstance() {
        if (connection == null) {
            connection = new Client();
        }
        return connection;
    }


    public void sendMessage(String message){
        try {
            outStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendObject(Object object){
        try {
            outStream.flush();
            outStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage(){
        try {
            message = (String) inStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    public Object readObject(){
        Object object = new Object();
        try {
            object = inStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }


    public void close() {
        try {
            clientSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}