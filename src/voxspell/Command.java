package voxspell;

import voxspell.GUIScreens.NonMainGUI;

/**
 * This interface provides a common typing for all models to associate with, they all must all implement
 * the execute method.
 * @author Jacky Lo
 *
 */

// Added an addGUI method to adapt the interface to a JavaFX context. - Victor
public interface Command {
	public void execute();
        public void addGUI(NonMainGUI viewGUI);
}

