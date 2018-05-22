/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell.GUIScreens.SpellingTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import voxspell.Command;
import voxspell.Concurrency.FestivalTask;
import voxspell.FileHandler;
import voxspell.GUIScreens.NonMainGUI;
import voxspell.VOXSPELL;

/**
 * This class in the model class for the SpellingTestController which represents the GUI. Many of the code below are developed by Jacky Lo in the collaboration for the prototype.
 *
 * @author victor
 */
public class SpellingTest implements Command {

    private SpellingTestController _GUI;
    private String _fileName;
    private List<String> _words = new ArrayList<>();
    private String _currentWord = "";
    private int _iterations = 0;
    private List<Integer> _wordIndex = new ArrayList<>();
    protected String[] _allLevels;
    protected String _level;
    protected int _wordsCorrect = 0;
    protected String _wordlist;
    private String _voiceSelected;
    private int _listSize = 0;
    public static final int NUM_WORDS_TESTED = 10; // A constant of num words to be tested, refactored - Victor
    private String stringToCheck = "";
    private ExecutorService _threadPool;
    private String[] voicesArray;

    public SpellingTest(String level, String wordlist) {
        // This supposedly solves the overlapping by only have one thread at a time. Not sure if this is optimal. - Victor
        _threadPool = Executors.newFixedThreadPool(1);
        setLevel(level);
        setWordlist(wordlist);
        setupVoices();
    }

    
    /**
     * This is only to be called by FestivalTask to properly sync the vanishing of the correctness image with Festival.
     */
    public void resetCorrectnessImage() {
        _GUI.resetCorrectnessIcon();
    }

    
    /**
     * This resets the heart image to full.
     */
    public void resetHeartImage() {
        _GUI.resetHeartImg();
    }

    
    /**
     * This method retrieves the voices available on the system's Festival and sets up the local field with it.
     */
    private void setupVoices() {
        // The block below gets the available Festival voices and stores it in a drop-down menu - Victor
        String bashCmd = "ls /usr/share/festival/voices/english";

        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", bashCmd);
        Process process;
        try {
            process = builder.start();

            List<String> voices = new ArrayList<String>();

            InputStream stdout = process.getInputStream();
            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
            String voice = stdoutBuffered.readLine();
            while ((voice != null)) {
                voices.add(voice);
                voice = stdoutBuffered.readLine();
            }
            voicesArray = voices.toArray(new String[0]);

        } catch (IOException e) {
        }
    }

    
    /**
     * Initialize the test.
     */
    @Override
    public void execute() {

        // Lower the background music when doing testing so that the user can properly hear the tested words.
        VOXSPELL.setMusicVolume(0.05);

        // Set up the voice select combo box.
        _GUI.setupVoiceSelect(voicesArray);

        // Reset all the fields.
        this._voiceSelected = _GUI.getVoiceField();
        this._wordsCorrect = 0;
        this._iterations = 0;
        this._words.clear();
        this._currentWord = "";
        this._wordIndex.clear();

        // Reset the GUI displays.
        _GUI.reset();
        _GUI.setLevelLabel(_level);

        // Setup current testing list.
        _fileName = "Wordlists/" + _wordlist; // Placed wordlist inside a specialised folder
        setupTestList();

        // setting the wordList to the required spelling list
        if (_words.size() >= NUM_WORDS_TESTED) {
            this._listSize = NUM_WORDS_TESTED;
        } else {
            this._listSize = _words.size();
        }
        
        _GUI.setLblScore(0 + " / " + _listSize);

        // Disable the next level button.
        _GUI.getBtnNextLevel().setDisable(true);

        // Begin the test.
        proceedToNextWord("");
    }

    
    /**
     * Set up the words to be tested. -Jacky Lo.
     */
    public void setupTestList() {
        _words = FileHandler.getWordList(_fileName, "%" + _level);
    }

    
    /**
     * Return current iteration.
     * @return 
     */
    public int getIterations() {
        return _iterations;
    }

    
    /**
     * Return number of words for the level. - Jacky Lo
     * @return 
     */
    public int numWords() {
        return _words.size();
    }

    /*
     * merely sends a string for the process builder to read through text to speech - Jacky Lo
     */
    public void spell() {
        textToSpeech("festival -b '(voice_" + _voiceSelected + ")' '(SayText \"Please spell\")'", false); // Space out TTS calls to give a pause.
        textToSpeech("festival -b '(voice_" + _voiceSelected + ")' '(SayText \"" + stringToCheck + "\")'", false);
    }

    
    /**
     * This method only generates and returns a random word and sets current word - Victor.
     * Original method by Jacky Lo.
     * @return 
     */
    public String generateRandomWord() {
        /*
	* this method generates random words from the wordList obtained - it cannot repeat
	* previous words within the same session
         */
        stringToCheck = "";
        if (_words.size() > 0) {
            int randomWord = (int) Math.ceil(Math.random() * _words.size() - 1);
            while (_wordIndex.contains(randomWord)) { // this checks that the word has not been previously assessed
                randomWord = (int) Math.ceil(Math.random() * _words.size() - 1);
            }
            _wordIndex.add(randomWord);
            _currentWord = _words.get(randomWord);
            _GUI.resetSpelling();

            // Checks the word correctness.
            for (int i = 0; i < _currentWord.toCharArray().length; i++) {
                if (!(_currentWord.toCharArray()[i] + "").equals("'")) {
                    stringToCheck = stringToCheck + _currentWord.toCharArray()[i];
                }
            }

            // Return the message to display for the GUI.
            if (_words.size() >= NUM_WORDS_TESTED) {
                return "Spell word " + (_iterations + 1) + " of " + NUM_WORDS_TESTED + ": ";
            } else {
                return "Spell word " + (_iterations + 1) + " of " + _words.size() + ": ";
            }
        }
        return null;
    }

    
    /**
     * This method is an algorithm to generate, append text to output and then speaks the word to the user. - Victor
     * Originally by Jacky.
     * @param condition 
     */
    public void proceedToNextWord(String condition) {

        String festivalMsg = "";

        // Changed below a bit to allow syncing of Festival and text output - Victor
        if (condition.equals("mastered") || condition.equals("faulted")) {
            festivalMsg = "Correct!";
        } else if (condition.equals("failed")) {
            festivalMsg = "Incorrect!";
        }

        // this is necessary to ensure the next word is not read out after 10 iterations or word.size()
        // is met
        if (_iterations == NUM_WORDS_TESTED || _words.size() - 1 < _iterations) {
            _GUI.setSpellLabel("Level Complete");
            textToSpeech("festival -b '(voice_" + _voiceSelected + ")' '(SayText \"" + festivalMsg + "\")'", false);
        } else {

            String nextWord = generateRandomWord();

            _GUI.setSpellLabel(nextWord);
            textToSpeech("festival -b '(voice_" + _voiceSelected + ")' '(SayText \"" + festivalMsg + "\")'", true);
            spell(); // asks -tts to say the word outloud
        }
    }

    /*
     * this function builds a process which is executed within the bash shell to make a TTS call to Festival. - Original by Jacky Lo.
     */
    public void textToSpeech(String command, boolean resetHearts) {

        // these are disabling the buttons to prevent error-prone states i.e. too many VoiceWorker objects
        // being generated.
        FestivalTask festivalTask = new FestivalTask(command, _GUI.getSubmitButton(), _GUI.getRelistenButton(), this, resetHearts);

        _threadPool.submit(festivalTask);
    }

    
    /**
     * Gets the current word of the test.
     * @return 
     */
    public String getCurrentWord() {
        return _currentWord;
    }

    
    /**
     * To be called by FestivalTask to sync the vanishing of answer label after Festival calls.
     */
    public void clearAnswerLabel() {
        _GUI.clearLblAnswer();
    }

    /*
     * this function merely checks that the user answer is equal to the current word being assessed - By Jacky Lo
     */
    public boolean isCorrect(String answer) {
        if (answer.toLowerCase().trim().equals(_currentWord.toLowerCase().trim())) {
            return true;
        } else {
            return false;
        }
    }

    
    /**
     * Get current selected voice.
     * @return 
     */
    protected String getVoice() {
        return this._voiceSelected;
    }

    
    /**
     * Get the tested words size.
     * @return 
     */
    public int getWordListSize() {
        return _words.size();
    }

    /*
     * this method calls the fileHandler and appends to the file thats specified with the word - Jacky Lo
     */
    protected void writeWordToFile(String fileName) {
        // This method now writes the stats to its appropriate wordlist folder.
        FileHandler.writeToFile(fileName, _currentWord, _wordlist);
    }

    /*
      * this method calls the fileHandler and remove the word from the specified file - Jacky Lo
     */
    protected void removeWordFromFile(String fileName) {
        FileHandler.removingWord(fileName, _currentWord);
    }

    /**
     * Associate with a SpellingTest GUI glass.
     * @param viewGUI 
     */
    @Override
    public void addGUI(NonMainGUI viewGUI) {
        _GUI = (SpellingTestController) viewGUI;
    }

    
    /**
     * Sets the current voice.
     * @param voice 
     */
    protected void setVoice(String voice) {

        // This is to convert the user-friendly display back to the actual names for TTS.
        if (voice.equals("NZ English")) {
            voice = "akl_nz_jdt_diphone";
        } else if (voice.equals("UK English")) {
            voice = "rab_diphone";
        } else if (voice.equals("US English")) {
            voice = "kal_diphone";
        }    

        this._voiceSelected = voice;
    }

    
    /**
     * Sets current level.
     * @param level 
     */
    public void setLevel(String level) {
        _level = level;
    }

    
    /**
     * Sets current wordlist.
     * @param wordlist 
     */
    public void setWordlist(String wordlist) {
        _wordlist = wordlist;
    }

    
    /**
     * This returns the number of mastered words in the level.
     * @return 
     */
    public int numMastered() {
        return _wordsCorrect;
    }

    /*
     * writes to a file depending on the condition, it will append the word to mastered/faulted/
     * failed if it does not exist. - Originally by Jacky Lo.
     */
    protected void processCondition(String condition) {
        _iterations++;
        if (condition.equals("mastered")) {
            _wordsCorrect++;
            writeWordToFile(".mastered.txt");
        } else if (condition.equals("faulted")) {
            writeWordToFile(".faulted.txt");
        } else if (condition.equals("failed")) {
            writeWordToFile(".failed.txt");
        }

        // Refactored the code previously here inside proceedToNextWord, so that it may say "Correct/Incorrect" for the last word. - Victor
        proceedToNextWord(condition);

    }

    
    /**
     * This method checks the user's personal best for streaks in the stats files and returns true and updates file if there' a new
     * personal best, otherwise return false and do nothing.
     * @param currentStreak
     * @return 
     */
    public boolean checkPersonalBest(int currentStreak) {
        int streakRecord;
        try {
            streakRecord = FileHandler.getLevelBestStreaks(_wordlist, _level);

            if (currentStreak > streakRecord) {
                FileHandler.updateNewBestStreak(_wordlist, _level, currentStreak);
                return true;
            }

            return false;
        } catch (IOException ex) {
            Logger.getLogger(SpellingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
