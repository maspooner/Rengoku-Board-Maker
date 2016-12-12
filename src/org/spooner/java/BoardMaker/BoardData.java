package org.spooner.java.BoardMaker;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import org.w3c.dom.Element;

public class BoardData {
	//members
	private Tile[][] tiles;
	private ArrayList<Element> others;
	//constructor
	protected BoardData(int tWidth, int tHeight){
		//blank board
		tiles = new Tile[tWidth][tHeight];
		//set all tiles to id=-1
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[i].length; j++){
				tiles[i][j] = new Tile();
			}
		}
	}
	protected BoardData(Tile[][] tiles, ArrayList<Element> others){
		this.tiles = tiles;
		this.others = others;
	}
	protected BoardData(BoardData oldData, int tWidth, int tHeight){
		Tile[][] oldTiles = oldData.tiles;
		//create a new board with different dimensions
		tiles = new Tile[tWidth][tHeight];
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0;j < tiles[i].length; j++){
				//if new tiles[][] is bigger than old
				if(i >= oldTiles.length || j >= oldTiles[i].length){
					//create blank tile
					tiles[i][j] = new Tile();
				}
				else{
					tiles[i][j] = oldTiles[i][j];
				}
			}
		}
		//copy the others
		this.others = oldData.others;
	}
	//methods
	public Tile[][] getTiles(){ return tiles; }
	public ArrayList<Element> getOthers(){ return others; }
	public void setTiles(Tile[][] tiles) { this.tiles = tiles; }
	public int getTileWidth(){ return tiles.length; }
	public int getTileHeight(){ return tiles[0].length; }
	public Tile getTileAt(int i, int j){ return tiles[i][j]; }
	public Tile getTileAt(Point p) { return getTileAt(p.x, p.y); }
	public boolean containsNulls(){
		boolean nulls = false;
		for(int i = 0; i < tiles.length && !nulls; i++){
			for(int j = 0; j < tiles[i].length && !nulls; j++){
				if(tiles[i][j].getID() == -1){
					nulls = true;
				}
			}
		}
		return nulls;
	}
	public void draw(Graphics g, boolean markingsShown, int boardZoom, int tileSize){
		for(int i=0;i<tiles.length;i++){
			for(int j=0;j<tiles[i].length;j++){
				tiles[i][j].draw(g, i * tileSize, j * tileSize,
						tileSize, markingsShown, boardZoom);
			}
		}
	}
	public boolean isValid(int x, int y){ return x < getTileWidth() && y < getTileHeight(); }
	public void massSetID(Point m1, Point m2, int id){
		fill(m1, m2, id, null, -20);
	}
	public void massSetBlocked(Point m1, Point m2, boolean[] blocked){
		fill(m1, m2, -20, blocked, -20);
	}
	public void massSetForeID(int foreID, Point m1, Point m2){
		fill(m1, m2, -20, null, foreID);
	}
	private void fill(Point m1, Point m2, int id, boolean[] blocked, int foreID){
		//both markers present
		if(m2 != null){
			//for every x value from 1 to 2
			for(int i = m1.x; i <= m2.x; i++){
				//for every y value from 1 to 2
				for(int j = m1.y; j <= m2.y; j++){
					fillTile(tiles[i][j], id, blocked, foreID);
				}
			}
		}
		else{
			//just at marker 1
			fillTile(getTileAt(m1), id, blocked, foreID);
		}
	}
	public void setID(Point m1, int id){
		massSetID(m1, null, id);
	}
	public void setBlocked(Point m1, boolean[] blocked){
		massSetBlocked(m1, null, blocked);
	}
	private void fillTile(Tile t, int id, boolean[] blocked, int foreID){
		//if deleting
		if(id == -1){
			t.delete();
		}
		else if(id > -1){
			t.setID(id);
		}
		else if(blocked != null){
			t.setBlockedArray(blocked);
		}
		else if(foreID > -2){
			t.setForeID(foreID);
		}
	}
	public void fillNulls(int id){
		for(Tile[] ta : tiles){
			for(Tile t : ta){
				if(t.getID() == -1){
					t.setID(id);
				}
			}
		}
	}
	private void moveTile(Tile source, int x, int y, int dx, int dy, boolean isCopy){
		//don't have to check bellow zeros (no way to move them off screen up or left)
		//if destination inside boundaries
		if(isValid(x + dx, y + dy)){
			//create a copy with the source
			tiles[x + dx][y + dy] = new Tile(source);
		}
		//if cut, delete source tile data
		if(!isCopy){
			source.delete();
		}
	}
	public void moveTiles(Point m1, Point m2, Point release, boolean isCopy){
		//calculate the change in position
		int dx = release.x - m1.x;
		int dy = release.y - m1.y;
		//no marker 2, just one tile
		if(m2 == null){
			moveTile(tiles[m1.x][m1.y], m1.x, m1.y, dx, dy, isCopy);
		}
		else{
			//clip the size of the selection
			Tile[][] clip = new Tile[m2.x - m1.x + 1][m2.y - m1.y + 1];
			for(int i = 0; i < clip.length; i++){
				for(int j = 0; j < clip[0].length; j++){
					Tile t = tiles[m1.x + i][m1.y + j];
					//save the tile in the clip
					clip[i][j] = t;
				}
			}
			//paste the clip in the right location
			for(int i = 0; i < clip.length; i++){
				for(int j = 0; j < clip[0].length; j++){
					moveTile(clip[i][j], release.x, release.y, i, j, isCopy);
				}
			}
		}
	}
}
