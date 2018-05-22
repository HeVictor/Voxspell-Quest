package voxspell.GUIScreens.Options;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import voxspell.GUIScreens.Achievements.Achievements;
import voxspell.Command;
import voxspell.GUIScreens.ControlledScreen;
import voxspell.Dialogs;
import voxspell.FileHandler;
import voxspell.GUIScreens.MainMenu.MainMenuController;
import voxspell.GUIScreens.NonMainGUI;
import voxspell.GUIScreens.StackedScreens;
import voxspell.VOXSPELL;

/**
 * FXML Controller class GUI screen class for Options.
 *
 * @author victor
 */
public class OptionsController implements Initializable, ControlledScreen, NonMainGUI {

    private StackedScreens _screensController;

    private Options modelController;

    @FXML
    private Button btnUpload;
    @FXML
    private Button btnSetList;
    @FXML
    private ComboBox<String> wordlistSelectBox;
    @FXML
    private Button btnBack;
    @FXML
    private ComboBox<String> musicSelectBox;
    @FXML
    private Button btnSetMusic;
    @FXML
    private Button btnDelete;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Code retrieved from http://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm This method uploads a user-selected wordlist to the Wordlist directory, to be able to be selected for SpellingTest.
     *
     * @param event
     */
    @FXML
    private void handleBtnUpload(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your wordlist");
        File file = fileChooser.showOpenDialog(btnUpload.getScene().getWindow());

        if (file != null) {

            String newWordList = file.getName();

            // This is to handle if user accidentally uploads files already inside Wordlists, which would delete the content if this code was not here.
            if (file.getAbsolutePath().equals(new File("Wordlists/" + newWordList).getAbsolutePath())) {
                Dialogs.createDialog(Alert.AlertType.WARNING, "Uploading duplicate file", " Please upload a file that is not already in the Wordlist folder");
                return;
            }

            FileHandler.copyFile(file, new File("Wordlists/" + newWordList));
            updateWordlistSelection(newWordList);

            FileHandler.createNewWordlistStatsFolder(newWordList);

            FileHandler.updateAchievementStatus(Achievements.UPLOADED);
        }

    }

    /**
     * This method updates the wordlist selection combobox in the GUI.
     *
     * @param listName
     */
    private void updateWordlistSelection(String listName) {

        if (!wordlistSelectBox.getItems().contains(listName)) {
            wordlistSelectBox.getItems().add(listName);
        }
    }

    /**
     * Returns the Options model controller.
     *
     * @return
     */
    public Options getOptions() {
        return modelController;
    }

    /**
     * This gets the currently selected wordlist item from the combo box.
     *
     * @return
     */
    public String getSelectedList() {
        return wordlistSelectBox.getSelectionModel().getSelectedItem();
    }

    /**
     * This gets the currently selected music item from the combo box.
     *
     * @return
     */
    public String getSelectedMusic() {
        return musicSelectBox.getSelectionModel().getSelectedItem();
    }

    /**
     * To be only called on app start-up, this sets the default list and combobox selection to Adventure.
     */
    public void defaultMusicSelection() {
        modelController.setMusic("Adventure");
        musicSelectBox.setValue("Adventure");
    }

    /**
     * To be only called on app start-up, this sets the default list and combobox selection to the NZCER list.
     */
    public void defaultWordlistSelection() {
        modelController.setList("NZCER-spelling-lists.txt");
        wordlistSelectBox.setValue("NZCER-spelling-lists.txt");
    }

    /**
     * Sets up comboboxes with a String list, and default select the first element.
     *
     * @param cb
     * @param lists
     */
    private void setupComboBox(ComboBox<String> cb, String[] lists) {
        cb.getItems().setAll(lists);
        cb.setValue(lists[0]);
    }

    /**
     * Set up the wordlist select combobox.
     *
     * @param lists
     */
    public void setupWordlistSelection(String[] lists) {
        setupComboBox(wordlistSelectBox, lists);
    }

    /**
     * Set up the music select combobox.
     *
     * @param lists
     */
    public void setupMusicSelection(String[] lists) {
        setupComboBox(musicSelectBox, lists);
    }

    /**
     * Update the current highlighted combobox select with the current music and wordlist.
     */
    public void updateComboBoxSelection() {
        musicSelectBox.setValue(Options.getCurrentMusic());
        wordlistSelectBox.setValue(Options.getCurrentList());
    }

    /**
     * Sets the current wordlist for the application.
     *
     * @param event
     */
    @FXML
    private void handleBtnSetList(ActionEvent event) {
        modelController.setList(wordlistSelectBox.getValue());

        Dialogs.createDialog(Alert.AlertType.INFORMATION, "Wordlist Set", "Current wordlist set to " + wordlistSelectBox.getValue());

        // This updates the MainMenuGUI to display the current list.
        MainMenuController mainMenuController = (MainMenuController) StackedScreens.getController(VOXSPELL.MAIN_MENU);
        mainMenuController.setCurrentListLabel();
    }

    /**
     * Sets the parent screen.
     *
     * @param screen
     */
    @Override
    public void setParent(StackedScreens screen) {
        _screensController = screen;
    }

    /**
     * Set the model class.
     *
     * @param cmd
     */
    @Override
    public void setModel(Command cmd) {
        modelController = (Options) cmd;
    }

    /**
     * Go back to Main Menu.
     *
     * @param event
     */
    @FXML
    private void handleBtnBack(ActionEvent event) {
        _screensController.set(VOXSPELL.MAIN_MENU);
    }

    @FXML
    private void handlewordlistSelectBox(ActionEvent event) {
    }

    @FXML
    private void handleMusicSelectBox(ActionEvent event) {
    }

    /**
     * Sets current background music.
     *
     * @param event
     */
    @FXML
    private void handleBtnSetMusic(ActionEvent event) {
        modelController.setMusic(musicSelectBox.getValue());
        VOXSPELL.setMusic(".media/music/" + musicSelectBox.getValue());
        Dialogs.createDialog(Alert.AlertType.INFORMATION, "Music Set", "Current music set to " + musicSelectBox.getValue());
    }

    /**
     * Deletes a wordlist from available lists. Automatically sets the current list to the next available one if
     * the list deleted is the current one.
     * @param event 
     */
    @FXML
    private void handleBtnDelete(ActionEvent event) {
        try {

            String listToDelete = wordlistSelectBox.getValue();
            String msg = "Are you sure you want to delete " + listToDelete + "?";

            if (listToDelete.equals(Options.getCurrentList())) {
                msg = msg + " This is your current list.";
            }

            Optional<ButtonType> result = Dialogs.createDialog(Alert.AlertType.CONFIRMATION, "Deleting wordlist", msg);

            if (result.get() == ButtonType.OK) {
                Files.delete(new File("Wordlists/" + listToDelete).toPath());

                this.setupWordlistSelection(FileHandler.getAllFileContents("Wordlists"));

                if (listToDelete.equals(Options.getCurrentList())) {
                    btnSetList.fire();
                } else {
                    wordlistSelectBox.setValue(Options.getCurrentList());
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(OptionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
