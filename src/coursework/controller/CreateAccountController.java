package coursework.controller;

import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import coursework.model.*;
import coursework.utils.DBUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

// удалено request

public class CreateAccountController implements Initializable {

    @FXML
    private Label close;
    @FXML
    private TextField username;
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label word;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        password.setTooltip(new Tooltip("Пароль должен содержать не менее 8 символов"));
        confirmPassword.setTooltip(new Tooltip("Пароль должен содержать не менее 8 символов"));
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

    private boolean validateEmail() {
        Pattern p = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        Matcher M = p.matcher(email.getText());
        if (M.find() && M.group().equals(email.getText())) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.ERROR, "", "Проверьте корректность ввода поля Почта");
            return false;
        }
    }

    private boolean validateFields() {
        if (username.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty() || confirmPassword.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION, "", "Заполните все поля");
            return false;
        }
        return true;
    }


    @FXML
    private void createAccount(ActionEvent event) throws IOException {
        boolean checkUserOnExist = DBUtils.checkIfUserAccountIsExist(username.getText());

        if (validateFields() && validateEmail() && validatePasswordLength() && !checkUserOnExist) {
            User user = new User(name.getText(), surname.getText(), username.getText(), email.getText(), password.getText(), "Librarian");

            if (password.getText().equals(confirmPassword.getText())) {
                DBUtils.register(user);

                Notification notification = new Notification("", "Аккаунт создан", 3);
                LoadStage stage = new LoadStage("/coursework/view/main.fxml", confirmPassword);
            } else {
                Alert alert = new Alert(AlertType.INFORMATION, "", "Пароль не совпадает");
                password.clear();
                confirmPassword.clear();
            }
        } else {
            Alert alert = new Alert(AlertType.INFORMATION, "", "Пользователь с таким логином уже существует. Попробуйте другой");

        }


    }

    @FXML
    private void caancel(ActionEvent event) throws IOException {
        LoadStage stage = new LoadStage("/coursework/view/login.fxml", confirmPassword);
    }


    private boolean validatePasswordLength() {
        if (password.getText().length() < 8 || confirmPassword.getText().length() < 8) {
            Alert alert = new Alert(AlertType.ERROR, "", "Пароль должен содержать не менее 8 символов");
            return false;
        }
        return true;
    }
}
