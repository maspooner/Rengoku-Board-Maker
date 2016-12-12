package org.spooner.java.BoardMaker;

import java.awt.image.BufferedImage;
import java.io.File;

public class TileSet {
	//members
	private static TileSet currentSet = null;
	private static TileSet foreSet = null;
	private int id;
	private BufferedImage[] icons;
	//constructors
	private TileSet(int id, String dir){
		this.id = id;
		//load the tileset image
		BufferedImage source = BoardIO.loadImage(new File(dir + "/" + id + ".png"));
		int width=source.getWidth();
		int height=source.getHeight();
		//incorrect sizing
		if(width % BoardConstants.ORG_TILE_SIZE != 0 || height % BoardConstants.ORG_TILE_SIZE != 0)
			BoardExceptionHandler.handle(9, new Exception());
		//find tile dimensions
		int twidth=width/BoardConstants.ORG_TILE_SIZE;
		int theight=height/BoardConstants.ORG_TILE_SIZE;
		//create the array
		icons=new BufferedImage[twidth*theight];
		//create the icons
		createIcons(source);
	}
	//methods
	public String getPath(){return BoardConstants.TILE_SET_PATH + id + ".png";}
	public int getID(){ return id; }
	public int getNumTiles(){return icons.length;}
	public BufferedImage getIcon(int id){
		return icons[id];
	}
	private void createIcons(BufferedImage source){
		final int size=BoardConstants.ORG_TILE_SIZE;
		int width=source.getWidth();
		int col=0;
		int row=0;
		for(int i=0;i<icons.length;i++){
			//crop out tiles
			icons[i]=source.getSubimage(col*size, row*size, size, size);
			//increment col
			col++;
			//if at end of row
			if(col*size==width){
				row++;
				col=0;
			}
		}
	}
	public static boolean isLoaded(){ return currentSet != null; }
	public static TileSet currentSet(){ return currentSet; }
	public static TileSet foreSet(){ return foreSet; }
	public static void loadSet(int id){
		currentSet = new TileSet(id, BoardConstants.TILE_SET_PATH);
		foreSet = new TileSet(id, BoardConstants.FORE_PATH);
	}
}
