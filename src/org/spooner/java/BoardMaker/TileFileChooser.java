package org.spooner.java.BoardMaker;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class TileFileChooser{
	//members
	private static JFileChooser tileSetChooser = setupChooser(".png", BoardConstants.TILE_SET_PATH);
	private static JFileChooser boardChooser = setupChooser(".xml", BoardConstants.BOARDS_PATH);
	private static String lastBoardLoad;
	//methods
	private static JFileChooser setupChooser(final String allowedType, final String defaultDir){
		JFileChooser fileChooser=new JFileChooser(defaultDir);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileFilter() {
			public String getDescription() {return null;}
			public boolean accept(File file) {
				return file.getName().endsWith(allowedType) || file.isDirectory();
			}
		});
		return fileChooser;
	}
	public static File loadTileSetFile(Component parent){
		int value=tileSetChooser.showOpenDialog(parent);
		if(value==JFileChooser.APPROVE_OPTION){
			return tileSetChooser.getSelectedFile();
		}
		return null;
	}
	public static File saveBoardFile(Component parent){
		int value=boardChooser.showSaveDialog(parent);
		if(value==JFileChooser.APPROVE_OPTION){
			String name=boardChooser.getSelectedFile().getAbsolutePath();
			return new File(name+".xml");
		}
		return null;
	}
	public static File loadBoardFile(Component parent){
		int value=boardChooser.showOpenDialog(parent);
		if(value==JFileChooser.APPROVE_OPTION){
			File board=boardChooser.getSelectedFile();
			lastBoardLoad=board.getAbsolutePath();
			return board;
		}
		return null;
	}
	public static String getLastBoardLoad(){return lastBoardLoad;}
}
