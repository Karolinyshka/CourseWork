package coursework.controller;

import com.jfoenix.controls.JFXButton;
import coursework.dto.AdminDto;
import coursework.model.DatabaseConnection;
import coursework.model.Notification;
import coursework.utils.DBUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.sqlite.core.DB;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditAdminDetails implements Initializable {

    @FXML
    private JFXButton cancel;
    @FXML
    private Label user;
    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private JFXButton update;
    @FXML
    private PasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fetchUserName();
        loadAdminInformation();
        username.setUserData("username");
        email.setUserData("email");
        password.setUserData("password");
        requestFocus(username);
        requestFocus(email);
        requestFocus(password);
    }

    private boolean validateFields() {
        if (username.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty()) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "", "Заполните все поля");
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        Pattern p = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(email.getText());
        if (m.find() && m.group().equals(email.getText())) {
            return true;
        }
        coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Email Validation", "Email is invalid");
        return false;
    }

    private void fetchUserName() {
        AdminDto adminDto = DBUtils.fetchUsername();
        user.setText(adminDto.getUsername());
    }

    @FXML
    private void updateAdminInfo(ActionEvent event) {
        AdminDto adminDto = new AdminDto();
        adminDto.setUsername(username.getText().trim());
        adminDto.setPassword(password.getText().trim());
        adminDto.setEmail(email.getText().trim());
        if (validateFields() && validateEmail()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Save changes ?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                DBUtils.updateAdminInfo(adminDto);
                fetchUserName();
                Notification notification = new Notification("", "Изменения сохранены", 3);
                password.clear();
            }
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "", "Ошибка в пароле");
            password.clear();
        }

        loadAdminInformation();

    }

    @FXML
    private void cancel(ActionEvent event) {
        username.clear();
        email.clear();
        loadAdminInformation();
    }

    private void loadAdminInformation() {
        AdminDto adminDto = DBUtils.loadAdminInfo();
        username.setText(adminDto.getUsername());
        email.setText(adminDto.getEmail());
//        password.setText(adminDto.getPassword());
    }

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("username")) {
                        email.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        password.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("password")) {
                        email.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        username.requestFocus();
                    }
                }
            }
        });
    }
}
