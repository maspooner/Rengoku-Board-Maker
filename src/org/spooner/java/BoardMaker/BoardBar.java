package org.spooner.java.BoardMaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class BoardBar extends JMenuBar implements ActionListener{
	//members
	private BlockedDialog blockedDialog;
	//constructors
	public BoardBar(){
		initMenus();
		blockedDialog = new BlockedDialog();
	}
	//methods
	private void initMenus(){
		JMenu menu;
		//file
		menu = new JMenu("File");
		menu.add(createItem("New", BoardConstants.KEY_NEW));
		menu.add(createItem("Edit"));
		menu.add(createItem("Close"));
		menu.add(createItem("Save", BoardConstants.KEY_SAVE));
		menu.add(createItem("Save as"));
		menu.add(createItem("Open", BoardConstants.KEY_LOAD));
		add(menu);
		//edit
		menu = new JMenu("Edit");
		menu.add(createItem("Cut", BoardConstants.KEY_CUT));
		menu.add(createItem("Copy", BoardConstants.KEY_COPY));
		menu.add(createItem("Paste", BoardConstants.KEY_PASTE));
		add(menu);
		//view
		menu = new JMenu("View");
		menu.add(createCheckItem("Gridlines", BoardConstants.KEY_GRIDLINES));
		menu.add(createCheckItem("Show Markings", BoardConstants.KEY_MARKINGS));
		add(menu);
		//zoom menu
		menu = new JMenu("Zoom");
		menu.add(createItem("Board in", BoardConstants.KEY_ZOOM_BOARD_IN));
		menu.add(createItem("Board out", BoardConstants.KEY_ZOOM_BOARD_OUT));
		menu.add(createItem("Area in", BoardConstants.KEY_ZOOM_AREA_IN));
		menu.add(createItem("Area out", BoardConstants.KEY_ZOOM_AREA_OUT));
		add(menu);
		//tile menu
		menu = new JMenu("Tile");
		
		//blocked section
		menu.add(createItem("All blocked"));
		menu.add(createItem("All open"));
		menu.add(createItem("Custom block", BoardConstants.KEY_BLOCK));
		menu.add(createItem("Apply Config", BoardConstants.KEY_APPLY));
		menu.addSeparator();
		//rest section
		menu.add(createItem("Clear Selection"));
		menu.add(createItem("Fill foreground", BoardConstants.KEY_FOREGROUND));
		menu.add(createItem("Fill"));
		menu.add(createItem("Delete"));
		add(menu);
	}
	private JMenuItem createItem(String name){
		JMenuItem item=new JMenuItem(name);
		item.addActionListener(this);
		return item;
	}
	private JMenuItem createItem(String name, int key){
		JMenuItem item = createItem(name);
		item.setAccelerator(KeyStroke.getKeyStroke(key, ActionEvent.CTRL_MASK));
		return item;
	}
	private JCheckBoxMenuItem createCheckItem(String name){
		JCheckBoxMenuItem item=new JCheckBoxMenuItem(name);
		item.addActionListener(this);
		//set to default state (to be in sync with inverting)
		item.setSelected(BoardFrame.DEFAULT_STATE);
		return item;
	}
	private JCheckBoxMenuItem createCheckItem(String name, int key){
		JCheckBoxMenuItem item = createCheckItem(name);
		item.setAccelerator(KeyStroke.getKeyStroke(key, ActionEvent.CTRL_MASK));
		return item;
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		String ac = ae.getActionCommand();
		BoardFrame frame = (BoardFrame) getTopLevelAncestor();
		//file
		if(ac.equals("New")){
			frame.displayDialog(true);
			if(frame.wasDialogSuccess()){
				synchronized(frame){
					frame.notify(RequestState.NEW);
				}
			}
		}
		else if(ac.equals("Edit")){
			frame.displayDialog(false);
			if(frame.wasDialogSuccess()){
				synchronized(frame){
					frame.notify(RequestState.EDIT);
				}
			}
		}
		else if(ac.equals("Close")){
			synchronized(frame){
				frame.notify(RequestState.CLOSE);
			}
		}
		else if(ac.equals("Save")){
			synchronized(frame){
				frame.notify(RequestState.SAVE);
			}
		}
		else if(ac.equals("Save as")){
			synchronized(frame){
				frame.notify(RequestState.SAVE_AS);
			}
		}
		else if(ac.equals("Open")){
			synchronized(frame){
				frame.notify(RequestState.LOAD);
			}
		}
		//edit
		else if(ac.equals("Cut")){
			frame.moveTiles(false);
		}
		else if(ac.equals("Copy")){
			frame.moveTiles(true);
		}
		else if(ac.equals("Paste")){
			frame.pasteTiles();
		}
		//view
		else if(ac.equals("Gridlines")){
			frame.invertGridlines();
		}
		else if(ac.equals("Show Markings")){
			frame.invertMarkings();
		}
		//tile
		else if(ac.equals("All blocked")){
			frame.setBlockedArray(new boolean[]{true, true, true, true});
		}
		else if(ac.equals("All open")){
			frame.setBlockedArray(new boolean[]{false, false, false, false});
		}
		else if(ac.equals("Custom block")){
			//reset current selection
			blockedDialog.setDirections(new boolean[]{false, false, false, false});
			blockedDialog.setVisible(true);
			//if user hit enter, set it
			if(blockedDialog.consumeSuccess()){
				frame.setBlockedArray(blockedDialog.getDirections());
			}
		}
		else if(ac.equals("Apply Config")){
			frame.fillBlocked();
		}
		else if(ac.equals("Clear Selection")){
			frame.clearSelection();
		}
		else if(ac.equals("Fill foreground")){
			String foreString = JOptionPane.showInputDialog(frame, "Enter a foreground id: ");
			try{
				int fore = Integer.parseInt(foreString);
				//valid fill
				if(fore < TileSet.currentSet().getNumTiles())
					frame.fillForeground(fore);
			}
			catch(NumberFormatException nfe){
				//do nothing - failed
			}
		}
		else if(ac.equals("Fill")){
			int id = showInputDialog(frame, "Enter ID to fill selection with: ");
			frame.fillSelection(id);
		}
		else if(ac.equals("Delete")){
			frame.fillSelection(-1);
		}
		else{
			//zoom
			if(ac.equals("Board in")){
				frame.zoomBoard(1);
			}
			else if(ac.equals("Board out")){
				frame.zoomBoard(-1);
			}
			else if(ac.equals("Area in")){
				frame.zoomArea(1);
			}
			else if(ac.equals("Area out")){
				frame.zoomArea(-1);
			}
		}
	}
	private int showInputDialog(BoardFrame frame, String message){
		String ui = JOptionPane.showInputDialog(frame, message);
		try{
			return Integer.parseInt(ui);
		}
		catch(Exception e){
		}
		return -1;
	}
}
