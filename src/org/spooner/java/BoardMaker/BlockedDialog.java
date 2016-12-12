package org.spooner.java.BoardMaker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;

@SuppressWarnings("serial")
public class BlockedDialog extends JDialog implements KeyListener{
	private static final int BOX_SIZE = 50;
	private static final int SIZE = 300;
	//members
	private boolean[] directions;
	private boolean wasSuccess;
	//constructors
	protected BlockedDialog(){
		directions = new boolean[4];
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setSize(SIZE, SIZE);
		setTitle("Select your directions.");
		addKeyListener(this);
		wasSuccess = false;
		setModal(true);
	}
	//methods
	public void flipDirection(int i){
		directions[i] = !directions[i];
		repaint();
	}
	public boolean[] getDirections(){ return directions; }
	public void setDirections(boolean[] directions){ this.directions = directions; }
	public boolean consumeSuccess(){
		boolean success = wasSuccess;
		if(success){
			wasSuccess = false;
		}
		return success;
	}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		//creating a 9x9 grid
		int leftoverSpace = SIZE - (3 * BOX_SIZE);
		//4 spaces in a 9x9 grid
		int spaceBetween = leftoverSpace / 4;
		//left direction
		int x = spaceBetween;
		int y = spaceBetween + BOX_SIZE + spaceBetween;
		drawSquare(g, 2, x, y);
		//right direction
		x += 2 * (BOX_SIZE + spaceBetween);
		drawSquare(g, 3, x, y);
		//up direction
		x -= BOX_SIZE + spaceBetween;
		y -= BOX_SIZE + spaceBetween;
		drawSquare(g, 0, x, y);
		//down direction
		y += 2 * (BOX_SIZE + spaceBetween);
		drawSquare(g, 1, x, y);
	}
	private void drawSquare(Graphics g, int i, int x, int y){
		g.setColor(directions[i] ? Color.GREEN : Color.RED);
		g.fillRect(x, y, BOX_SIZE, BOX_SIZE);
	}
	public void keyPressed(KeyEvent ke) {}
	@Override
	public void keyReleased(KeyEvent ke) {
		int key = ke.getKeyCode();
		switch(key){
			case KeyEvent.VK_UP: flipDirection(0); break;
			case KeyEvent.VK_DOWN: flipDirection(1); break;
			case KeyEvent.VK_LEFT: flipDirection(2); break;
			case KeyEvent.VK_RIGHT: flipDirection(3); break;
			case KeyEvent.VK_ENTER:
				wasSuccess = true;
				setVisible(false);
			break;
			default: break;
		}
	}
	public void keyTyped(KeyEvent ke) {}
}
