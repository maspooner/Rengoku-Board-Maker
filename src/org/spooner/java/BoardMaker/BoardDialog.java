package org.spooner.java.BoardMaker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class BoardDialog extends JDialog implements ActionListener{
	//members
	private static final String DEFAULT_TILESET_TEXT = "[No tile set selected]";
	
	private File tileSetFile;
	private int width;
	private int height;
	private boolean success=false;
	
	private JLabel tileSetLabel;
	private JLabel dimensionLabel;
	private JLabel byLabel;
	private JTextField widthField;
	private JTextField heightField;
	private JButton browseButton;
	private JButton okButton;
	private JButton cancelButton;
	//constructors
	public BoardDialog(){
		setTitle("New Board");
		setModal(true);
		setSize(BoardConstants.DIALOG_SIZE);
		setResizable(false);
		setLayout(new GridBagLayout());
		
		tileSetLabel=new JLabel();
		dimensionLabel=new JLabel("Dimensions: ");
		byLabel=new JLabel("by");
		widthField=new JTextField();
		heightField=new JTextField();
		browseButton=new JButton("Browse...");
		browseButton.setActionCommand("b");
		browseButton.addActionListener(this);
		okButton=new JButton("OK");
		okButton.setActionCommand("o");
		okButton.addActionListener(this);
		cancelButton=new JButton("Cancel");
		cancelButton.setActionCommand("c");
		cancelButton.addActionListener(this);
		//also sets inital values
		clearDialog();
		
		arrangeComponents();
	}
	//methods
	private void arrangeComponents(){
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		c.weightx=0.01;
		c.weighty=0.01;
		c.fill=GridBagConstraints.BOTH;
		add(tileSetLabel, c);
		c.gridx=1;
		add(browseButton, c);
		c.gridx=0;
		c.gridy=1;
		add(dimensionLabel, c);
		c.gridx=1;
		add(widthField, c);
		c.gridy=2;
		add(byLabel, c);
		c.gridy=3;
		add(heightField, c);
		c.gridx=0;
		c.gridy=4;
		add(okButton, c);
		c.gridx=1;
		add(cancelButton, c);
	}
	private void clearDialog(){
		tileSetFile=null;
		width=-1;
		height=-1;
		
		tileSetLabel.setText(DEFAULT_TILESET_TEXT);
		widthField.setText("");
		heightField.setText("");
	}
	private void showDialog(String message, int messageType){JOptionPane.showMessageDialog(this, message, "Message", messageType);}
	public void display(){
		//display with no fields filled in
		success=false;
		//clear fields
		clearDialog();
		
		setVisible(true);
	}
	public void display(int width, int height){
		TileSet curr = TileSet.currentSet();
		success=false;
		tileSetFile=new File(curr.getPath());
		this.width=width;
		this.height=height;
		
		tileSetLabel.setText(curr.getPath());
		widthField.setText(Integer.toString(width));
		heightField.setText(Integer.toString(height));
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		String ac=ae.getActionCommand();
		if(ac.equals("o")){
			//ok
			//check for good content
			if(tileSetFile==null){
				showDialog("Choose a tile set source file.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(!tileSetFile.isFile()){
				showDialog("Tile set source file could not be read.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(!BoardIO.isValidSize(tileSetFile)){
				showDialog("Not a valid tile set size.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try{
				width=Integer.parseInt(widthField.getText());
				height=Integer.parseInt(heightField.getText());
				if(width<2 || height<2)
					throw new Exception();
			}
			catch(Exception e){
				showDialog("Invalid dimension parameters.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			success=true;
		}
		else if(ac.equals("c")){
			//cancel
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		else{
			//browse
			File f = TileFileChooser.loadTileSetFile(this);
			//if the user choose nothing
			if(f == null) return;
			tileSetFile = f;
			tileSetLabel.setText(tileSetFile.getName());
		}
	}
	public boolean wasSuccess(){
		//upon checking this method, success is set to false for next dialog showing
		boolean success = this.success;
		this.success = false;
		return success;
	}
	public int getTileSetID(){
		String fileName = tileSetFile.getName();
		//chop off .png
		int id = Integer.parseInt(fileName.substring(0, fileName.length() - 4));
		return id;
	}
	public int getTileWidth(){return width;}
	public int getTileHeight(){return height;}
}
