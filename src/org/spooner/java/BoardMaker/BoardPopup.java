package org.spooner.java.BoardMaker;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class BoardPopup extends JPopupMenu implements ActionListener{
	//members
	private int x;
	private int y;
	private BoardFrame frame;
	//constructors
	public BoardPopup(BoardFrame frame){
		this.frame = frame;
		JMenuItem item;
		//delete
		item=new JMenuItem("Delete");
		item.setActionCommand("del");
		item.addActionListener(this);
		add(item);
		//apply
		item=new JMenuItem("Apply block");
		item.setActionCommand("apply");
		item.addActionListener(this);
		add(item);
		//id
		item=new JMenuItem("ID");
		item.setActionCommand("id");
		item.addActionListener(this);
		add(item);
		//coordinates
		item=new JMenuItem("Coordinates");
		item.setActionCommand("coords");
		item.addActionListener(this);
		add(item);
		//board id
		item=new JMenuItem("Board ID");
		item.setActionCommand("bid");
		item.addActionListener(this);
		add(item);
		//item
		item=new JMenuItem("Item tile");
		item.setActionCommand("item");
		item.addActionListener(this);
		add(item);
		//foreground
		item=new JMenuItem("Set foreground");
		item.setActionCommand("fore");
		item.addActionListener(this);
		add(item);
	}
	//methods
	@Override
	public void show(Component c, int x, int y) {
		this.x = x;
		this.y = y;
		super.show(c, x, y);
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		String ac = ae.getActionCommand();
		Board b = (Board) getInvoker();
		Point tPoint = b.getFinePoint(new Point(x, y));
		Tile t = b.getData().getTileAt(tPoint);
		if(ac.equals("del")){
			b.getData().setID(tPoint, -1);
			b.repaint();
		}
		else if(ac.equals("apply")){
			b.getData().setBlocked(tPoint, frame.getBlockedArray());
			b.repaint();
		}
		else if(ac.equals("id")){
			JOptionPane.showMessageDialog(this, Integer.toString(t.getID()));
		}
		else if(ac.equals("coords")){
			JOptionPane.showMessageDialog(this, "x:" + tPoint.x + " y:" + tPoint.y);
		}
		else if(ac.equals("bid")){
			String bid = t.getBoardChange();
			//avoid null pointer
			if(bid == null) bid = "None.";
			//get new bid
			String newbid = JOptionPane.showInputDialog(this, "Enter a board change id: ", bid);
			//user did not cancel
			if(newbid != null){
				int spid = getNum(t.getStartID(), "Enter a start point: ");
				if(spid != -1){
					int face = getNum(t.getFace(), "Enter a direction to face: ");
					if(face != -1){
						//one of these is messed up
						if(newbid.isEmpty() || spid == -2 || face == -2){
							t.setBoardChange(null, -1, 0);
						}
						else{
							//make it in range if not
							if(face < 0 || face > 3) face = 0;
							//everything is good
							t.setBoardChange(newbid, spid, face);
						}
						b.repaint();
					}
				}
			}
		}
		else if(ac.equals("item")){
			//get responses
			int after = getNum(t.getAfter(), "Enter a flag for which the item will be available after: ");
			if(after != -1){
				int item = getNum(t.getItem(), "Enter a flag for the item to give: ");
				if(item != -1){
					int fire = getNum(t.getFire(), "Enter a flag to fire after the item is recieved: ");
					if(fire != -1){
						//wrong value anywhere ruins everyone
						if(after == -2 || item == -2 || fire == -2){
							t.setItemTile(-1, -1, -1);
						}
						else{
							t.setItemTile(after, item, fire);
						}
						//all good
						b.repaint();
					}
				}
			}
			
		}
		else if(ac.equals("fore")){
			int oldFore = t.getForeID();
			String fore = JOptionPane.showInputDialog(this, "Enter a foreground ID: ", oldFore);
			//user did not cancel
			if(fore != null){
				//nothing there, remove
				if(fore.isEmpty()){
					t.setForeID(-1);
				}
				//try parsing
				else{
					try{
						int newFore = Integer.parseInt(fore);
						//success means convert successful
						//must be within range of foreground set
						if(newFore < TileSet.foreSet().getNumTiles()){
							t.setForeID(newFore);
						}
					}
					catch(NumberFormatException nfe){
						//failed = do nothing
					}
				}
			}
			b.repaint();
		}
	}
	private int getNum(int init, String message){
		String newVal = JOptionPane.showInputDialog(this, message, init);
		//user did not cancel
		if(newVal != null){
			//try parsing
			try{
				int intVal = Integer.parseInt(newVal);
				if(intVal >= 0){
					return intVal;
				}
			}
			catch(NumberFormatException nfe){
				//do nothing
			}
			//user input wrong value
			return -2;
		}
		//user canceled
		return -1;
	}
}
