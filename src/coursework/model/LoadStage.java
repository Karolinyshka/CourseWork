
package coursework.model;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class LoadStage {

    public LoadStage(String url, Node node) {
        Scene scene = null;
        try {
            Parent root = FXMLLoader.load(getClass().getResource(url));
            if (url.equals("/coursework/view/createAccount.fxml") || url.equals("/coursework/view/login.fxml") || url.equals("/coursework/view/forgetPassword.fxml")) {
                scene = new Scene(root);
            } else {
                scene = new Scene(root, 1283, 680);
            }
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoadStage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
