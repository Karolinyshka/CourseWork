package coursework.utils;

import coursework.command.ClientAction;
import coursework.connection.ClientServerConnection;
import coursework.dto.ChangePasswordDto;
import coursework.dto.ClientResponseAfterLogin;
import coursework.dto.LoginDto;
import coursework.model.User;
import javafx.scene.control.PasswordField;
import jfxtras.scene.layout.HBox;

import java.io.IOException;
import java.net.Socket;


public class DBUtils {
    public static ClientServerConnection clientServerConnection;

    static {
        try {
            clientServerConnection = new ClientServerConnection(new Socket("127.0.0.1", 8001));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean checkUserOnExist(String username, String password) {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);
        clientServerConnection.writeObject(ClientAction.CHECK_IF_ACCOUNT_EXIST);
        clientServerConnection.writeObject(loginDto);
        return (boolean) clientServerConnection.readObject();
    }

    public static ClientResponseAfterLogin login(String login, String password) {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(login);
        loginDto.setPassword(password);
        clientServerConnection.writeObject(ClientAction.LOGIN);
        clientServerConnection.writeObject(loginDto);
        return (ClientResponseAfterLogin) clientServerConnection.readObject();
    }

    public static String changePassword(int userID, String currentPassword, String newPassword) {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setUserId(userID);
        changePasswordDto.setCurrentPassword(currentPassword);
        changePasswordDto.setNewPassword(newPassword);
        clientServerConnection.writeObject(ClientAction.CHANGE_PASSWORD);
        clientServerConnection.writeObject(changePasswordDto);
        return (String) clientServerConnection.readObject();
    }

    public static void register(User user) {
        clientServerConnection.writeObject(ClientAction.REGISTRATION);
        clientServerConnection.writeObject(user);
    }

    public static boolean checkIfUserAccountIsExist(String username) {
        clientServerConnection.writeObject(ClientAction.CHECK_IF_ACCOUNT_ALREADY_EXIST);
        clientServerConnection.writeObject(username);
        return (boolean) clientServerConnection.readObject();
    }
}
