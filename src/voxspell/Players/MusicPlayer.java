package voxspell.Players;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.*;

/**
 *
 * This class play background music, or sometimes audio, on a non-visible player.
 * 
 * 
 * @author victor
 */
public class MusicPlayer {

    private static MediaPlayer player; // From dac2009 at StackOverflow, need a declared MediaPlayuer variable so that music does not stop after 5 seconds.
    private Media _track;
    private boolean _stopped = false;
    private boolean _doRepeat;

    public MusicPlayer(String track, boolean doRepeat) {
        _track = new Media(new File(track).toURI().toString());
        _doRepeat = doRepeat;
    }

    
    /**
     * This method sets a new music/audio file to play.
     * @param newTrack 
     */
    public void setAndPlayMusic(String newTrack) {
        player.stop();
        _track = new Media(new File(newTrack).toURI().toString());
        playMusic();
    }

    
    /**
     * This method plays the audio file at 0.25 volume to not be too loud for the user.
     */
    public void playMusic() {
        player = new MediaPlayer(_track);
        player.setVolume(0.20);

        // It can be specified by an invoking object whether to loop the audio indefinitely.
        if (_doRepeat) {
            // Retrieved from user2768684 to make indefinite looping of music, from StackOverflow.
            player.setOnEndOfMedia(new Runnable() {
                public void run() {
                    player.seek(Duration.ZERO);
                }
            });
        }

        player.play();
    }

    
    /**
     * This method toggles the music on or off depending on whether the audio is playing right now.
     */
    public void toggle() {
        if (!_stopped) {
            player.stop();
            _stopped = true;

        } else {
            player.play();
            _stopped = false;
        }
    }
    
    
    /**
     * This method sets the audio volume.
     * @param volume 
     */
    public void setVolume(double volume) {
        player.setVolume(volume);
    }

}
