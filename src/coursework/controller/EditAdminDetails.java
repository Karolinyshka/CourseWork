package coursework.controller;

import com.jfoenix.controls.JFXButton;
import coursework.model.DatabaseConnection;
import coursework.model.Notification;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT Username FROM User";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            user.setText(rs.getString(1));
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pre != null) {
                    pre.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }

    @FXML
    private void updateAdminInfo(ActionEvent event) {
        Connection conn = null;
        PreparedStatement pre1 = null;
        PreparedStatement pre2 = null;
        ResultSet rs = null;
        String query1 = "UPDATE User SET Username = ?,Email = ? WHERE Password = ?";
        String query2 = "SELECT * FROM User WHERE Usertype = ? AND Password = ?";
        if (validateFields() && validateEmail()) {
            try {
                conn = DatabaseConnection.Connect();
                pre1 = conn.prepareStatement(query1);
                pre2 = conn.prepareStatement(query2);
                pre2.setString(1, "Administrator");
                pre2.setString(2, password.getText().trim());
                rs = pre2.executeQuery();
                if (rs.next()) {
                    pre1.setString(1, username.getText().trim());
                    pre1.setString(2, email.getText().trim());
                    pre1.setString(3, password.getText());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText(null);
                    alert.setContentText("Save changes ?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if (option.get() == ButtonType.OK) {
                        pre1.executeUpdate();
                        fetchUserName();
                        Notification notification = new Notification("", "Изменения сохранены", 3);
                        password.clear();
                    }
                } else {
                   coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "", "Ошибка в пароле");
                    password.clear();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (pre1 != null) {
                        pre1.close();
                    }
                    if (pre2 != null) {
                        pre2.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.err.println(ex);
                }
                loadAdminInformation();
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        username.clear();
        email.clear();
        loadAdminInformation();
    }

    private void loadAdminInformation() {
        String query = "SELECT * FROM User WHERE Usertype = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "Administrator");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                username.setText(resultSet.getString(4));
                email.setText(resultSet.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(EditAdminDetails.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(EditAdminDetails.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
