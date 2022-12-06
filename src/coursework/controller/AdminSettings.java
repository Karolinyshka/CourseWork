package coursework.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminSettings implements Initializable {


    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXButton adminInfo;
    @FXML
    private JFXButton signInOptions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/editAdminDetails.fxml"));
            borderPane.setCenter(pane);
        } catch (IOException ex) {
            Logger.getLogger(AdminSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @FXML
    private void loadAdmininfo(ActionEvent event) throws IOException {
        BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/editAdminDetails.fxml"));
        borderPane.setCenter(pane);
    }

    @FXML
    private void loadSignInOptions(ActionEvent event) throws IOException {
        BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/changeAdminPassword.fxml"));
        borderPane.setCenter(pane);
    }



}
