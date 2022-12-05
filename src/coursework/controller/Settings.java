package coursework.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXButton signInOptions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            BorderPane borderpane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/Password.fxml"));
            borderPane.setCenter(borderpane);
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    @FXML
    private void loadSignInOptions(ActionEvent event) throws IOException, InterruptedException {
        BorderPane borderpane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/Password.fxml"));
        borderPane.setCenter(borderpane);
    }

}
