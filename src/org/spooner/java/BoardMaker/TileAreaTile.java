package org.spooner.java.BoardMaker;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public class TileAreaTile extends JComponent implements MouseListener{
	//members
	private int id;
	private BufferedImage icon;
	private int tileSize;
	//constructors
	public TileAreaTile(int id, BufferedImage icon){
		this.id=id;
		this.icon=icon;
		addMouseListener(this);
		setTransferHandler(new BoardDragHandler());
		setToolTipText("ID: "+id);
	}
	//methods
	public int getID(){return id;}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		BoardFrame frame = (BoardFrame) getTopLevelAncestor();
		int tileSize = BoardConstants.ORG_TILE_SIZE * frame.getAreaZoom();
		//if there is a change
		if(this.tileSize!=tileSize){
			this.tileSize=tileSize;
			int newSize = tileSize + (BoardConstants.ENLARGEMENT_FACTOR * frame.getAreaZoom());
			setPreferredSize(new Dimension(newSize, newSize));
			revalidate();
		}
		g.drawImage(icon, 0, 0, tileSize, tileSize, null);
	}
	@Override
	public void mousePressed(MouseEvent me) {
		getTransferHandler().exportAsDrag(this, me, TransferHandler.COPY);
	}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
}
