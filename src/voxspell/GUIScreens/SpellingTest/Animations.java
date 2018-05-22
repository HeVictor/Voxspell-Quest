/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell.GUIScreens.SpellingTest;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * This class creates animations by transitioning between various images used as individual frames. The code below is retrieved from AjithKP560_ at http://www.codeproject.com/Tips/788527/Creating-Animation-from-Sequence-of-Images-in-Java. Some minor modifications were added, which is using the non-deprecated version of Timeline over TimelineBuilder.
 *
 * @author victor
 */
public class Animations {

    private ImageView[] images = new ImageView[10];
    private ImageView idle;

    private Group _group;
    private String _action;

    public Animations(Group group) {
        _group = group;
        Image idleImg = new Image(getClass().getResourceAsStream("Images/Idle.png"));
        idle = new ImageView(idleImg);
        idle.setFitHeight(idleImg.getHeight() / 6);
        idle.setFitWidth(idleImg.getWidth() / 6);
    }

    
    /**
     * Animate the given action, with a choice of also reverseing the animation or not.
     * @param action
     * @param doReverse 
     */
    public void animateAction(String action, boolean doReverse) {
        _action = action;
        setup();
        animate(doReverse);
    }

    
    /**
     * Setup all the image frames.
     */
    private void setup() {

        for (int count = 0; count < images.length; count++) {
            int imgNumber = count + 1;

            Image image = new Image(getClass().getResourceAsStream("Images/" + _action + " (" + imgNumber + ").png"));

            images[count] = new ImageView(image);
            images[count].setFitHeight(image.getHeight() / 6);
            images[count].setFitWidth(image.getWidth() / 6);
        }

    }

    
    /**
     * Set the idle Knight image.
     */
    public void setIdleImage() {
        _group.getChildren().setAll(idle);
    }

    
    /**
     * Animate through the frames. It can also animate in reverse.
     * @param doReverse 
     */
    private void animate(boolean doReverse) {
        Timeline timeline = new Timeline();
        timeline.setAutoReverse(doReverse);

        if (doReverse) {
            timeline.setCycleCount(2);
        } else {
            timeline.setCycleCount(1);
        }

        timeline.getKeyFrames().addAll(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                _group.getChildren().setAll(idle);
            }
        }),
                new KeyFrame(Duration.millis(200), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[0]);
                    }
                }),
                new KeyFrame(Duration.millis(300), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[1]);
                    }
                }),
                new KeyFrame(Duration.millis(400), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[2]);
                    }
                }),
                new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[3]);
                    }
                }),
                new KeyFrame(Duration.millis(600), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[4]);
                    }
                }),
                new KeyFrame(Duration.millis(700), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[5]);
                    }
                }),
                new KeyFrame(Duration.millis(800), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[6]);
                    }
                }),
                new KeyFrame(Duration.millis(900), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[7]);
                    }
                }),
                new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[8]);
                    }
                }),
                new KeyFrame(Duration.millis(1100), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        _group.getChildren().setAll(images[9]);
                    }
                }),
                new KeyFrame(Duration.millis(1200), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {

                        // Only set the frame here to be Idle if it's attack motion, as Dead action will need to reverse the animation anyway.
                        if (_action.equals("Attack")) {
                            _group.getChildren().setAll(idle);
                        }

                    }
                })
        );
        timeline.play();

    }

}
