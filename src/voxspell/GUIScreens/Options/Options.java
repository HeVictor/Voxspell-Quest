/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell.GUIScreens.Options;

import voxspell.Command;
import voxspell.FileHandler;
import voxspell.GUIScreens.NonMainGUI;

/**
 * The model class for the Option screen.
 * @author victor
 */
public class Options implements Command{
    
    private OptionsController _GUI;
    
    // Selected current wordlist and music.
    private static String _wordlist;
    private static String _music;
    
    
    /**
     * Set current wordlist.
     * @param list 
     */
    public void setList(String list) {
        _wordlist = list;
    }
    
    
    /**
     * Set current backgruond music.
     * @param music 
     */
    public void setMusic(String music) {
    	_music = music;
    }
    
    
    /**
     * Get current wordlist.
     * @return 
     */
    public static String getCurrentList() {
        return _wordlist;
    }
    
    
    /**
     * Get current background music.
     * @return 
     */
    public static String getCurrentMusic() {
        return _music;
    }

    
    /**
     * The execute() initializes all the available music and wordlists to show in the combo boxes.
     */
    @Override
    public void execute() {
        String[] allWordlists = FileHandler.getAllFileContents("Wordlists");
        _GUI.setupWordlistSelection(allWordlists);
        
        String[] allMusic = FileHandler.getAllFileContents(".media/music");
        _GUI.setupMusicSelection(allMusic);
           
        _GUI.updateComboBoxSelection();
    }

    /**
     * Associate the model class with corresponding GUI controller class.
     * @param viewGUI 
     */
    @Override
    public void addGUI(NonMainGUI viewGUI) {
        _GUI = (OptionsController) viewGUI;
    }
    
}
