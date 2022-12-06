package coursework.controller;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import coursework.model.DatabaseConnection;
import coursework.model.LoadStage;
import coursework.model.Notification;

public class MainController implements Initializable {

    @FXML
    private BorderPane borderpane;
    @FXML
    private JFXButton back;
    @FXML
    private JFXButton settings;
    @FXML
    private JFXButton close;
    @FXML
    private JFXButton books;
    @FXML
    private JFXButton home;
    @FXML
    private VBox homeButtonsPanel;
    double x = 0, y = 0;
    @FXML
    private JFXButton students;
    @FXML
    private JFXButton issuedBooks;
    @FXML
    private JFXButton retunbooks;
    @FXML
    private JFXButton allIssuedBooks;
    @FXML
    private JFXButton export;
    @FXML
    private JFXButton announcement;
    public static BorderPane pane;
    @FXML
    private JFXButton clearance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pane = borderpane;
        back.setTooltip(new Tooltip("Logout"));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(4000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        checkLateFee(" Late fee not set");
                    }
                });
                return null;
            }
        };
        try {
            BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/NewProduct.fxml"));
            borderpane.setCenter(borderPane);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @FXML
    private void closeApp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to close the application ?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {

        LoadStage stage = new LoadStage("/coursework/view/login.fxml", home);
    }




    public static void checkLateFee(String message) {
        String query = "SELECT * FROM Account";
        String insertQuery = "INSERT INTO Account (LateFeePerDay,LateFeePerHour) VALUES (0.00,0.00)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement1 = connection.prepareStatement(insertQuery);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double lateFeePerDay = resultSet.getDouble(2);
                double lateFeePerHour = resultSet.getDouble(3);
                if (lateFeePerDay == 0.0 || lateFeePerHour == 0.0) {
                    Notification notification = new Notification("Information", message, 5);
                }
            } else {
                preparedStatement1.executeUpdate();
                Notification notification = new Notification("Information", message, 5);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void stageDragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }
    @FXML
    private void loadStudentPanel(ActionEvent event) throws IOException {

        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/Clients.fxml"));
        borderpane.setCenter(borderPane);
    }
    @FXML
    private void loadPro(ActionEvent event) throws IOException {

        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/ExportData.fxml"));
        borderpane.setCenter(borderPane);
    }

    @FXML
    private void loadHomePanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/NewProduct.fxml"));
        borderpane.setCenter(borderPane);
    }

    @FXML
    private void stagePressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
    @FXML
    private void loadIssueBooksPanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/issueProduct.fxml"));
        borderpane.setCenter(borderPane);
    }
    @FXML
    private void loadStaticPanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/Graphic.fxml"));
        borderpane.setCenter(borderPane);
    }
    @FXML
    private void loadSettingsPanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/Password.fxml"));
        borderpane.setCenter(borderPane);
    }


}
