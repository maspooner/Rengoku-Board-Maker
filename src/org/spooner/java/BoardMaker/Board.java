package org.spooner.java.BoardMaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Board extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener{
	//members
	private BoardData data;
	private int tileSize;
	private int width;
	private int height;
	private BoardPopup popup;
	private Thread updater;
	private boolean isPresent;
	private RequestState requestState;
	private Point marker1;
	private Point marker2;
	private boolean isDragging;
	private Point roughFocus;
	private BoardFrame frame;
	private Point clip1;
	private Point clip2;
	private boolean clipIsCopy;
	//constructors
	public Board(BoardFrame frame){
		data = null;
		marker1 = null;
		marker2 = null;
		clip1 = null;
		clip2 = null;
		clipIsCopy = false;
		setTransferHandler(new BoardDragHandler());
		addMouseListener(this);
		addMouseMotionListener(this);
		this.frame = frame;
		isPresent = false;
		isDragging = false;
		requestState = RequestState.NONE;
		popup = new BoardPopup(frame);
		updater = new Thread(this);
		updater.start();
	}
	//methods
	public boolean isPresent(){return isPresent;}
	public BoardData getData(){ return data; }
	public void loadBoard(BoardData data){
		clearSelection();
		this.data = data;
		resize();
		isPresent = true;
	}
	private void closeBoard(){
		clearSelection();
		isPresent=false;
		data = null;
		resize();
	}
	public void fillNulls(int id){
		data.fillNulls(id);
		revalidate();
		repaint();
	}
	public void fill(int id, boolean[] blocked, int foreID){
		//must be selecting at least one tile
		if(marker1 == null) return;
		boolean success = true;
		//filling with nulls or number
		if(id > -2){
			data.massSetID(marker1, marker2, id);
			//not a success if out of range
			if(id > TileSet.currentSet().getNumTiles()){
				success = false;
			}
		}
		else if(blocked != null){
			data.massSetBlocked(marker1, marker2, blocked);
		}
		else if(foreID > -2){
			data.massSetForeID(foreID, marker1, marker2);
			//not a success if out of range
			if(id > TileSet.foreSet().getNumTiles()){
				success = false;
			}
		}
		else
			success = false;
		//only if successful
		if(success){
			revalidate();
			repaint();
		}
	}
	public void createBlankBoard(int tWidth, int tHeight){
		clearSelection();
		data = new BoardData(tWidth, tHeight);
		resize();
		isPresent=true;
	}
	public void editBoard(int tWidth, int tHeight){
		clearSelection();
		data = new BoardData(data, tWidth, tHeight);
		resize();
	}
	private void resize(){
		//change tile draw scale
		tileSize = BoardConstants.ORG_TILE_SIZE * frame.getBoardZoom();
		width = data.getTileWidth() * tileSize;
		height = data.getTileHeight() * tileSize;
		//if should remove preferred size, set to null, else: new Dimension
		setPreferredSize(data == null ? null : new Dimension(width, height));
		//repaint
		revalidate();
		repaint();
	}
	public void notify(RequestState state){
		requestState = state;
		notify();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//no board
		if(!isPresent) return;
		//tiles
		data.draw(g, frame.markingsShown(), frame.getBoardZoom(), tileSize);
		//gridlines
		if(frame.isGridlines()){
			g.setColor(Color.CYAN);
			for(int i=tileSize;i<width;i+=tileSize){
				//vertical lines
				g.drawLine(i, 0, i, height);
				for(int j=tileSize;j<height;j+=tileSize){
					//horizontal lines
					g.drawLine(0, j, width, j);
				}
			}
		}
		//marker 1
		if(marker1!=null){
			g.setColor(Color.BLUE);
			g.drawRect(marker1.x*tileSize, marker1.y*tileSize, tileSize, tileSize);
		}
		//marker 2
		if(marker2!=null){
			g.setColor(Color.GREEN);
			g.drawRect(marker2.x*tileSize, marker2.y*tileSize, tileSize, tileSize);
		}
	}
	public Point getFinePoint(Point rough){
		//must be inside board
		if(rough.x > width || rough.y > height) 
			return null;
		//use integer math
		int xTile = rough.x/tileSize;
		int yTile = rough.y/tileSize;
		return new Point(xTile, yTile);
	}
	public void clearSelection(){
		marker1 = null;
		marker2 = null;
		repaint();
	}
	public void rememberTiles(boolean isCopy){
		if(isPresent){
			if(marker1 != null){
				this.clipIsCopy = isCopy;
				clip1 = new Point(marker1.x, marker1.y);
				//one location = null; multi = marker 2
				clip2 = marker2 == null ? null : new Point(marker2.x, marker2.y);
				clearSelection();
			}
		}
	}
	public void pasteTiles(){
		if(isPresent){
			if(marker1 != null && clip1 != null){
				data.moveTiles(clip1, clip2, marker1, clipIsCopy);
				if(clip2 != null){
					int dx = clip2.x - clip1.x;
					int dy = clip2.y - clip1.y;
					marker2 = new Point(marker1.x + dx, marker1.y + dy);
				}
				clip1 = null;
				clip2 = null;
				repaint();
			}
		}
	}
	private void moveTiles(Point destination, boolean isCopy){
		//get the focus of the move
		Point focus = getFinePoint(roughFocus);
		//focus of the mouse is not marker 1
		if(!focus.equals(marker1)) return;
		//calculate the change in position
		int dx = destination.x - marker1.x;
		int dy = destination.y - marker1.y;
		//no movement
		if(dx == 0 && dy == 0) return;
		//move the tiles
		data.moveTiles(marker1, marker2, destination, isCopy);
		//move markers
		if(marker1 != null){
			marker1.x += dx;
			marker1.y += dy;
		}
		if(marker2 != null){
			marker2.x += dx;
			marker2.y += dy;
			//if marker 2 is outside boundaries
			if(!data.isValid(marker2.x, marker2.y))
				marker2 = null;
		}
		repaint();
	}
	@Override
	public void run() {
		while(true){
			synchronized(this){
				try{
					//wait until new board needed or resize
					wait();
					switch(requestState){
					default: break;
					case CLOSE:
						closeBoard();
						break;
					case RESIZE:
						//don't resize if no board there
						if(!isPresent) continue;
						resize();
						break;
					}
				}catch(Exception e){
					BoardExceptionHandler.handle(3, e);
				}
				
			}
		}
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent me) {}
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent me) {
		if(!isPresent) return;
		Point release=me.getPoint();
		//outside of board
		if(release.x>width || release.y>height){
			isDragging=false;
			return;
		}
		if(me.isPopupTrigger()){
			//only right click
			popup.show(this, release.x, release.y);
		}
		else{
			//is left click
			//if just a click
			if(!isDragging){
				if(me.isShiftDown()){
					//shift left click
					//make sure marker1 placed first
					if(marker1!=null){
						//make sure not clicking on marker1
						Point p = getFinePoint(me.getPoint());
						if(!p.equals(marker1)){
							//must be bellow and to the right of marker1
							if(p.x >= marker1.x && p.y >= marker1.y)
								marker2 = getFinePoint(me.getPoint());
						}
					}
				}
				else{
					//left click
					marker1 = getFinePoint(me.getPoint());
					marker2 = null;
				}
			}
			else{
				//is a drag
				//if shift is down, is copy
				Point dest = getFinePoint(me.getPoint());
				moveTiles(dest, me.isShiftDown());
			}
		}
		isDragging = false;
		repaint();
	}
	@Override
	public void mouseDragged(MouseEvent me) {
		if(!isPresent) return;
		if(!isDragging){
			roughFocus=me.getPoint();
			isDragging=true;
		}
	}
	public void keyPressed(KeyEvent arg0) {}
	@Override
	public void keyReleased(KeyEvent ke) {
		boolean changed = true;
		if(marker1 == null){
			changed = false;
		}
		else{
			switch(ke.getKeyCode()){
				case KeyEvent.VK_UP:
					//in range
					if(marker1.y - 1 >= 0)
						marker1.y--;
					break;
				case KeyEvent.VK_DOWN:
					//in range
					if(marker1.y + 1 < data.getTileHeight())
						marker1.y++;
					break;
				case KeyEvent.VK_LEFT:
					//in range
					if(marker1.x - 1 >= 0)
						marker1.x--;
					break;
				case KeyEvent.VK_RIGHT:
					//in range
					if(marker1.x + 1 < data.getTileWidth())
						marker1.x++;
					break;
				default:
					changed = false;
				break;
			}
		}
		//changed marker 1
		if(changed){
			if(marker2 != null){
				//remove marker 2
				marker2 = null;
			}
			repaint();
		}
	}
	public void keyTyped(KeyEvent arg0) {}
}
