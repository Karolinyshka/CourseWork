package coursework.db;

import javafx.scene.input.InputMethodTextRun;
import coursework.controller.loginController;
import coursework.model.Alert;


import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBWorker implements databaseInterface {
    private InputMethodTextRun usernameTextField;
    private InputMethodTextRun passwordTextField;
    private static DBWorker db = null;


    public static DBWorker getInstance() {
        if (db == null) {
            db = new DBWorker();
        }
        return db;
    }


    private boolean validateFields() {
        if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Fields validation", "Please, enter all the required fields");
            return false;
        } else {
            return true;
        }
    }
    private boolean checkIfAccountExist() {
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
                Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Information", "There is no user account, create a new account");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(loginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean logIN(String Username, String Password) {
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM User WHERE Username = ? AND Password = ?";
        if (validateFields() && checkIfAccountExist()) {
            try {
                conn = DatabaseConnection.Connect();
                pre = conn.prepareStatement(query);
                pre.setString(1, usernameTextField.getText().trim());
                pre.setString(2, passwordTextField.getText().trim());
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return false;
    }
}
