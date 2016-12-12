package org.spooner.java.BoardMaker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class Tile {
	//members
	private int id;
	private boolean[] isSideBlocked;
	private String boardChange;
	private int startID;
	private int face;
	private int foreID;
	private int after;
	private int item;
	private int fire;
	//constuctors
	public Tile(int id, boolean[] isSideBlocked, String boardChange, int startID, int face, int foreID, int after, int item, int fire){
		this.id=id;
		this.isSideBlocked = isSideBlocked;
		this.boardChange = boardChange;
		this.startID = startID;
		this.face = face;
		this.foreID = foreID;
		this.after = after;
		this.item = item;
		this.fire = fire;
	}
	public Tile(){
		this(-1, new boolean[]{false, false, false, false}, null, -1, 0, -1, -1, -1, -1);
	}
	public Tile(Tile t){
		this(t.id, new boolean[]{ t.isSideBlocked[0], t.isSideBlocked[1], t.isSideBlocked[2], t.isSideBlocked[3] },
				t.boardChange, t.startID, t.face, t.foreID, t.after, t.item, t.fire);
	}
	//methods
	public int getForeID(){ return foreID; }
	public void setForeID(int foreID){ this.foreID = foreID; }
	public int getID(){return id;}
	public void setID(int id){this.id = id;}
	public void blockAll(boolean blocked){
		for(int i = 0; i < isSideBlocked.length; i++)
			isSideBlocked[i] = blocked;
	}
	public boolean allUnblocked(){
		boolean allUnblocked = true;
		for(int i = 0; i < isSideBlocked.length && allUnblocked; i++)
			allUnblocked = !isSideBlocked[i];
		return allUnblocked;
	}
	public void setBlockedArray(boolean[] blocked){
		//deep copy
		for(int i = 0; i < 4; i++){
			isSideBlocked[i] = blocked[i];
		}
	}
	public void delete(){
		//set id to -1
		id = -1;
		//reset blocked
		blockAll(false);
		//not foreground
		foreID = -1;
		boardChange = null;
		after = -1;
		item = -1;
		fire = -1;
	}
	public boolean[] getBlockedArray(){ return isSideBlocked; }
	public void setBoardChange(String boardChange, int startID, int face){
		this.boardChange = boardChange;
		this.startID = startID;
		this.face = face;
	}
	public String getBoardChange(){ return boardChange; }
	public int getStartID(){ return startID; }
	public int getFace() { return face; }
	public void setItemTile(int after, int item, int fire){
		this.after = after;
		this.item = item;
		this.fire = fire;
	}
	public int getAfter() { return after; }
	public int getItem() { return item; }
	public int getFire(){ return fire; }
	public void draw(Graphics g, int x, int y, int size, boolean markingsShown, int zoom){
		//if no tile
		if(id == -1){
			//draw black rectangle
			g.setColor(Color.BLACK);
			g.fillRect(x, y, size, size);
		}
		else{
			//draw the tile
			g.drawImage(TileSet.currentSet().getIcon(id), x, y, size, size, null);
		}
		if(markingsShown){
			//foreground
			if(foreID >= 0)
				drawForeground(g, x, y, zoom, markingsShown);
			//borders
			drawBorders(g, x, y, size, zoom);
			//board change
			if(boardChange != null)
				drawConnection(g, x, y, zoom);
			if(after != -1)
				drawItem(g, x, y, zoom);
		}
	}
	private void drawBorders(Graphics g, int x, int y, int tileSize, int blockSize){
		g.setColor(Color.RED);
		if(isSideBlocked[0]){
			//up block
			g.fillRect(x, y, tileSize, blockSize);
		}
		if(isSideBlocked[1]){
			//down block
			g.fillRect(x, y + tileSize - blockSize, tileSize, blockSize);
		}
		if(isSideBlocked[2]){
			//left block
			g.fillRect(x, y, blockSize, tileSize);
		}
		if(isSideBlocked[3]){
			//down block
			g.fillRect(x + tileSize - blockSize, y, blockSize, tileSize);
		}
	}
	private void drawConnection(Graphics g, int x, int y, int zoom){
		g.setColor(Color.YELLOW);
		g.fillRect(x + 2 * zoom, y + 2 * zoom, 2 * zoom, 2 * zoom);
	}
	private void drawItem(Graphics g, int x, int y, int zoom){
		g.setColor(Color.CYAN);
		g.fillOval(x + 5 + 2 * zoom, y + 5 + 2 * zoom, 2 * zoom, 2 * zoom);
	}
	private void drawForeground(Graphics g, int x, int y, int zoom, boolean markingsShown){
		Image orgImage = TileSet.foreSet().getIcon(foreID);
		int w = orgImage.getWidth(null) * zoom;
		int h = orgImage.getHeight(null) * zoom;
		Image scaledImage = orgImage.getScaledInstance(w, h, Image.SCALE_FAST);
		g.drawImage(scaledImage, x, y, null);
		//draw transpartent cover
		if(markingsShown){
			g.setColor(new Color(1.0f, 0.0f, 1.0f, 0.2f));
			g.fillRect(x, y, w, h);
		}
	}
}
