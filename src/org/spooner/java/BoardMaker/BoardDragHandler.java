package org.spooner.java.BoardMaker;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public class BoardDragHandler extends TransferHandler{
	//methods
	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}
	@Override
	protected Transferable createTransferable(JComponent c) {
		TileAreaTile tat=(TileAreaTile) c;
		//pack id into string
		return new StringSelection(Integer.toString(tat.getID()));
	}
	@Override
	public boolean canImport(TransferSupport sup) {
		//must be board
		if(!(sup.getComponent() instanceof Board)) return false;
		//must be drop
		if(!sup.isDrop()) return false;
		return true;
	}
	@Override
	public boolean importData(TransferSupport sup) {
		if(!canImport(sup)) return false;
		Transferable trans = sup.getTransferable();
		DropLocation dl = sup.getDropLocation();
		int id = -1;
		try{
			String data = (String) trans.getTransferData(DataFlavor.stringFlavor);
			id = Integer.parseInt(data);
			Board b = (Board) sup.getComponent();
			Point tileDrop = b.getFinePoint(dl.getDropPoint());
			//only if valid
			if(tileDrop != null){
				b.getData().setID(tileDrop, id);
				b.repaint();
			}
			return true;
		}catch(Exception e){
			BoardExceptionHandler.handle(6, e);
			return false;
		}
	}
}
