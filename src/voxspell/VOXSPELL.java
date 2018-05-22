/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell;

import voxspell.Players.MusicPlayer;
import voxspell.GUIScreens.NonMainGUI;
import voxspell.GUIScreens.StackedScreens;
import voxspell.GUIScreens.MainMenu.MainMenuController;
import voxspell.GUIScreens.Options.Options;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import voxspell.GUIScreens.Options.OptionsController;

/**
 * From Angela Caicedo's blog, at https://blogs.oracle.com/acaicedo/entry/managing_multiple_screens_in_javafx1

 The main method here starts and shows the GUI as usual, but now it also loads all the required screen in advance into a StackedScreens object.
 *
 * @author victor
 */
public class VOXSPELL extends Application {
    
    // The collection of final String constants below are the various app screens' associated name and their fxml files.

    public static final String MAIN_MENU = "MainMenu";
    public static final String MAIN_MENU_FXML = "/voxspell/GUIScreens/MainMenu/MainMenu.fxml";

    public static final String SPELLING_TEST = "SpellingTest";
    public static final String SPELLING_TEST_FXML = "/voxspell/GUIScreens/SpellingTest/SpellingTest.fxml";

    public static final String ACHIEVEMENTS = "Achievements";
    public static final String ACHIEVEMENTS_FXML = "/voxspell/GUIScreens/Achievements/Achievements.fxml";
    
    public static final String STATISTICS = "Statistics";
    public static final String STATISTICS_FXML = "/voxspell/GUIScreens/Statistics/Statistics.fxml";

    public static final String OPTIONS = "Options";
    public static final String OPTIONS_FXML = "/voxspell/GUIScreens/Options/Options.fxml";
    
    // This player is the single player available to use for the app.
    private static MusicPlayer player;

    
    /**
     * This method starts the app and carries any preliminary works required.
     * 
     * @param primaryStage
     * @throws Exception 
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // Sets window title.
        primaryStage.setTitle("Voxspell Quest");

        // Loads all the screens of the app to a StackedScreens variable, and set the top of the screen to be Main Menu screen.
        StackedScreens mainContainer = new StackedScreens();
        mainContainer.load(MAIN_MENU,MAIN_MENU_FXML);
        mainContainer.load(SPELLING_TEST,SPELLING_TEST_FXML);
        mainContainer.load(ACHIEVEMENTS,ACHIEVEMENTS_FXML);
        mainContainer.load(STATISTICS, STATISTICS_FXML);
        mainContainer.load(OPTIONS,OPTIONS_FXML);
        mainContainer.set(MAIN_MENU);

        // Add the StackedScreens to a group and show the top screen.
        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // This initializes the current wordlist selection through Options functionality.
        MainMenuController menuController = (MainMenuController)StackedScreens.getController(MAIN_MENU);
        NonMainGUI controller = (NonMainGUI) StackedScreens.getController(VOXSPELL.OPTIONS);
        menuController.cmdExecute(new Options(),controller);
        
        // Creates new files for all wordlists in a hidden directory if they are not there already.
        FileHandler.initializeAllListStatsFolders();
        
        // Creates new BestStreak file for all levels of all wordlists if they are not there already.
        FileHandler.createBestStreakFiles();
        
        // Creates new Achievements file if they are not there already.
        FileHandler.createAchievementsFiles();
        
        // Loads the Minecraft font for the Main Menu.
        Font.loadFont(getClass().getResourceAsStream("/voxspell/GUIScreens/MainMenu/Minecraft.ttf"), 30);
        
        // Plays default background track
        player = new MusicPlayer(".media/music/Adventure",true);
        player.playMusic();
        
        // Initialize the Options ComboBox selections to reflect the default wordlist and music.
        OptionsController opController = (OptionsController) controller;
        opController.defaultMusicSelection();
        opController.defaultWordlistSelection();
        menuController.setCurrentListLabel();
    }
    
    
    /**
     *  This method toggles the music player.
     */
    public static void toggleMusic() {
    	player.toggle();
    }
    
    
    /**
     * This method sets the audio/music to be played on the music player.
     * @param newTrack 
     */
    public static void setMusic(String newTrack) {
    	player.setAndPlayMusic(newTrack);
    }
    
    
    /**
     * This method sets the player volume.
     * @param volume 
     */
    public static void setMusicVolume(double volume) {
        player.setVolume(volume);
    }

    /**
     * Main method.
     * @param args 
     */
    public static void main(String[] args) {
        launch(args);
    }

}
