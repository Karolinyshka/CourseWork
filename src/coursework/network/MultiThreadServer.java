package coursework.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadServer {
    private ServerSocket serverSocket;


    MultiThreadServer(ServerSocket servSock) {
        serverSocket = servSock;
    }

    public void run(){
        try {
            while (true) {
                Socket clientSocket;
                clientSocket = serverSocket.accept();
                new Thread(new Server(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка подключения");
        }
    }
}
