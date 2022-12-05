
package coursework.model;


public class Alert {
       public Alert(javafx.scene.control.Alert.AlertType type,String title,String text){
              javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
              alert.setTitle(title);
              alert.setHeaderText(null);
              alert.setContentText(text);
              alert.showAndWait();
       }
}
