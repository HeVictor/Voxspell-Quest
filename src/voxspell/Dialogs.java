package voxspell;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * This class only contains static methods to create dialogs on demand. 
 * Methods retrieved from code.makery.ch/blog/javafx-dialogs-official/
 * @author victor
 */
public class Dialogs {
    
    /**
     * Creates a headerless dialog, specifies which type and also the messages and titles.
     * 
     * @param type
     * @param title
     * @param info
     * @return 
     */
    public static Optional<ButtonType> createDialog(Alert.AlertType type, String title, String info) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(info);
        Optional<ButtonType> result = alert.showAndWait();
        
        return result;
    }
}
