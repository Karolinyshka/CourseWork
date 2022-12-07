package coursework.controller;

import coursework.dto.ClientResponseAfterLogin;
import coursework.model.Alert;
import coursework.model.DatabaseConnection;
import coursework.model.LoadStage;
import coursework.model.Notification;
import coursework.utils.DBUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Label close;

    public static int userID;
    public static String userName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private boolean validateFields() {
        if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Ошибка", "Заполните все поля!");
            return false;
        } else {
            return true;
        }
    }

    private void clearFields() {
        usernameTextField.clear();
        passwordTextField.clear();
    }

    @FXML
    private void close(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void login(ActionEvent event) throws IOException {
        Optional<ClientResponseAfterLogin> clientResponseAfterLogin = Optional.ofNullable(DBUtils.login(usernameTextField.getText().trim(), passwordTextField.getText().trim()));
        int counter = 0;
        if (validateFields() && checkIfAccountExist()) {
            if(!(clientResponseAfterLogin.get().getUserId() == 0))
                switch (clientResponseAfterLogin.get().getRole()) {
                    case "Administrator": {
                        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Message", "Access granted");
                        LoadStage loadStage = new LoadStage("/coursework/view/adminPanel.fxml", close);
                        Thread thread = new Thread(new showMessage(clientResponseAfterLogin.get().getUserName()));
                        thread.setDaemon(true);
                        thread.start();
                        counter++;
                        break;
                    }
                    case "Librarian": {
                        userID = clientResponseAfterLogin.get().getUserId();
                        userName = clientResponseAfterLogin.get().getUserName();
                        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Message", "Access granted");
                        LoadStage loadStage = new LoadStage("/coursework/view/main.fxml", close);
                        counter++;
                        Thread thread = new Thread(new showMessage(clientResponseAfterLogin.get().getUserName()));
                        thread.setDaemon(true);
                        thread.start();
                        break;
                    }
            }
            if(counter == 0){
                Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Information", "Логин или пароль введены неверно");
            }

        }
    }

    @FXML
    private void loadPasswordRetrivalPanel(MouseEvent event) throws IOException {
        LoadStage stage = new LoadStage("/coursework/view/forgetPassword.fxml", close);
    }

    @FXML
    private void createAccount(MouseEvent event) throws IOException {
        LoadStage stage = new LoadStage("/coursework/view/createAccount.fxml", close);
    }

    @FXML
    private void signIn(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            login(new ActionEvent());
        }
    }

    //TODO:DONE!
    private boolean checkIfAccountExist() {
        boolean check = DBUtils.checkUserOnExist(usernameTextField.getText(), passwordTextField.getText());
        if (check == false) {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Information", "There is no user account, create a new account");
            clearFields();
            return false;
        }
        return true;
    }


    private class showMessage extends Task<Void> {
        private final String username;

        public showMessage(String username) {
            this.username = username;
        }

        @Override
        protected Void call() throws Exception {
            Thread.sleep(1500);
            Platform.runLater(() -> {
                Notification notification = new Notification("Message", "Welcome " + username, 3);
            });
            return null;
        }
    }

}
