package org.spooner.java.BoardMaker;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TileArea extends JPanel implements Runnable, ComponentListener{
	//members
	private Thread updater;
	private RequestState requestState;
	private BoardFrame frame;
	//constructors
	public TileArea(BoardFrame frame){
		addComponentListener(this);
		this.frame = frame;
		setLayout(new GridLayout(0, 1, 0, BoardConstants.V_SPACING_AREA));
		requestState=RequestState.NONE;
		updater=new Thread(this);
		updater.start();
	}
	//methods
	private void clearArea(){
		removeAll();
		setPreferredSize(new Dimension(0,0));
		revalidate();
		repaint();
	}
	private void changeDimensions(){
		//if no tile set
		if(!TileSet.isLoaded()) return;
		//-20 for scroll bar width
		int tileSize=BoardConstants.ORG_TILE_SIZE * frame.getAreaZoom();
		int scrollWidth=getParent().getParent().getWidth()-20;
		GridLayout gl=(GridLayout) getLayout();
		
		int col=(int) scrollWidth/tileSize;
		//make sure col rounds down
		if(tileSize*col>=scrollWidth) col--;
		//make sure col is at least 1
		if(col==0) col=1;
		gl.setColumns(col);
		//rounds up
		int row=(int) TileSet.currentSet().getNumTiles() / col;
		//height is equal to all tiles+spacing in a column
		int scrollHeight=row*(tileSize+BoardConstants.V_SPACING_AREA);
		setPreferredSize(new Dimension(scrollWidth, scrollHeight));
		revalidate();
		repaint();
		
	}
	public void notify(RequestState state){
		requestState=state;
		notify();
	}
	private void changeTiles(){
		removeAll();
		for(int i = 0; i < TileSet.currentSet().getNumTiles(); i++){
			//TODO optimise (don't create new areatiles)
			add(new TileAreaTile(i, TileSet.currentSet().getIcon(i)));
		}
		revalidate();
		repaint();
	}
	@Override
	public void run(){
		while(true){
			synchronized(this){
				try{
					//wait until new tile set or resize
					wait();
					switch(requestState){
					default: break;
					case CLOSE:
						clearArea();
						break;
					case LOAD:
						//same as load
					case EDIT:
						//same as NEW
					case NEW:
						//change in tile set
						changeTiles();
						changeDimensions();
						break;
					case RESIZE:
						changeDimensions();
						break;
					}
				}catch(Exception e){
					BoardExceptionHandler.handle(1, e);
				}
			}
		}
	}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentResized(ComponentEvent ce) {
		changeDimensions();
	}
}
