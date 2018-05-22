package voxspell.GUIScreens.Statistics;

/**
 * This class is the model associated with the viewStats GUI. It handles the processing - sorting, and what stats to display. It then tells the viewStatsGUI to update itself
 *
 * @author jacky, with Victor adding some modifications for current app.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import voxspell.Command;
import voxspell.FileHandler;
import voxspell.GUIScreens.NonMainGUI;

public class Statistics implements Command {

    private StatisticsController _GUI; // associating itself with its view

    private String currentList;

    // The ObservableList for current word stats.
    private ObservableList<WordStats> currentWordStats = FXCollections.observableArrayList();

    // The ObservableList for current level stats. 
    private ObservableList<LevelStats> currentLevelStats = FXCollections.observableArrayList();
    private ObservableList<LevelStats> originalLevelOrdering; // This will equate to currentLevelStats' oiginal ordering.

    
    /**
     * This shows the current stats in the tables.
     */
    @Override
    public void execute() {
        _GUI.setupWordlistSelect(FileHandler.getAllFileContents("Wordlists"));
        currentList = _GUI.getSelectedList();
        showSelectedListStats();
    }

    
    /**
     * Show the current selected lists. - Original by Jacky.
     */
    private void showSelectedListStats() {
        clearStats();
        generateWordStats();
        generateScore();

        // This generates the level data.
        for (String level : FileHandler.getAllLevelNames("Wordlists/" + currentList)) {
            generateLevelStats(level);
        }
        originalLevelOrdering = this.cloneList(currentLevelStats);
        sortByAlphabetical();
    }

    
    /**
     * This sets the current list's stats to be viewed.
     * @param list 
     */
    public void setCurrentList(String list) {
        currentList = list;
        showSelectedListStats();
    }

    
    /**
     * Sorts the level stats by level order.
     */
    public void sortByLevel() {
        currentLevelStats = this.cloneList(originalLevelOrdering);
        printStats();
    }

    
    /**
     * This method sorts by the accuracy of a level. By Jacky.
     */
    public void sortByAccuracy() {
        Comparator<LevelStats> bestStreakSort = new Comparator<LevelStats>() {
            public int compare(LevelStats level1, LevelStats level2) {
                if (level1.getAccuracy() > level2.getAccuracy()) {
                    return -1;
                } else if (level1.getAccuracy() < level2.getAccuracy()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        Collections.sort(currentLevelStats, bestStreakSort);
        printStats();
    }


    /**
     * This method sorts the Level Stats table by best streaks. Original by Jacky.
     */
    public void sortByBestStreak() {
        Comparator<LevelStats> bestStreakSort = new Comparator<LevelStats>() {
            public int compare(LevelStats level1, LevelStats level2) {
                if (level1.getBestStreak() > level2.getBestStreak()) {
                    return -1;
                } else if (level1.getBestStreak() < level2.getBestStreak()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        Collections.sort(currentLevelStats, bestStreakSort);
        printStats();
    }

    
    /**
     * This method sorts by alphabetical order when the button is pressed. Original by Jacky.
     */
    public void sortByAlphabetical() {
        Comparator<WordStats> alphabeticalSort = new Comparator<WordStats>() {
            public int compare(WordStats word1, WordStats word2) {
                return word1.getWord().compareTo(word2.getWord());
            }
        };
        Collections.sort(currentWordStats, alphabeticalSort);
        printStats();
    }

    
    /**
     * This method sorts by best order when the button is pressed
     * creating an anon inner class to override the compare method for the .sort method
     * Original by Jacky.
     */
    public void sortByMaster() {
        Comparator<WordStats> bestSort = new Comparator<WordStats>() {
            public int compare(WordStats word1, WordStats word2) {
                if (word1.getMastered() > word2.getMastered()) {
                    return -1;
                } else if (word1.getMastered() < word2.getMastered()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        Collections.sort(currentWordStats, bestSort);
        printStats();
    }

    
    /**
     * This method sorts by the most failed statsFileLine when the button is pressed
     * Original by Jacky.
     */
    public void sortByFailed() {
        Comparator<WordStats> worstSort = new Comparator<WordStats>() {
            public int compare(WordStats word1, WordStats word2) {
                if (word1.getFailed() > word2.getFailed()) {
                    return -1;
                } else if (word1.getFailed() < word2.getFailed()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        Collections.sort(currentWordStats, worstSort);
        printStats();
    }

    
    /**
     * Clears the stats.
     */
    public void clearStats() {
        currentWordStats.clear();
        currentLevelStats.clear();
        printStats();
    }

    
    /**
     * this method updates the GUI to show stats
     * Original by Jacky.
     */
    public void printStats() {
        
        if (currentWordStats.isEmpty()) {
            _GUI.notifyNoAttempts();
        } else {

            _GUI.setWordStats(currentWordStats);
            _GUI.setLevelStats(currentLevelStats);

        }
    }

    
    /**
     * this method generates the scores associated with each word 
     * Original by Jacky.
     */
    private void generateScore() {
        String[] fileNames = {".mastered.txt", ".faulted.txt", ".failed.txt"};
        List<String> statsFileLine = new ArrayList<String>();
        statsFileLine = FileHandler.getWordList(FileHandler.LISTSTATSFOLDER + "/" + currentList + "/" + ".stats.txt", null);
        for (String fileStats : statsFileLine) {
            for (int i = 0; i < fileNames.length; i++) {
                for (WordStats aWordStat : currentWordStats) {
                    // the statFileNames indexes are stored mastered, faulted and failed 0, 1, 2 respectively
                    if ((aWordStat.getWord() + ": " + fileNames[i]).equals(fileStats)) {
                        // if the string matches the output of for example echoes: .mastered.txt then
                        // we increment its score location by 1;
                        switch (i) {
                            case 0:
                                aWordStat.setMastered(aWordStat.getMastered() + 1);
                                break;
                            case 1:
                                aWordStat.setFaulted(aWordStat.getFaulted() + 1);
                                break;
                            case 2:
                                aWordStat.setFailed(aWordStat.getFailed() + 1);
                                break;
                        }
                    }
                }
            }
        }
    }

    
    /**
     * This method initializes the currentWordStats with all the words of the current list and default 0 stats.
     */
    private void generateWordStats() {
        String[] statFileNames = {".failed.txt", ".faulted.txt", ".mastered.txt"};
        List<String> words = new ArrayList<String>();
        for (int i = 0; i < statFileNames.length; i++) {
            words = FileHandler.getWordList(FileHandler.LISTSTATSFOLDER + "/" + currentList + "/" + statFileNames[i], null);
            for (String word : words) {

                WordStats newStats = new WordStats(word, 0, 0, 0);

                if (!currentWordStats.contains(newStats)) {
                    currentWordStats.add(newStats);  
                }

            }
        }
    }

    
    /**
     * Generates the level stats with the current list.
     * Original by Jacky Lo.
     * @param level 
     */
    private void generateLevelStats(String level) {
        List<String> levelWords = new ArrayList<String>();
        List<String> wordsStats = new ArrayList<String>();
        int[] failedSuccess = {0, 0};
        levelWords = FileHandler.getWordList("Wordlists/" + currentList, "%" + level);
        wordsStats = FileHandler.getWordList(FileHandler.LISTSTATSFOLDER + "/" + currentList + "/" + ".stats.txt", null);
        // the following code gets the statsFileLine from the stats file, and then compares to the main spelling text
        // to determine what level it is from.
        for (String e : levelWords) {
            for (String d : wordsStats) {
                if ((e + ": .mastered.txt").equals(d)) {
                    failedSuccess[1]++;
                    failedSuccess[0]++;
                } else if ((e + ": .faulted.txt").equals(d) || (e + ": .failed.txt").equals(d)) {
                    failedSuccess[0]++;
                }
            }
        }

        LevelStats newLevelStats;

        if (failedSuccess[0] != 0) {

            try {
                double accuracy = (double) failedSuccess[1] / failedSuccess[0] * 100;
                double roundedAccuracy = Math.round(accuracy * 100.0) / 100.0; // From StackOverflow uer SiB
                newLevelStats = new LevelStats(level, roundedAccuracy + "", FileHandler.getLevelBestStreaks(currentList, level));
                currentLevelStats.add(newLevelStats);
            } catch (IOException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            newLevelStats = new LevelStats(level, "", 0);
        }

    }

    /**
     * Associate with GUI controller class.
     * @param viewGUI 
     */
    @Override
    public void addGUI(NonMainGUI viewGUI) {
        _GUI = (StatisticsController) viewGUI;
    }

    
    /**
     * This method makes a deep clone of a LevelStats list. From StackOverflow user cdmckay.
     * @param original
     * @return 
     */
    private ObservableList<LevelStats> cloneList(ObservableList<LevelStats> original) {
        ObservableList<LevelStats> clonedList = FXCollections.observableArrayList();
        for (LevelStats aStat : original) {
            clonedList.add(aStat);
        }
        return clonedList;
    }

    /**
     *
     * An inner class that contains the statistics for a single level.
     *
     * @author victor
     */
    public static class LevelStats {

        private final SimpleStringProperty level;
        private final SimpleStringProperty accuracy;
        private final SimpleIntegerProperty bestStreak;

        private LevelStats(String level, String accuracy, int bestStreak) {
            this.level = new SimpleStringProperty(level);
            this.accuracy = new SimpleStringProperty(accuracy);
            this.bestStreak = new SimpleIntegerProperty(bestStreak);
        }

        // A constructor that copies a LevelStats object's fields to a new LevelStat copy object.
        private LevelStats(LevelStats original) {
            this.level = new SimpleStringProperty(original.getLevel());
            this.accuracy = new SimpleStringProperty(original.getAccuracy() + "");
            this.bestStreak = new SimpleIntegerProperty(original.getBestStreak());
        }

        // Gets level.
        public String getLevel() {
            return level.get();
        }

        // Sets level.
        public void setLevel(String level) {
            this.level.set(level);
        }

        // Gets accuracy and converts it to String.
        public Double getAccuracy() {
            String str = accuracy.get();
            return Double.parseDouble(str.substring(0, str.length()));
        }

        // Sets accuracy.
        public void setAccuracy(Double accuracy) {
            String strAccuracy = accuracy + "";
            this.accuracy.set(strAccuracy);
        }

        // Gets bbest streak.
        public int getBestStreak() {
            return bestStreak.get();
        }

        // Sets best streak.
        public void setBestStreak(int bestStreak) {
            this.bestStreak.set(bestStreak);
        }

    }

    /**
     *
     * An inner class that contains the statistics for a single word.
     *
     * @author victor
     */
    public static class WordStats {

        private final SimpleStringProperty _word;
        private final SimpleIntegerProperty _numMastered;
        private final SimpleIntegerProperty _numFaulted;
        private final SimpleIntegerProperty _numFailed;

        private WordStats(String word, int mastered, int faulted, int failed) {
            _word = new SimpleStringProperty(word);
            _numMastered = new SimpleIntegerProperty(mastered);
            _numFaulted = new SimpleIntegerProperty(faulted);
            _numFailed = new SimpleIntegerProperty(failed);
        }

        // Gets word.
        public String getWord() {
            return _word.get();
        }

        // Sets word.
        public void setWord(String word) {
            _word.set(word);
        }

        // Get mastered amount.
        public int getMastered() {
            return _numMastered.get();
        }

        // Set mastered amount.
        public void setMastered(int mastered) {
            _numMastered.set(mastered);
        }

        // Get faulted amount.
        public int getFaulted() {
            return _numFaulted.get();
        }

        // Set faulted amount.
        public void setFaulted(int faulted) {
            _numFaulted.set(faulted);
        }

        // Get failed amount.
        public int getFailed() {
            return _numFailed.get();
        }

        // Set failed amount.
        public void setFailed(int failed) {
            _numFailed.set(failed);
        }

        /**
         * From StackOverflow users Spence and Kim. Compares equivalence between two WordStats objects.
         * @param obj
         * @return 
         */
        @Override
        public boolean equals(Object obj) {

            if (obj == null) {
                return false;
            }

            if (!WordStats.class.isAssignableFrom(obj.getClass())) {
                return false;
            }

            final WordStats other = (WordStats) obj;

            if (this.getWord().equals(other.getWord())) {
                return true;
            }
            return false;
        }

        /**
         * Unique hashcode generator.
         * @return 
         */
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(this._word);
            return hash;
        }
    }
}
