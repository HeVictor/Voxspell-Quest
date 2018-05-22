/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell.Concurrency;

import voxspell.GUIScreens.SpellingTest.SpellingTest;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import static voxspell.GUIScreens.SpellingTest.SpellingTest.NUM_WORDS_TESTED;

/**
 * A SwingWorker equivalent concurrency class to enable Festival to not freeze up Voxspell.
 *
 * @author victor
 */
public class FestivalTask extends Task<Void> {

    private String _command;
    private Button _btn1;
    private Button _btn2;
    private SpellingTest _test;
    private boolean _resetHearts;

    public FestivalTask(String command, Button btn1, Button btn2, SpellingTest test, boolean resetHearts) {
        _command = command;
        _btn1 = btn1;
        _btn2 = btn2;
        _test = test;
        _resetHearts = resetHearts;
    }

    /**
     * This method processes the Festival call in a non-blocking way and updates the GUI appropriately.
     * @return
     * @throws Exception 
     */
    @Override
    protected Void call() throws Exception {
        
        // Disables relisten and submmit when Festival is talking to prevent error states.
        _btn1.setDisable(true);
        _btn2.setDisable(true);

        // Call Festival and place it in background.
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", _command);
        try {
            Process process = pb.start();
            process.waitFor(); // waiting for the process to finish
        } catch (Exception e) {
        }
        
        // This is to handle the case when it is the final word of a level being tested, and we want to disable the submit button and relisten button after that.
        if (_test.getIterations() == NUM_WORDS_TESTED || _test.getWordListSize() - 1 < _test.getIterations()) {
            return null;
        } else {
            // Else we enable the relisten and submit for the user for the next word.
            _btn1.setDisable(false);
            _btn2.setDisable(false);
        }
        
        // This syncs the dissapearing of the image with the Festival voice.
        _test.resetCorrectnessImage();
        
        // If the call specified to reset the Heart image to be reset, it shall be done here.
        if (_resetHearts) {
            _test.resetHeartImage();
        }

        return null;
    }

}
