package org.spooner.java.BoardMaker;
/**
 * BoardMaker
 * Made by Matt Spooner
 * 
 * Date Created: Apr 8, 2014
 * Last Modified: Apr 28, 2015
 * 
 * New Topics:
 * 	wait/notify
 *  synchronization
 *  drag and drop
 *  cut, paste, keyboard shortcuts
 * 
 * TODO
 * changeable null tile color
 * changeable tangibility color
 * remove printlns
 */
public abstract class BoardMaker{
	//members
	private static final String TITLE = "Board Maker ";
	private static final String VERSION = "v1.4";
	//methods
	public static void main(String[] args){
		new BoardFrame(TITLE + VERSION);
	}
}
