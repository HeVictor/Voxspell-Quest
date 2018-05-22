/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell.GUIScreens.Achievements;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import voxspell.Command;
import voxspell.FileHandler;
import voxspell.GUIScreens.NonMainGUI;

/**
 *The model class for the Achievements screen.
 * 
 * @author victor
 */
public class Achievements implements Command{
    
     private AchievementsController _GUI;
     
     // The four available achievements names.
     public static final String UPLOADED = "uploaded";
     public static final String STREAK3 = "3streak";
     public static final String STREAK5 = "5streak";
     public static final String MASTER10 = "10master";
     
     private static final String[] ACHIEVEMENTS = {UPLOADED, STREAK3, STREAK5, MASTER10};
     
     private static final String ACHIEVEMENTSFILE = ".achievements";
     

    
     /**
      * The execute() here enables reward buttons according to their status within the .achievements file.
      */
    @Override
    public void execute() {
        
        boolean[] allStatus = new boolean[ACHIEVEMENTS.length];
        int counter = 0;
        
        for (String anAchievement : ACHIEVEMENTS) {
            try {
                String strStatus = FileHandler.getAchievementStatus(anAchievement);
                boolean status = Boolean.parseBoolean(strStatus);
                allStatus[counter] = status;
                counter++;
            } catch (IOException ex) {
                Logger.getLogger(Achievements.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        _GUI.setButtons(allStatus);
        
    }

    /**
     * Associate with the corresponding GUI.
     * @param viewGUI 
     */
    @Override
    public void addGUI(NonMainGUI viewGUI) {
        _GUI = (AchievementsController) viewGUI;
    }
    
    
    /**
     * This method returns all of the achievement types
     * @return 
     */
    public static String[] getAchievements() {
        return ACHIEVEMENTS;
    }
    
    
    /**
     * This method returns the achivements file location.
     * @return 
     */
    public static String getAchivementFile() {
        return ACHIEVEMENTSFILE;
    }
    
}
