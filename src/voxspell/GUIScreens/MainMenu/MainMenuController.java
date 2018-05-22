package voxspell.GUIScreens.MainMenu;

import voxspell.GUIScreens.Options.Options;
import voxspell.GUIScreens.Options.OptionsController;
import voxspell.GUIScreens.Statistics.Statistics;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import voxspell.GUIScreens.Achievements.Achievements;
import voxspell.Command;
import voxspell.GUIScreens.ControlledScreen;
import voxspell.Dialogs;
import voxspell.GUIScreens.LevelSelection.LevelSelectionController;
import voxspell.GUIScreens.NonMainGUI;
import voxspell.GUIScreens.SceneManager;
import voxspell.GUIScreens.SpellingTest.SpellingTestController;
import voxspell.GUIScreens.StackedScreens;
import voxspell.VOXSPELL;

/**
 *FXML controller class.
 * This class is the GUI Screen representation for the main menu.
 * 
 * @author victor
 */
public class MainMenuController implements Initializable, ControlledScreen {

    @FXML
    private Button btnPlay;
    @FXML
    private Button btnStats;
    @FXML
    private Button btnOptions;
    @FXML
    private Label lblCurrentList;

    private StackedScreens _stackedScreens;
    @FXML
    private Button btnAchievements;
    @FXML
    private Button btnSound;
    @FXML
    private ImageView volumeIcon;

    private boolean musicPlaying;

    /**
     * Starts playing the music on start-up.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        musicPlaying = true;
    }

    
    /**
     * The play button opens the level selection window.
     * @param event 
     */
    @FXML
    private void handlePlayButton(ActionEvent event) {

        LevelSelectionController c = (LevelSelectionController) SceneManager.changeScene(this, null, "/voxspell/GUIScreens/LevelSelection/LevelSelection.fxml", "Select a level", true);
        String wordList = Options.getCurrentList();
        boolean success = c.initializeComboBox(wordList, this);
        if (!success) {
            c.closeWindow();
            Dialogs.createDialog(Alert.AlertType.ERROR, "Wordlist Error", "Error retrieving levels. Please make sure your list follows the correct level labelling format.");
        }
    }

    
    /**
     * The stats button goes to the Statistics screen.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void handleStatsButton(ActionEvent event) throws IOException {
        Command cmd = new Statistics();
        NonMainGUI statsController = (NonMainGUI) StackedScreens.getController(VOXSPELL.STATISTICS);
        cmdExecute(cmd, statsController);
        goToScreen(VOXSPELL.STATISTICS);
    }

    
    /**
     * The Options button goes to the options screen.
     * @param event 
     */
    @FXML
    private void handleOptionsButton(ActionEvent event) {
        goToScreen(VOXSPELL.OPTIONS);
        OptionsController optionsController = (OptionsController) StackedScreens.getController(VOXSPELL.OPTIONS);
        optionsController.getOptions().execute();
    }

   
    /**
     * This method associates a model class with a GUI screen class, and calls the inherited execute() method for initializing processes for that screen.
     * @param cmd
     * @param controller
     * @throws IOException 
     */
    public void cmdExecute(Command cmd, NonMainGUI controller) throws IOException {
        cmd.addGUI(controller);
        controller.setModel(cmd);
        cmd.execute();
    }

    
    /**
     * This is just to get a reference for the current scene, by giving a button of the current scene.
     * @return 
     */
    public Button getLocalButton() {
        return btnOptions;
    }

    
    /**
     * This method will go to a certain screen from the main menu.
     * @param ID 
     */
    public void goToScreen(String ID) {
        _stackedScreens.set(ID);
    }

    
    /**
     * Sets the current wordlist label to the current list.
     */
    public void setCurrentListLabel() {
        lblCurrentList.setText("Current wordlist: " + Options.getCurrentList());
    }

    /**
     * Set parent for the current screen.
     * @param screen 
     */
    @Override
    public void setParent(StackedScreens screen) {
        _stackedScreens = screen;
    }

    
    /**
     * Goes to the achievements screen. Update achievement status if necessary.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void handleAchievementsButton(ActionEvent event) throws IOException {
        Command cmd = new Achievements();
        NonMainGUI achievementsController = (NonMainGUI) StackedScreens.getController(VOXSPELL.ACHIEVEMENTS);
        cmdExecute(cmd, achievementsController);
        goToScreen(VOXSPELL.ACHIEVEMENTS);
    }

    
    /**
     * Toggles the background music.
     * @param event 
     */
    @FXML
    private void handleBtnSound(ActionEvent event) {
    	
    	VOXSPELL.toggleMusic();
        
        SpellingTestController controller = (SpellingTestController) StackedScreens.getController(VOXSPELL.SPELLING_TEST);
        
        updateVolumeIcon(volumeIcon,controller.getVolumeIcon());
        

    }
    
    
    /**
     * This updates all the relevant music button icons in the app.
     * @param image1
     * @param image2 
     */
    public void updateVolumeIcon(ImageView image1,ImageView image2) {
        
        // The mute and volume icons are credited to Material Icons at https://design.google.com/icons/.
        if (musicPlaying) {
            image1.setImage(new Image(getClass().getResourceAsStream("ic_volume_mute_black_24dp_2x.png")));
            image2.setImage(new Image(getClass().getResourceAsStream("ic_volume_mute_black_24dp_2x.png")));
            musicPlaying = false;
        } else {
            image1.setImage(new Image(getClass().getResourceAsStream("ic_volume_up_black_24dp_2x.png")));
            image2.setImage(new Image(getClass().getResourceAsStream("ic_volume_up_black_24dp_2x.png")));
            musicPlaying = true;
        }
        
    }
    
    
    /**
     * This toggles the music and updates the sound button icon appropriately.
     */
    public void toggleMusic() {
    	btnSound.fire();
    }

}
