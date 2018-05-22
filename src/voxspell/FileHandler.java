package voxspell;

import voxspell.GUIScreens.Achievements.Achievements;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;

/**
 *
 * This class handles all file-related stuff. It removes words and appends words to the specified files. If needed, it will also clearstats().
 *
 * Victor modified it so that it only has static methods now, since it doesn't really make sense to create a new FileHandler object every time with no customizable fields.
 *
 * @author jacky, with Victor adding more file-methods for the current Voxspell.
 *
 */
public class FileHandler {

    private static final String[] STATSFILES = {".mastered.txt", ".stats.txt", ".failed.txt", ".faulted.txt"}; // This is final as we don't want to modify the list of stats files.
    // The name of the file which contains all of the wordlist stats folders
    public static final String LISTSTATSFOLDER = ".wordListStats";

    /**
     * This gets all files' names within a directory. No directories are included in the returned list.
     * 
     * @param fileName
     * @return 
     */
    public static String[] getAllFileContents(String fileName) {
        File wordlistFolder = new File(fileName);
        File[] allWordlists = wordlistFolder.listFiles();

        if (allWordlists == null) {
            System.out.println(wordlistFolder.getAbsolutePath());
            return new String[0]; // This is so that no NullPointerException gets thrown when nothing can be found.
        }

        ArrayList<String> wordlistNames = new ArrayList<String>();

        // Add all the contents of the directory that is actually a file.
        for (File allWordlist : allWordlists) {
            if (allWordlist.isFile()) {
                wordlistNames.add(allWordlist.getName());
            }
        }
        return wordlistNames.toArray(new String[0]);
    }

    
    /**
     * This creates default achivements files, with no achievements unlocked yet.
     * Nothing happens if file exists already.
     * @throws IOException 
     */
    public static void createAchievementsFiles() throws IOException {
        String[] achievements = Achievements.getAchievements();

        File achievementsFile = new File(Achievements.getAchivementFile());
        boolean fileNotExists = achievementsFile.createNewFile();

        if (fileNotExists) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Achievements.getAchivementFile()));
            for (String achievement : achievements) {
                appendingFile(writer, achievement + " false");
            }
            writer.close();
        }
    }

    
    /**
     * This method creates default 0 streak files for all levels in a wordlist, for all lists.
     * Nothing will happen if the files already exists.
     * @throws IOException 
     */
    public static void createBestStreakFiles() throws IOException {
        String[] allLists = getAllFileContents("Wordlists");
        for (String aList : allLists) {

            File streakFile = new File(LISTSTATSFOLDER + "/" + aList + "/BestStreaks");
            boolean fileNotExists = streakFile.createNewFile();

            if (fileNotExists) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(FileHandler.LISTSTATSFOLDER + "/" + aList + "/BestStreaks", true));

                for (String levelName : getAllLevelNames("Wordlists/" + aList)) {
                    appendingFile(writer, levelName + "@0"); // An "@" is used here because custome level names usually don't have this symbol, and we can use this to split string to retrieve best streak later.
                }

                writer.close();
            }

        }

    }

    
    /**
     * This method initializes all statistics files for all wordlists. 
     */
    public static void initializeAllListStatsFolders() {
        String[] allLists = getAllFileContents("Wordlists");
        for (String aList : allLists) {
            createNewWordlistStatsFolder(aList);
        }
    }

    
    /**
     * This method create a folder to contain the stats files for a wordlist.
     * @param wordlist 
     */
    public static void createNewWordlistStatsFolder(String wordlist) {
        File statsFolder = new File(LISTSTATSFOLDER + "/", wordlist);
        statsFolder.mkdir();
    }

     
    /**
     * this method writes to the specified fileName - it checks if the word exists yet or not
     * if it does, it will then add it. it will also add the word to stats along with whether
     * it was faulted, failed, or mastered.
     * Originally by Jackly Lo, made slight modifications by Victor to cater to the current directory structure.
     * 
     * @param fileName
     * @param currentWord
     * @param currentList 
     */
    public static void writeToFile(String fileName, String currentWord, String currentList) {
        try {
            if (wordNotExists(FileHandler.LISTSTATSFOLDER + "/" + currentList + "/" + fileName, currentWord)) {
                // true set to enable appending to file writer
                BufferedWriter typeOfSuccess = new BufferedWriter(new FileWriter(FileHandler.LISTSTATSFOLDER + "/" + currentList + "/" + fileName, true));
                appendingFile(typeOfSuccess, currentWord);
                typeOfSuccess.close();
            }
            BufferedWriter stats = new BufferedWriter(new FileWriter(FileHandler.LISTSTATSFOLDER + "/" + currentList + "/" + ".stats.txt", true));
            appendingFile(stats, currentWord + ": " + fileName);
            stats.close();
        } catch (IOException e1) {
        }
    }

     
    /**
     * this method checks if a word exists in the specified filename
     * By Jacky Lo
     * @param fileName
     * @param currentWord
     * @return 
     */
    private static boolean wordNotExists(String fileName, String currentWord) {
        try {
            String word = null;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            while ((word = bufferedReader.readLine()) != null) {
                if (word.equals(currentWord)) {
                    bufferedReader.close();
                    return false;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
        }
        return true;
    }

    /*
     * This method appends to an existing file and adds the string to a new line. By Jacky Lo.
     */
    private static void appendingFile(BufferedWriter fileName, String toAdd) {
        try {
            fileName.write(toAdd);
            fileName.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*     
    * this method finds a string in a file and removes it from the file - removing the blank space
    * too. it uses a temporary file to do this. By Jacky Lo.
     */
    public static void removingWord(String fileName, String toRemove) {

        try {
            File tempFile = new File(".TempWordlist.txt");
            File inputFile = new File(fileName);

            BufferedWriter fileToWrite = new BufferedWriter(new FileWriter(tempFile, true));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            String word = "";
            while ((word = bufferedReader.readLine()) != null) {
                if (!word.equals(toRemove)) {
                    appendingFile(fileToWrite, word);
                }
            }
            bufferedReader.close();
            fileToWrite.close();
            tempFile.renameTo(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Resets all achievements by recreating the files. 
     */
    public static void clearAchievements() {
        try {
            Files.delete(Paths.get(Achievements.getAchivementFile()));
            createAchievementsFiles(); // Recreating Achivements files.
            Files.move(new File(".media/music/The Adventure Begins").toPath(), new File(".media/music/locked/The Adventure Begins").toPath(), REPLACE_EXISTING);
            Files.move(new File(".media/music/In The Name Of All").toPath(), new File(".media/music/locked/In The Name Of All").toPath(), REPLACE_EXISTING);
        } catch (IOException ex) {
            // Nothing needs to happen here. If exception is thrown it just means the music haven't been unlocked yet anyway, throwing a FileNotFoundException.
        }
    }

    /*
     * reset all the statistics by recreating the files. - By Jacky Lo.
     */
    public static void clearStats() {
        try {
            for (String aList : getAllFileContents("Wordlists")) {

                String filePath = LISTSTATSFOLDER + "/" + aList + "/";

                for (int i = 0; i < STATSFILES.length; i++) {
                    BufferedWriter out = new BufferedWriter(new FileWriter(filePath + STATSFILES[i]));
                    out.close();
                }
                Files.delete(Paths.get(filePath + "BestStreaks")); // Deleting BestStreak files.
            }
            createBestStreakFiles(); // Recreating BestStreak files.
        } catch (IOException e) {
        }
    }

    
     
    /**
     * A rewritten getWordList method to take level as a parameter to process new wordlist format. Original, single
     * fileName string method functionality are retained by all invokers of such methods use null for the level input,
     * to indicate not reading from the NZCER wordlist. - Victor (method originally written by Jacky)
     * If the level is null it just reads every line as normal.
     * 
     * @param fileName
     * @param level
     * @return 
     */
    public static List<String> getWordList(String fileName, String level) {
        /*
         * retrieve the word list associated with a file.
         */
        String word = null;
        List<String> words = new ArrayList<String>();
        FileReader fileReader;

        ArrayList<String> invalidWords = new ArrayList<String>();
        try {

            fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            if (level == null) {
                // Read the entire file if level is null.
                while ((word = bufferedReader.readLine()) != null) {
                    words.add(word);
                    if (!checkWordEligibility(word)) {
                        invalidWords.add(word);
                    }
                }
            } else {
                while (!(word = bufferedReader.readLine()).equals(level)) {
                }
                word = bufferedReader.readLine();
                while ((word != null) && word.split(" ")[0].charAt(0) != '%') {
                    words.add(word);
                    if (!checkWordEligibility(word)) {
                        invalidWords.add(word);
                    }
                    word = bufferedReader.readLine();
                }
            }

            // Show an error dialog shwoing all words with unallowed special characters that are detected, if there are any. 
            if (invalidWords.size() > 0) {

                String errMsg = "";

                int count = 0;
                for (String invalidWord : invalidWords) {

                    if (count == invalidWords.size() - 1) {
                        errMsg = errMsg + invalidWord + " ";
                    } else {
                        errMsg = errMsg + invalidWord + ", ";
                    }

                    count++;

                }
                errMsg = errMsg + "contained special characters! Only alphabets and apostrophes allowed!";

                Dialogs.createDialog(Alert.AlertType.ERROR, "Wordlist Error!", errMsg);
            }

            bufferedReader.close();
        } catch (Exception e) {
        }
        return words;
    }

    
    /**
     * An error checking method to check if a word from a spelling list contains unallowed characters. 
     * @param word
     * @return 
     */
    private static boolean checkWordEligibility(String word) {
        
        char[] chars = word.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isLetter(chars[i]) && (chars[i] != '\'')) {
                
                // Assume that if the string begins with a % it is a level name and is acceptable.
                if (i != 0 && chars[i] == '%') {
                    return false;
                }
                
            }
        }

        return true;
    }

    
    /**
     * This method gets all the level names preceeded by a "%" for a given wordlist file. 
     * Users will be notified to follow this strict rule when creating their own lists.
     * 
     * @param wordlist
     * @return 
     */
    public static String[] getAllLevelNames(String wordlist) {

        List<String> levels = new ArrayList<String>();
        FileReader fileReader;
        try {

            fileReader = new FileReader(wordlist);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("%")) {
                    levels.add(line.substring(1)); // Removes the %, from marcog StackOverflow user.
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
        }

        String[] levelsArray = levels.toArray(new String[0]);
        return levelsArray;
    }

    
    /**
     * This method updates an achievement in the achievements file to show true instead of false, if it is false. 
     * @param matchingAchievement 
     */
    public static void updateAchievementStatus(String matchingAchievement) {
        try {
            if (FileHandler.getAchievementStatus(matchingAchievement).equals("false")) {

                Dialogs.createDialog(Alert.AlertType.INFORMATION, "Achievement Unlocked!", "Congratulations! You have unlocked a new achievement!");

                try {
                    // Gets all the file contents
                    ArrayList<String> fileContent = new ArrayList<String>();
                    BufferedReader reader = new BufferedReader(new FileReader(Achievements.getAchivementFile()));
                    String line;
                    while ((line = reader.readLine()) != null) {

                        String[] achievementAndStatus = line.split(" ");
                        String achievement = achievementAndStatus[0];

                        // Modifies the relevant BestStreak line to new record.
                        if (achievement.equals(matchingAchievement)) {
                            line = achievementAndStatus[0] + " true";
                        }

                        fileContent.add(line);
                    }

                    reader.close();

                    // Clear the file
                    BufferedWriter writer = new BufferedWriter(new FileWriter(Achievements.getAchivementFile()));
                    writer.close();

                    // Write to file with the original content plus the new best record.
                    writer = new BufferedWriter(new FileWriter(Achievements.getAchivementFile()));
                    for (String fileLine : fileContent) {
                        appendingFile(writer, fileLine);
                    }
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
    /**
     * This method updates a level's best streak to a new record by reading a file and overwriting it with new content.
     * @param wordlist
     * @param level
     * @param newStreak 
     */
    public static void updateNewBestStreak(String wordlist, String level, int newStreak) {

        try {

            // Gets all the file contents
            ArrayList<String> fileContent = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(LISTSTATSFOLDER + "/" + wordlist + "/BestStreaks"));
            String line;
            while ((line = reader.readLine()) != null) {

                String[] levelAndStreak = line.split("@");
                String readLevel = levelAndStreak[0];

                // Modifies the relevant BestStreak line to new record.
                if (readLevel.equals(level)) {
                    line = levelAndStreak[0] + "@" + newStreak;
                }

                fileContent.add(line);
            }

            reader.close();

            // Clear the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(LISTSTATSFOLDER + "/" + wordlist + "/BestStreaks"));
            writer.close();

            // Write to file with the original content plus the new best record.
            writer = new BufferedWriter(new FileWriter(LISTSTATSFOLDER + "/" + wordlist + "/BestStreaks"));
            for (String fileLine : fileContent) {
                appendingFile(writer, fileLine);
            }
            writer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
    /**
     * This gets a status matching a pattern, which is in the form of "PATTERN STATUS" for a line, which is found in a file of this format.
     * @param filename
     * @param patternToMatch
     * @param splitter
     * @return
     * @throws IOException 
     */
    public static String getStatusFromPattern(String filename, String patternToMatch, String splitter) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {

            String[] patternAndStatus = line.split(splitter);
            String pattern = patternAndStatus[0];

            if (pattern.equals(patternToMatch)) {
            	reader.close();
                return patternAndStatus[1];
            }
        }

        reader.close();
        return ""; // Should never reach here theoretically.
    }

    
    /**
     * This method reads the BestStreak file for the specified level of a wordlist and gets the best streak.
     * @param wordlist
     * @param level
     * @return
     * @throws IOException 
     */
    public static int getLevelBestStreaks(String wordlist, String level) throws IOException {

        String strStreak = getStatusFromPattern(LISTSTATSFOLDER + "/" + wordlist + "/BestStreaks", level, "@");

        return Integer.parseInt(strStreak);
    }

    
    /**
     * Gets the true or false status of an achievement from achievement file.
     * @param achievement
     * @return
     * @throws IOException 
     */
    public static String getAchievementStatus(String achievement) throws IOException {
        return getStatusFromPattern(Achievements.getAchivementFile(), achievement, " ");
    }

    
    /**
     * Retrieved from https://crunchify.com/java-file-copy-example-simple-way-to-copy-file-in-java/
     * This method copies a file content to another
     * @param f1
     * @param f2 
     */
    public static void copyFile(File f1, File f2) {

        InputStream in = null;
        OutputStream out = null;

        try {

            in = new FileInputStream(f1);
            out = new FileOutputStream(f2);

            byte[] buffer = new byte[1024];

            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();
        } catch (IOException e) {
        }
    }
}
