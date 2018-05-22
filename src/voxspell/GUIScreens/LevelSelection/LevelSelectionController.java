package voxspell.GUIScreens.LevelSelection;

import voxspell.GUIScreens.MainMenu.MainMenuController;
import voxspell.GUIScreens.SpellingTest.SpellingTest;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import voxspell.Dialogs;
import voxspell.FileHandler;
import voxspell.GUIScreens.NonMainGUI;
import voxspell.GUIScreens.StackedScreens;
import voxspell.VOXSPELL;

/**
 * FXML Controller class Level selection GUI class.
 *
 * @author victor
 */
public class LevelSelectionController implements Initializable {

    @FXML
    private ComboBox<String> levelSelect;
    @FXML
    private Button btnOK;

    private MainMenuController _mainController;

    private String _wordlist;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleLevelSelect(ActionEvent event) {
    }

    
    /**
     * This method sets up the level selection combo box based on all the levels for the current levels.
     * @param levels 
     */
    private void setupLevelComboBox(String[] levels) {
        levelSelect.getItems().addAll(levels);
        levelSelect.getSelectionModel().selectFirst();
    }

    
    /**
     * Returns the raw level name with %
     * @return 
     */
    public String getCurrentLevel() {
        return levelSelect.getValue();
    }

    
    /**
     * Initializes the combobox, and also gets the controller class (which is MainMenu) that created the window.
     * @param wordlist
     * @param mainController
     * @return 
     */
    public boolean initializeComboBox(String wordlist, Initializable mainController) {

        _wordlist = wordlist;

        _mainController = (MainMenuController) StackedScreens.getController(VOXSPELL.MAIN_MENU);

        String _fileName = "Wordlists/" + wordlist; // Placed wordlist inside a specialised folder
        String[] _allLevels = FileHandler.getAllLevelNames(_fileName);

        if (_allLevels.length == 0) {
            return false;
        }
        setupLevelComboBox(_allLevels);

        return true;
    }

    
    /**
     * This programetically closes the selection window.
     */
    public void closeWindow() {
        Stage stage = (Stage) btnOK.getScene().getWindow();
        stage.close();
    }

    
    /**
     * Closes selection window and initialize the SpellingTest with the selected level.
     * @param event 
     */
    @FXML
    private void handleBtnOK(ActionEvent event) {
        closeWindow();
        NonMainGUI controller = (NonMainGUI) StackedScreens.getController(VOXSPELL.SPELLING_TEST);
        try {

            if (_wordlist == null) {
                Dialogs.createDialog(Alert.AlertType.ERROR, "Wordlist Error", "No wordlists found. Please make sure you have wordlist files inside Wordlist folder.");
            }

            SpellingTest testMode = new SpellingTest(getCurrentLevel(), _wordlist);
            _mainController.cmdExecute(testMode, controller);
            if (testMode.getWordListSize() > 0) { // Go to spelling test screen.
            	_mainController.goToScreen(VOXSPELL.SPELLING_TEST);
            } else { // Detect no words error.
            	Dialogs.createDialog(AlertType.ERROR, "No words found for level", "Please make sure you have words beneath the level name in your wordlist file.");
            }
            
        } catch (IOException ex) {
            Logger.getLogger(LevelSelectionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
