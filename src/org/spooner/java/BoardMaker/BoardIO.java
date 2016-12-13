package org.spooner.java.BoardMaker;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class BoardIO {
	//methods
	public static void save(BoardData data, File output){
		//save to xml
		if(output==null) return;
		try{
			BoardDOM.writeXML(data, output);
		}
		catch(Exception e){
			BoardExceptionHandler.handle(5, e);
		}
	}
	public static boolean isValidSize(File path){
		BufferedImage source=loadImage(path);
		int width=source.getWidth();
		int height=source.getHeight();
		//if not an accurate tile size
		if(width%BoardConstants.ORG_TILE_SIZE!=0 || height%BoardConstants.ORG_TILE_SIZE!=0){
			return false;
		}
		return true;
	}
	public static BufferedImage loadImage(File path){
		try {
			System.out.println(path.toString());
			return ImageIO.read(path);
		}catch(Exception e){
			BoardExceptionHandler.handle(2, e);
			return null;
		}
	}
}
