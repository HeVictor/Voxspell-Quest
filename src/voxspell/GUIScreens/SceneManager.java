/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell.GUIScreens;

import voxspell.GUIScreens.MainMenu.MainMenuController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * This class manages activities to do with scenes.
 * @author victor
 */
public class SceneManager {
    
    
    /**
     * Changes from one scene to another.
     * Retrieved from JavaFxTutorials at http://www.javafxtutorials.com/tutorials/switching-to-different-screens-in-javafx-and-fxml/
     * @param currentController
     * @param currentButton
     * @param fxmlFile
     * @param title
     * @param createNewWindow
     * @return 
     */
    public static Object changeScene(Initializable currentController, Button currentButton, String fxmlFile, String title, boolean createNewWindow) {
        try {
            Stage stage;
            Parent root;

            FXMLLoader loader = new FXMLLoader(currentController.getClass().getResource(fxmlFile));

            if (createNewWindow) {
                stage = new Stage();
            } else {
                stage = (Stage) currentButton.getScene().getWindow();
            }

            root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

            // Retrieved from StackOverflow user Patrick for getting the controller class.
            Object newController = loader.getController();
            return newController;
        } catch (IOException ex) {
            Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
