/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voxspell.GUIScreens;

/**
 * From Angela Caicedo's blog, at https://blogs.oracle.com/acaicedo/entry/managing_multiple_screens_in_javafx1
 * This interface allows the implementing screen classes to know their parent, which is MainMenu.
 * 
 * @author victor
 */
public interface ControlledScreen {
    
    public void setParent(StackedScreens screen);
    
}
