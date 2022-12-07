
package coursework.model;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class Notification {

    public Notification(String title, String text, int seonds) {
        Notifications notifications = Notifications.create()
                .title(title)
                .text(text)
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(Duration.seconds(seonds));
        switch (title) {
            case "Information":
                notifications.showInformation();
                break;
            case "Message":
                notifications.showInformation();
                break;
            case "Error":
                notifications.showError();
                break;
            case "Warning":
                notifications.showWarning();
                break;
            default:
                break;
        }
    }
}
