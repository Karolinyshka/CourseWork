package coursework.controller;

import com.jfoenix.controls.JFXButton;
import coursework.utils.DBUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import coursework.model.DatabaseConnection;
import coursework.model.Notification;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class Password implements Initializable {

    @FXML
    private PasswordField currentPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField newpassword2;
    @FXML
    private JFXButton save;
    @FXML
    private JFXButton cancel;
    @FXML
    private JFXButton change;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentPassword.setUserData("password1");
        newPassword.setUserData("password2");
        newpassword2.setUserData("password3");
        requestFocus(newPassword);
        requestFocus(newpassword2);
        requestFocus(currentPassword);
        newPassword.setTooltip(new Tooltip("The password must be at least 8 characters long"));
        newpassword2.setTooltip(new Tooltip("The password must be at least 8 characters long"));
    }

    //TODO:DONE
    @FXML
    private void changePassword(ActionEvent event) {
        if (validateFields() && validatePasswordLength()) {
            if (newPassword.getText().equals(newpassword2.getText())) {
                String s = DBUtils.changePassword(LoginController.userID, currentPassword.getText(), newPassword.getText());
                Notification notification = new Notification("Information", s, 3);
                change.setDisable(false);
                clearFields();
                disablePasswordFieldsandButtons();

            } else {
                coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", "Passwords don't match");
                newPassword.clear();
                newpassword2.clear();
            }
        }

    }

    @FXML
    private void enablePasswordFieldsandButtons(ActionEvent event) {
        change.setDisable(true);
        currentPassword.setEditable(true);
        newPassword.setEditable(true);
        newpassword2.setEditable(true);
        save.setDisable(false);
        cancel.setDisable(false);
    }

    private boolean validateFields() {
        if (currentPassword.getText().isEmpty() || newPassword.getText().isEmpty() || newpassword2.getText().isEmpty()) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Fields validation", "Please enter in all fields");
            return false;
        } else {
            return true;
        }
    }

    private void disablePasswordFieldsandButtons() {
        currentPassword.setEditable(false);
        newPassword.setEditable(false);
        newpassword2.setEditable(false);
        save.setDisable(true);
        cancel.setDisable(true);
    }

    @FXML
    private void cancel(ActionEvent event) {
        change.setDisable(false);
        clearFields();
        disablePasswordFieldsandButtons();
    }

    private void clearFields() {
        currentPassword.clear();
        newPassword.clear();
        newpassword2.clear();
    }

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("password1")) {
                        newPassword.requestFocus();
                    }
                    if (field.getUserData().equals("password2")) {
                        newpassword2.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("password3")) {
                        newPassword.requestFocus();
                    }
                    if (field.getUserData().equals("password2")) {
                        currentPassword.requestFocus();
                    }
                }
            }
        });
    }

    private boolean validatePasswordLength() {
        if (newPassword.getText().length() < 8 || newpassword2.getText().length() < 8) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Password length validation", "The password must be at least 8 characters long");
            return false;
        }
        return true;
    }
}
