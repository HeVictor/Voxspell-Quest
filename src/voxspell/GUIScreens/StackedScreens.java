/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell.GUIScreens;

import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 * From Angela Caicedo's blog, at https://blogs.oracle.com/acaicedo/entry/managing_multiple_screens_in_javafx1

 This StackPane class contains all of the screens of the application, and can perform all of the actions of addScreen, load, set and unload as required.
 *
 *
 * @author victor
 */
public class StackedScreens extends StackPane {

    // A collection that matches the screen's ID and the node associated.
    private HashMap<String, Node> appScreens = new HashMap<>();

    // A collection of FXML controllers matching a screen ID. Static because the available screens won't change during runtime.
    private static HashMap<String, Initializable> fxmlControllers = new HashMap<>();

    // Method for adding the screen ID, node pair to the HashMap.
    public void addScreen(String ID, Node screen) {
        appScreens.put(ID, screen);
    }
    // Method for adding the screen ID, controller pair to the other HashMap.
    public void addController(String ID, Initializable controller){
        fxmlControllers.put(ID, controller);
    }
    
    // Static method to get the FXML controller associated with a screen.
    public static Initializable getController(String ID){
        return fxmlControllers.get(ID);
    }

    // 
    /**
     * This method loads a screen given its name and associated FXML file string. It gets the screen's controller
     * and then sets the parent for the controller to be this class. Finally, the screen is added to the HashMap.
     * The return boolean value indicates whether it was successful loading the screen or not.
     * 
     * @param name
     * @param fxmlFile
     * @return 
     */
    public boolean load(String name, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            Parent newScreen = (Parent) loader.load();
            ControlledScreen childScreen = ((ControlledScreen) loader.getController());

            childScreen.setParent(this);
            this.addScreen(name, newScreen);
            
            Initializable newScreenController = loader.getController();
            this.addController(name, newScreenController);
            
            return true;
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sets the current screen to show on top.
     * @param name
     * @return 
     */
    public boolean set(final String name) {

        if (appScreens.get(name) != null) {
            //DoubleProperty opacity = opacityProperty();
            if (!getChildren().isEmpty()) { // If more than one screen in this StackPane.

                // Removes current screen and display the new screen.
                getChildren().remove(0);
                getChildren().add(0, appScreens.get(name));
            } else {

                // If there's no screen currently then simply addScreen it and show.
                getChildren().add(appScreens.get(name));
            }
            return true; // Screen has loaded.
        } else {
            System.out.println("screen hasn't been loaded!\n");
            return false; // Screen has not loaded.
        }
    }

    
    /**
     * This method removes the screen from the hashmap, and returns whether it is successful or not.    
     * @param screenName
     * @return 
     */
    public boolean unload(String screenName) {
        if (appScreens.remove(screenName) != null) {
            return true;
        } else {
            System.out.println("Screen didn't exist");
            return false;
        }
    }

}
