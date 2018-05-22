package voxspell.GUIScreens.SpellingTest;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import voxspell.GUIScreens.Achievements.Achievements;
import voxspell.Command;
import voxspell.GUIScreens.ControlledScreen;
import voxspell.Dialogs;
import voxspell.FileHandler;
import voxspell.GUIScreens.MainMenu.MainMenuController;
import voxspell.Players.MusicPlayer;
import voxspell.GUIScreens.NonMainGUI;
import voxspell.GUIScreens.StackedScreens;
import voxspell.VOXSPELL;

/**
 * FXML Controller class
 * GUI screen class for Spelling Test.
 *
 * @author victor
 */
public class SpellingTestController implements Initializable, NonMainGUI, ControlledScreen {

    private int count = 0;
    private SpellingTest modelController;
    private int iterations = 0;
    private int currentStreak = 0;

    private StackedScreens _screensController;

    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnRelisten;
    @FXML
    private Label spellLabel;
    @FXML
    private TextField txtField;
    @FXML
    private Label levelLabel;
    @FXML
    private ComboBox<String> voiceSelect;
    @FXML
    private Button btnNextLevel;
    @FXML
    private Label lblStreak;
    @FXML
    private Label lblNewPersonalBest;
    @FXML
    private ImageView imgCorrectness;
    @FXML
    private ImageView imgHeart;
    @FXML
    private Group animationGroup;

    private Animations _animation;
    @FXML
    private Label lblCorrectAnswer;
    @FXML
    private Button btnSound;
    @FXML
    private ImageView volumeIcon;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblScore;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    
    /**
     * This is only to be called by SpellingTest to reset imgCorrectness.
     */
    public void resetCorrectnessIcon() {
        imgCorrectness.setImage(null);
    }
    
    
    /**
     * This clears the answer label.
     */
    public void clearLblAnswer() {
        lblCorrectAnswer.setText("");
    }

    
    /**
     * This is only to be called by SpellingTest to reset imgHeart.
     */
    public void resetHeartImg() {
        imgHeart.setImage(new Image(getClass().getResourceAsStream("Images/UI_HEART_FULL.png")));
    }

    
    /**
     * Handles submitting user's attempt.
     * Base of the code below originally by Jacky Lo.
     * @param event 
     */
    @FXML
    private void handleBtnSubmit(ActionEvent event) {
        
        this.clearLblAnswer();

        if (iterations != 10 || modelController.getWordListSize() > iterations) {
            String userInput = txtField.getText(); // gets what the user entered into the JTextField
            txtField.setText("");
            if (userInput.equals("")) {
                // sends a warning if empty input 
                Dialogs.createDialog(AlertType.WARNING, "Warning!", "Uh oh! You didn't write anything!");
            } else if (!userInput.chars().allMatch(Character::isLetter) && userInput.indexOf('\'') == -1){
                // Code for checking string only containing alphabets retrieved from Max Malysh and Journeycorner at StackOverflow
                // Code for detecting the existence of a single char retrieved from mP. and SWPhantom at StackOverflow.
                
                // sends a warning if input contains non-apostrophe special characters
                Dialogs.createDialog(AlertType.WARNING, "Warning!", "Make sure your answer only contains alphabets, and apostrophes if necessary.");
            } else if (modelController.getWordListSize() > 0) {
                // this is the 'mastered' branch, it will notify the model to do appropriate processing
                if (count == 0 && modelController.isCorrect(userInput)) {
                    imgCorrectness.setImage(new Image(getClass().getResourceAsStream("Images/tick.png")));
                    iterations++;
                    modelController.processCondition("mastered");
                    currentStreak++;
                    _animation.animateAction("Attack", false);
                    
                    // Check for achievements.
                    if (currentStreak >= 3) {
                        FileHandler.updateAchievementStatus(Achievements.STREAK3);
                    }
                    if (currentStreak >= 5) {
                        FileHandler.updateAchievementStatus(Achievements.STREAK5);
                    }
                    
                    // Check for personal best.
                    boolean newPersonalBest = modelController.checkPersonalBest(currentStreak);

                    if (newPersonalBest) {
                        lblNewPersonalBest.setVisible(true);
                    }
                    lblStreak.setText("Current Streak: " + currentStreak);
                } else if (modelController.isCorrect(userInput)) {
                    // this is the faulted branch - specifically if count > 0, then it means they've had another try
                    imgCorrectness.setImage(new Image(getClass().getResourceAsStream("Images/tick.png")));
                    _animation.animateAction("Attack", false);
                    iterations++;
                    modelController.processCondition("faulted");
                    currentStreak = 0;
                    lblNewPersonalBest.setVisible(false);
                } else if (count == 0) {
                    // this is if they've failed the word the first try
                    modelController.textToSpeech("festival -b '(voice_" + modelController.getVoice() + ")' '(SayText \"Incorrect, try once more: " + modelController.getCurrentWord() + "\")'", false);
                    modelController.textToSpeech("festival -b '(voice_" + modelController.getVoice() + ")' '(SayText \"" + modelController.getCurrentWord() + "\")'", false);
                    imgCorrectness.setImage(new Image(getClass().getResourceAsStream("Images/warning.png")));
                    imgHeart.setImage(new Image(getClass().getResourceAsStream("Images/UI_HEART_HALF.png")));
                    _animation.animateAction("Dead", true);
                    count++;
                    currentStreak = 0;
                    lblNewPersonalBest.setVisible(false);
                } else {
                    // this is if they've failed the word two times in a row
                    lblCorrectAnswer.setText("Previous answer: " + modelController.getCurrentWord());
                    imgCorrectness.setImage(new Image(getClass().getResourceAsStream("Images/cross.png")));
                    imgHeart.setImage(new Image(getClass().getResourceAsStream("Images/UI_HEART_EMPTY.png")));
                    _animation.animateAction("Dead", true);
                    iterations++;
                    modelController.processCondition("failed");
                    
                }
                // reset the iterations and show level complete message.
                if (iterations == 10 || modelController.getWordListSize() == iterations) {

                    // Play audio congratulation applause sound for level completion.
                    new MusicPlayer(".media/applause2.wav", false).playMusic();

                    iterations = 0;

                    if (modelController._wordsCorrect >= 10) {
                        FileHandler.updateAchievementStatus(Achievements.MASTER10);
                    }

                    String[] levels = FileHandler.getAllLevelNames("Wordlists/" + modelController._wordlist);

                    String finalLevel = levels[levels.length - 1];

                    String message = "You have completed a level, mastering " + modelController.numMastered() + " words!";

                    if (!modelController._level.equals(finalLevel)) {
                        Dialogs.createDialog(AlertType.INFORMATION, "Level Complete!", message + " You can now choose to progress to the next level!");
                        btnNextLevel.setDisable(false);
                    } else {
                        Dialogs.createDialog(AlertType.INFORMATION, "Level Complete!", message + " No more levels to advance.");
                        btnNextLevel.setDisable(true);
                    }
                    setSpellLabel("Level Complete");
                }
            }
        }

        // Update display on progress bar, score and streak.
        progressBar.setProgress((double)modelController.numMastered() / modelController.getWordListSize());
        lblScore.setText(modelController.numMastered() + " / " + modelController.getWordListSize());
        
        // Refocus the textfield.
        txtField.requestFocus();
    }

    
    /**
     * Go back to Main Menu.
     * @param event 
     */
    @FXML
    private void handleBtnBack(ActionEvent event) {
        // prompting user to confirm if they want to end this game.
        Optional<ButtonType> result = Dialogs.createDialog(AlertType.CONFIRMATION, "Confirmation", "Are you sure you want to leave?");

        if (result.get() == ButtonType.OK) {
            _screensController.set(VOXSPELL.MAIN_MENU);

            // Reset the volume back to higher volumes once they exit the test mode.
            VOXSPELL.setMusicVolume(0.25);

        }

    }
    
    
    /**
     * Returns selected voice.
     * @return 
     */
    public String getVoiceField(){

        String voice = voiceSelect.getValue();

        // This is to convert the user-friendly display back to the actual names for TTS.
        if (voice.equals("NZ English")) {
            voice = "akl_nz_jdt_diphone";
        } else if (voice.equals("UK English")) {
            voice = "rab_diphone";
        } else if (voice.equals("US English")) {
            voice = "kal_diphone";
        }

        return voice;
    }
    
    /**
     * Relisten to the current word.
     * @param event 
     */
    @FXML
    private void handleBtnRelisten(ActionEvent event) {
        modelController.spell();
        // Refocus the textfield after pressing button.
        txtField.requestFocus();
    }

    
    /**
     * Submit attempt through shortcut of pressing Enter key.
     * @param event 
     */
    @FXML
    private void handleEnterKeyPressed(ActionEvent event) {
        btnSubmit.fire();
    }

    
    /**
     * Set the spell label.
     * @param txt 
     */
    public void setSpellLabel(String txt) {
        spellLabel.setText(txt);
    }

    
    /**
     * Reset the GUI components and displays.
     */
    public void reset() {
        _animation = new Animations(animationGroup);
        _animation.setIdleImage();
        resetSpelling();
        iterations = 0;
        currentStreak = 0;
        lblStreak.setText("Current Streak: 0");
        lblNewPersonalBest.setVisible(false); // Set the new personal best notification invisible when game begins.
        imgHeart.setImage(new Image(getClass().getResourceAsStream("Images/UI_HEART_FULL.png")));
        progressBar.setProgress(0);
    }
    
    /**
     * Sets the score label.
     * @param score 
     */
    public void setLblScore(String score) {
        lblScore.setText(score);
    }

    
    /**
     * Get the next level button from this controller class.
     * @return 
     */
    public Button getBtnNextLevel() {
        return btnNextLevel;
    }

    
    /**
     * Reset the count for counting user's attempt of a word.
     */
    public void resetSpelling() {
        count = 0;
    }

    
    /**
     * Get the back button from this controller class.
     * @return 
     */
    public Button getBackButton() {
        return btnBack;
    }

    
    /**
     * Get the submit button from this controller class.
     * @return 
     */
    public Button getSubmitButton() {
        return btnSubmit;
    }
    
    
    /**
     * Get the relisten button from this controller class.
     * @return 
     */
    public Button getRelistenButton() {
        return btnRelisten;
    }

    
    /**
     * Set the level label display.
     * @param level 
     */
    public void setLevelLabel(String level) {
        levelLabel.setText(level);
    }

    
    /**
     * Set up the voice select combo box.
     * @param voices 
     */
    public void setupVoiceSelect(String[] voices) {

        for (int i = 0; i < voices.length; i++) {

            // This is to convert actual voice names to user-friendly displays for ComboBox.
            if (voices[i].equals("akl_nz_jdt_diphone")) {
                voices[i] = "NZ English";
            } else if (voices[i].equals("rab_diphone")) {
                voices[i] = "UK English";
            } else if (voices[i].equals("kal_diphone")) {
                voices[i] = "US English";
            }
        }

        voiceSelect.getItems().setAll(voices);
        voiceSelect.setValue(voices[0]);
    }

    /**
     * Set model class.
     * @param cmd 
     */
    @Override
    public void setModel(Command cmd) {
        modelController = (SpellingTest) cmd;
    }

    /**
     * Set parent screen.
     * @param screen 
     */
    @Override
    public void setParent(StackedScreens screen) {
        _screensController = screen;
    }

    
    /**
     * Select and set the current voice.
     * @param event 
     */
    @FXML
    private void handleVoiceSelect(ActionEvent event) {
        modelController.setVoice((String) voiceSelect.getValue());
        // Set focus on textfield.
        txtField.requestFocus();
    }

    
    /**
     * Pressing this button progresses to the next level of the wordlist.
     * @param event 
     */
    @FXML
    private void handleBtnNextLevel(ActionEvent event) {

        String[] levels = FileHandler.getAllLevelNames("Wordlists/" + modelController._wordlist);

        int currentLevelIndex = java.util.Arrays.asList(levels).indexOf(modelController._level);

        modelController.setLevel(levels[currentLevelIndex + 1]);
        modelController.execute();

        lblStreak.setText("Current Streak: 0");

        btnSubmit.setDisable(false);
        btnRelisten.setDisable(false);
        
        // Refocus the textfield.
        txtField.requestFocus();
    }

    
    /**
     * Toggles background music.
     * @param event 
     */
    @FXML
    private void handleBtnSound(ActionEvent event) {
        // This syncs toggle music with main menu button.
        MainMenuController controller = (MainMenuController) StackedScreens.getController(VOXSPELL.MAIN_MENU);       
        controller.toggleMusic();
        // Refocus the textfield.
        txtField.requestFocus();
    }
    
    
    /**
     * This returns the volume icon from the music button.
     * @return 
     */
    public ImageView getVolumeIcon() {
        return volumeIcon;
    }

}
