package coursework.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class exportDataController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private ComboBox<String> reportType;
    public static String type = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            BorderPane borderpane = FXMLLoader.load(getClass().getResource("/coursework/view/exportToPdf.fxml"));
            borderPane.setCenter(borderpane);
        } catch (IOException ex) {

        }
        reportType.setItems(FXCollections.<String>observableArrayList("Students", "Books"));
        reportType.setOnAction((e) -> {
            type = reportType.getValue();
        });

    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void fullscreen(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (!stage.isFullScreen()) {
            stage.setFullScreen(true);
        }
    }

    @FXML
    private void unfullscreen(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        }
    }

    @FXML
    private void close(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void loadPDFExportPanel(ActionEvent event) throws IOException {
        BorderPane borderP = FXMLLoader.load(getClass().getResource("/coursework/view/exportToPdf.fxml"));
        borderPane.setCenter(borderP);
    }


}
