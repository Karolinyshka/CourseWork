package coursework.network;

import coursework.db.DBWorker;
import coursework.db.DatabaseConnection;
import org.hsqldb.User;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private static final String FILE = "client.txt";
    private Socket factSocket;

    private DBWorker db = DBWorker.getInstance();
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private static String currentUser;


    private String messServ = null;


    Server(Socket socket) throws IOException {
        factSocket = socket;


        try {
            inStream = new ObjectInputStream(factSocket.getInputStream());
            outStream = new ObjectOutputStream(factSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String doing = getAddress() + "\t" + "on" + "\t" + new java.util.Date().toString();
        System.out.println(doing);
        writeInFile(doing);
    }

    public void close() {
        try {
            outStream.flush();
            inStream.close();
            outStream.close();
            factSocket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private String readMessage() {
        try {
            messServ = (String) inStream.readObject();
        } catch (Exception e) {
            System.err.println("Клиент отключился");

        }
        return messServ;
    }

    private void sendMessage(String messServ) {
        try {
            outStream.flush();
            outStream.writeObject(messServ);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Object readObject() {
        Object object = null;
        try {
            object = inStream.readObject();
        } catch (Exception e) {
            System.out.println("Нет данных в потоке");
        }

        return object;
    }


    private void sendObject(Object object) {
        try {
            outStream.flush();
            outStream.writeObject(object);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }


    private String getAddress() {
        return factSocket.getInetAddress().toString();
    }

    private void writeInFile(String doing) throws IOException {

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE, true)))) {

            pw.println(doing);

        }
    }

    @Override
    public void run() {
        boolean workFlag = true;
        try {
            while ((messServ =(String) inStream.readObject()) != null) {
                switch (messServ) {
                    case "Shutdown": {
                        try {
                            String doing = getAddress() + "\t" + "off" + "\t" + new java.util.Date().toString();
                            System.out.println(doing);
                            writeInFile(doing);
                            close();
                            System.exit(0);

                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        close();
                        workFlag = false;
                    }
                    break;
                    case "LogIn": {
                        User user = (User) readObject();
                        Connection connection = null;
                        PreparedStatement preparedStatement = null;
                        ResultSet resultSet = null;
                        String selectQuery = "SELECT COUNT(*) FROM User";
                        try {
                            connection = DatabaseConnection.Connect();
                            preparedStatement = connection.prepareStatement(selectQuery);
                            resultSet = preparedStatement.executeQuery();
                            int numberOfRows = resultSet.getInt(1);
                            if (numberOfRows == 0) {
                                sendMessage("Fail");
                            } else {
                                sendMessage("Good");
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;

                    default:
                        close();
                        workFlag = false;
                }
            }
        }catch (Exception e){
        } finally {
            disconnect();
        }
    }
    private void disconnect() {
        try {
            if (outStream != null) {
                outStream.close();
            }
            if (inStream != null) {
                inStream.close();
            }
            System.out.println(factSocket.getInetAddress() + " disconnecting");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
