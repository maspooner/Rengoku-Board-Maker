package org.spooner.java.BoardMaker;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;

public class BoardConstants {
	//members
	protected static final Dimension DEFAULT_SIZE = new Dimension(800, 600);
	protected static final Dimension DIALOG_SIZE = new Dimension(500,400);
	protected static final int ORG_TILE_SIZE = 16;
	protected static final int V_SPACING_AREA = 5;
	protected static final int ZOOM_MAX = 10;
	protected static final int ZOOM_MIN = 1;
	protected static final int SCROLL_INCREMENT = 20;
	protected static final int DEFAULT_ZOOM = 3;
	protected static final int ENLARGEMENT_FACTOR = 25;
	
	private static final String RENGOKU_PROJECT_NAME = "rengoku\\"; //TODO real bad way of doing this
	private static final String PROJECT_LOC = System.getProperty("user.dir");
	private static final String ASSETS_PATH = PROJECT_LOC.substring(0, PROJECT_LOC.lastIndexOf(File.separatorChar) + 1) 
			+ RENGOKU_PROJECT_NAME + "assets\\";
	protected static final String BOARDS_PATH = ASSETS_PATH + "boards\\";
	protected static final String TILE_SET_PATH = ASSETS_PATH + "tilesets\\";
	protected static final String FORE_PATH = TILE_SET_PATH + "foregrounds\\";
	
	protected static final int KEY_CUT = KeyEvent.VK_X;
	protected static final int KEY_COPY = KeyEvent.VK_C;
	protected static final int KEY_PASTE = KeyEvent.VK_V;
	protected static final int KEY_SAVE = KeyEvent.VK_S;
	protected static final int KEY_FOREGROUND = KeyEvent.VK_F;
	protected static final int KEY_BLOCK = KeyEvent.VK_B;
	protected static final int KEY_ZOOM_BOARD_IN = KeyEvent.VK_EQUALS;
	protected static final int KEY_ZOOM_BOARD_OUT = KeyEvent.VK_MINUS;
	protected static final int KEY_ZOOM_AREA_IN = KeyEvent.VK_COMMA;
	protected static final int KEY_ZOOM_AREA_OUT = KeyEvent.VK_PERIOD;
	protected static final int KEY_MARKINGS = KeyEvent.VK_M;
	protected static final int KEY_GRIDLINES = KeyEvent.VK_G;
	protected static final int KEY_APPLY = KeyEvent.VK_A;
	protected static final int KEY_NEW = KeyEvent.VK_N;
	protected static final int KEY_LOAD = KeyEvent.VK_O;
}
