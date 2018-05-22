package voxspell.GUIScreens.Achievements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import voxspell.Command;
import voxspell.Dialogs;
import voxspell.FileHandler;
import voxspell.GUIScreens.ControlledScreen;
import voxspell.Players.MediaPlayer;
import voxspell.GUIScreens.NonMainGUI;
import voxspell.GUIScreens.StackedScreens;
import voxspell.GUIScreens.MainMenu.MainMenuController;
import voxspell.VOXSPELL;

/**
 * FXML Controller class.
 * Achievements screen GUI class.
 *
 * @author victor
 */
public class AchievementsController implements Initializable, ControlledScreen, NonMainGUI {

    private StackedScreens _screensController;

    private Achievements modelController;
    @FXML
    private Button btnUploadReward;
    @FXML
    private Button btnStreak5Reward;
    @FXML
    private Button btn3StreakReward;
    @FXML
    private Button btnMaster10;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnReset;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Disable all buttons by default, at the start.
        this.btnUploadReward.setDisable(true);
        this.btn3StreakReward.setDisable(true);
        this.btnStreak5Reward.setDisable(true);
        this.btnMaster10.setDisable(true);
    }

    /**
     * Sets the parent screen.
     * @param screen 
     */
    @Override
    public void setParent(StackedScreens screen) {
        _screensController = screen;
    }

    /**
     * Sets the model class.
     * @param cmd 
     */
    @Override
    public void setModel(Command cmd) {
        modelController = (Achievements) cmd;
    }

    /**
     * Unlocks new background music for uploading a wordlist.
     * @param event 
     */
    @FXML
    private void handleBtnUpload(ActionEvent event) {
        unlockMusic("In The Name Of All");
    }

    /**
     * Unlocks new background music for achieving best streak of 5.
     * @param event 
     */
    @FXML
    private void handleBtnStreak5(ActionEvent event) {
        unlockMusic("The Adventure Begins");
    }

    /**
     * Unlocks new video reward for achieving best streak of 3.
     * @param event 
     */
    @FXML
    private void handleBtn3Streak(ActionEvent event) {
        new MediaPlayer(".media/sintel-1024-surround.mp4").setupGUI();

        // This disables the background music when the video starts.
        MainMenuController controller = (MainMenuController) StackedScreens.getController(VOXSPELL.MAIN_MENU);
        controller.toggleMusic();
    }
    
    
    /**
     * This unlocks new background music by moving a music file from a "locked" directory to the "music" directory.
     * @param fileName 
     */
    private void unlockMusic(String fileName) {
        
         try {
            Files.move(new File(".media/music/locked/"+fileName).toPath(), new File(".media/music/"+fileName).toPath(), REPLACE_EXISTING);
            Dialogs.createDialog(AlertType.INFORMATION, "New Music Unlocked!", "You have unlocked a new background music! Go check it out in Options!");
        } catch (IOException e) {
            // This Dialog indicates the achievement has already been activated, as a FileNotFoundException will be thrown.
            Dialogs.createDialog(AlertType.INFORMATION, "Achievement Claimed!", "You have already unlocked the reward music! You can check it out in Options.");
        }
        
    } 

    /**
     * Unlocks new video reward for mastering 10 words in a level.
     * @param event 
     */
    @FXML
    private void handleBtnMaster10(ActionEvent event) {
        new MediaPlayer(".media/big_buck_bunny_480p_stereo.avi").setupGUI();

        // This disables the background music when the video starts.
        MainMenuController controller = (MainMenuController) StackedScreens.getController(VOXSPELL.MAIN_MENU);
        controller.toggleMusic();
    }

    /**
     * Go back to main menu.
     * @param event 
     */
    @FXML
    private void handleBtnBack(ActionEvent event) {
        _screensController.set(VOXSPELL.MAIN_MENU);
    }

    
    /**
     * This enables and disables buttons according to their unlocked status in the .achievements file.
     * @param allStatus 
     */
    public void setButtons(boolean[] allStatus) {

        Button[] buttons = {btnUploadReward, btn3StreakReward, btnStreak5Reward, btnMaster10};

        int count = 0;
        for (Button aButton : buttons) {
            if (allStatus[count]) {
                aButton.setDisable(false);
            } else {
                aButton.setDisable(true);
            }
            count++;
        }
    }

    
    
    /**
     * This will reset all the achievements by resetting the .achievements file and disabling all buttons.
     * @param event 
     */
    @FXML
    private void handleBtnReset(ActionEvent event) {
        Optional<ButtonType> result = Dialogs.createDialog(Alert.AlertType.CONFIRMATION, "Clearing Achievements", "Are you sure you want to clear and reset your achievements?");
        if (result.get() == ButtonType.OK) {
            FileHandler.clearAchievements();
            modelController.execute();
        }

    }

}
