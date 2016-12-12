package org.spooner.java.BoardMaker;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class BoardFrame extends JFrame implements Runnable{
	//static members
	protected static final boolean DEFAULT_STATE = false;
	//members
	private BoardBar boardBar;
	private JSplitPane splitPane;
	private TileArea tileArea;
	private Board board;
	private Thread updater;
	private BoardDialog dialog;
	private boolean isGridlines;
	private boolean markingsShown;
	private int boardZoom;
	private int areaZoom;
	private boolean[] blockedState;
	
	private File currentProject;
	private RequestState requestState;
	//constructors
	public BoardFrame(final String title){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle(title);
				setDefaultCloseOperation(EXIT_ON_CLOSE);
				setSize(BoardConstants.DEFAULT_SIZE);
				setMinimumSize(BoardConstants.DEFAULT_SIZE);
				
				initComponents();
				setVisible(true);
				splitPane.setDividerLocation(0.3);
				
			}
		});
		isGridlines = DEFAULT_STATE;
		markingsShown = DEFAULT_STATE;
		boardZoom = BoardConstants.DEFAULT_ZOOM;
		areaZoom = BoardConstants.DEFAULT_ZOOM;
		requestState=RequestState.NONE;
		//set to all open
		blockedState = new boolean[]{false, false, false, false};
		updater = new Thread(this);
		updater.start();
		currentProject=null;
	}
	//methods
	private void initComponents(){
		boardBar=new BoardBar();
		//create dialog singleton
		new BoardDialog();
		setJMenuBar(boardBar);
		tileArea = new TileArea(this);
		board = new Board(this);
		addKeyListener(board);
		dialog = new BoardDialog();
		
		JScrollPane tileScroll=new JScrollPane(tileArea);
		JScrollPane boardScroll=new JScrollPane(board);
		//only vertical always there
		tileScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tileScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tileScroll.getVerticalScrollBar().setUnitIncrement(BoardConstants.SCROLL_INCREMENT);
		//both scroll bars always there
		boardScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		boardScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		boardScroll.getVerticalScrollBar().setUnitIncrement(BoardConstants.SCROLL_INCREMENT);
		//set content pane
		splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, tileScroll, boardScroll);
		splitPane.setResizeWeight(0.0); //make it so tile area size stays constant when resizing
		setContentPane(splitPane);
	}
	public int getBoardZoom(){ return boardZoom; }
	public int getAreaZoom(){ return areaZoom; }
	public boolean isGridlines(){ return isGridlines; }
	public boolean markingsShown(){ return markingsShown; }
	public boolean[] getBlockedArray(){ return blockedState; }
	private void notifyBoard(RequestState state){
		synchronized(board){
			board.notify(state);
		}
	}
	private void notifyArea(RequestState state){
		synchronized(tileArea){
			tileArea.notify(state);
		}
	}
	public synchronized void notify(RequestState state){
		requestState = state;
		notify();
	}
	private boolean fixEmptyRegions(){
		String ans = JOptionPane.showInputDialog(this, "Your board contains empty tile spaces. Select a Tile ID to fill these with:");
		try{
			int id = Integer.parseInt(ans);
			if(id < 0 || id >= TileSet.currentSet().getNumTiles())
				throw new Exception();
			board.fillNulls(id);
			return true;
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(this, "Did not save your board.", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}
	public void setBlockedArray(boolean[] blocked){ this.blockedState = blocked; }
	public void invertGridlines(){
		isGridlines = !isGridlines;
		//repaint frame
		repaint();
	}
	public void invertMarkings(){
		markingsShown = !markingsShown;
		//repaint frame
		repaint();
	}
	public void zoomBoard(int zoom){
		int newZoom = boardZoom + zoom;
		//if in range
		if(newZoom >= BoardConstants.ZOOM_MIN && newZoom <= BoardConstants.ZOOM_MAX){
			//do it
			boardZoom += zoom;
		}
		//notify the board
		notifyBoard(RequestState.RESIZE);
	}
	public void zoomArea(int zoom){
		int newZoom = areaZoom + zoom;
		//if in range
		if(newZoom >= BoardConstants.ZOOM_MIN && newZoom <= BoardConstants.ZOOM_MAX){
			//do it
			areaZoom += zoom;
		}
		//notify the area
		notifyArea(RequestState.RESIZE);
	}
	public void displayDialog(boolean isNew){
		if(isNew)
			dialog.display();
		else if(!board.isPresent())
			return;
		else
			dialog.display(board.getData().getTileWidth(), board.getData().getTileHeight());
	}
	public boolean wasDialogSuccess(){ return dialog.wasSuccess(); }
	public void clearSelection(){board.clearSelection();}
	public void fillSelection(int id){ board.fill(id, null, -20); }
	public void fillBlocked(){ board.fill(-20, blockedState, -20); }
	public void fillForeground(int foreID){ board.fill(-20, null, foreID); }
	public void moveTiles(boolean isCopy){ board.rememberTiles(isCopy); }
	public void pasteTiles(){ board.pasteTiles(); }
	@Override
	public void run() {
		while(true){
			synchronized(this){
				try{
					//wait until save/load
					wait();
					switch(requestState){
					case NONE: break;
					case CLOSE:
						currentProject=null;
						//same as resize
					case RESIZE:
						notifyBoard(requestState);
						notifyArea(requestState);
						break;
					case EDIT:
						//load the set
						TileSet.loadSet(dialog.getTileSetID());
						//edit the board
						board.editBoard(dialog.getTileWidth(), dialog.getTileHeight());
						notifyArea(requestState);
						break;
					case NEW:
						//load the set
						TileSet.loadSet(dialog.getTileSetID());
						//create a new board
						board.createBlankBoard(dialog.getTileWidth(), dialog.getTileHeight());
						notifyArea(requestState);
						break;
					case LOAD:
						File f = TileFileChooser.loadBoardFile(this);
						//if the user choose nothing
						if(f == null) continue;
						currentProject = f;
						//load board into data
						BoardData data = BoardDOM.parseXML(TileFileChooser.getLastBoardLoad());
						//no need to notify board
						board.loadBoard(data);
						notifyArea(RequestState.LOAD);
						break;
					case SAVE_AS:
						//same as save, set project to null
						currentProject=null;
					case SAVE:
						//must have tiles (no empty boards)
						if(!board.isPresent()) continue;
						//TODO bug: if save as, cancels, then press save, currentProject=null -> tileFileChooser shown
						if(currentProject==null){
							//new project, needs save directory
							File i=TileFileChooser.saveBoardFile(this);
							//if the user choose nothing
							if(i==null) continue;
							currentProject=i;
						}
						//can save if no nulls
						boolean canSave = !board.getData().containsNulls();
						//give the user a chance to fix it
						if(!canSave && fixEmptyRegions()){
							canSave = true;
						}
						if(canSave){
							//save project
							BoardIO.save(board.getData(), currentProject);
						}
						break;
					}
				}catch (Exception e){
					BoardExceptionHandler.handle(4, e);
				}
			}
		}
	}
}
