package voxspell.GUIScreens.Statistics;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import voxspell.Command;
import voxspell.GUIScreens.ControlledScreen;
import voxspell.Dialogs;
import voxspell.FileHandler;
import voxspell.GUIScreens.NonMainGUI;
import voxspell.GUIScreens.Options.Options;
import voxspell.GUIScreens.StackedScreens;
import voxspell.GUIScreens.Statistics.Statistics.LevelStats;
import voxspell.GUIScreens.Statistics.Statistics.WordStats;
import voxspell.VOXSPELL;

/**
 * FXML Controller class
 * GUI screen class for Statistics.
 * @author victor
 */
public class StatisticsController implements Initializable, NonMainGUI, ControlledScreen {

    private Statistics modelController;
    private StackedScreens _screensController;
    
    @FXML
    private ComboBox<String> wordlistSelect;
    @FXML
    private TableView<WordStats> wordStatsTable;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnAlphabetical;
    @FXML
    private Button btnBest;
    @FXML
    private Button btnWorst;
    @FXML
    private TableColumn<WordStats,String> wordColumn;
    @FXML
    private TableColumn<WordStats,Integer> masteredColumn;
    @FXML
    private TableColumn<WordStats,Integer> faultedColumn;
    @FXML
    private TableColumn<WordStats,Integer> failedColumn;
    @FXML
    private Button btnReset;
    @FXML
    private TableView<LevelStats> levelStatsTable;
    @FXML
    private TableColumn<LevelStats, String> levelColumn;
    @FXML
    private TableColumn<LevelStats, String> accuracyColumn;
    @FXML
    private TableColumn<LevelStats, Integer> bestStreakColumn;
    @FXML
    private Button btnAccuracy;
    @FXML
    private Button btnBestStreak;
    @FXML
    private Button btnLevel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Matches up each table column with the respective property from WordStats class, for the wordStatsTable.
        wordColumn.setCellValueFactory(new PropertyValueFactory<WordStats, String>("word"));
        masteredColumn.setCellValueFactory(new PropertyValueFactory<WordStats, Integer>("mastered"));
        faultedColumn.setCellValueFactory(new PropertyValueFactory<WordStats, Integer>("faulted"));
        failedColumn.setCellValueFactory(new PropertyValueFactory<WordStats, Integer>("failed"));
        
        // Matches up each table column with the respective property from the LevelStats class, for the levelStatsTable.
        levelColumn.setCellValueFactory(new PropertyValueFactory<LevelStats, String>("level"));
        accuracyColumn.setCellValueFactory(new PropertyValueFactory<LevelStats, String>("accuracy"));
        bestStreakColumn.setCellValueFactory(new PropertyValueFactory<LevelStats, Integer>("bestStreak"));
    }

    /**
     * Associate model class.
     * @param cmd 
     */
    @Override
    public void setModel(Command cmd) {
        modelController = (Statistics) cmd;
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
     * Sets to display stats for selected wordlist.
     * @param event 
     */
    @FXML
    private void handleWordlistSelect(ActionEvent event) {
        modelController.setCurrentList(wordlistSelect.getSelectionModel().getSelectedItem());
    }

    
    /**
     * Sort alphabetically.
     * @param event 
     */
    @FXML
    private void handleBtnAlphabetical(ActionEvent event) {
        modelController.sortByAlphabetical();
    }

    
    /**
     * Sort by best (mastered).
     * @param event 
     */
    @FXML
    private void handleBtnBest(ActionEvent event) {
        modelController.sortByMaster();
    }

    
    /**
     * Sort by worst (failed).
     * @param event 
     */
    @FXML
    private void handleBtnWorst(ActionEvent event) {
        modelController.sortByFailed();
    }

    
    /**
     * Set up the wordlist select combo box.
     * @param lists 
     */
    public void setupWordlistSelect(String[] lists) {
        wordlistSelect.getItems().setAll(lists);
        
        String currentList = Options.getCurrentList();
        
        wordlistSelect.setValue(currentList);
    }

    
    /**
     * Set up the word statistics in table.
     * @param stats 
     */
    public void setWordStats(ObservableList<WordStats> stats) {
        wordStatsTable.setItems(stats);
    }
    
    
    /**
     * Set up the level statistics in table.
     * @param stats 
     */
    public void setLevelStats(ObservableList<LevelStats> stats) {
        levelStatsTable.setItems(stats);
    } 
    
    
    /**
     * Get currently selected list.
     * @return 
     */
    public String getSelectedList() {
        return wordlistSelect.getSelectionModel().getSelectedItem();
    }

    
    /**
     * This method notifies the user from table text that they have not attempted any words yet.
     */
    public void notifyNoAttempts() {
        String message = "You have yet to attempt any words!";
        wordStatsTable.setPlaceholder(new Label(message));
        levelStatsTable.setPlaceholder(new Label(message));
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
     * Reset the stats.
     * @param event 
     */
    @FXML
    private void handleBtnReset(ActionEvent event) {
        Optional<ButtonType> result = Dialogs.createDialog(Alert.AlertType.CONFIRMATION, "Clearing Stats", "Are you sure you want to clear and reset your stats?");
        if (result.get() == ButtonType.OK) {
            FileHandler.clearStats();
            modelController.clearStats();
        }

    }

    
    /**
     * Sort by accuracy.
     * @param event 
     */
    @FXML
    private void handleBtnAccuracy(ActionEvent event) {
        modelController.sortByAccuracy();
    }

    
    /**
     * Sort by best streak. 
     * @param event 
     */
    @FXML
    private void handleBtnBestStreak(ActionEvent event) {
        modelController.sortByBestStreak();
    }

    
    /**
     * Sort by level order.
     * @param event 
     */
    @FXML
    private void handleBtnLevel(ActionEvent event) {
        modelController.sortByLevel();
    }
}
