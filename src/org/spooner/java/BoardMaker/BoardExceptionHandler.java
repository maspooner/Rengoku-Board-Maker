package org.spooner.java.BoardMaker;

import java.util.HashMap;

public class BoardExceptionHandler {
	//members
	private static HashMap<Integer, String> exceptionMap=new HashMap<Integer, String>();
	
	static{
		exceptionMap.put(1, "TileArea was interrupted during wait()");
		exceptionMap.put(2, "ImageIO could not load an image with the path given");
		exceptionMap.put(3, "Board was interrupted during wait()");
		exceptionMap.put(4, "BoardFrame was interrupted during wait()");
		exceptionMap.put(5, "Save was not successful");
		exceptionMap.put(6, "Drag & Drop transfer was invalid");
		exceptionMap.put(7, "[EMPTY LOT]");
		exceptionMap.put(8, "Load was not successful");
		exceptionMap.put(9, "Incorrect TileSet sizing (not divisible by TileSize)");
		exceptionMap.put(10, "Error locating TileSet when loading Board data");
		exceptionMap.put(11, "Export was unsucessful");
	}
	//methods
	public static void handle(int exceptionID, Exception e){
		System.err.println("!EXCEPTION!");
		for(int i=0;i<50;i++)
			System.err.print('=');
		System.err.println();
		String message;
		//if key not registered
		if(!exceptionMap.containsKey(exceptionID)){
			message="Unregistered Exception has occured";
		}
		else{
			message=exceptionMap.get(exceptionID);
		}
		StackTraceElement ste=e.getStackTrace()[0];
		//print the message
		System.err.println("Exception:\t"+message);
		System.err.println("*in class:\t"+ste.getFileName());
		System.err.println("*in method:\t"+ste.getMethodName());
		System.err.println("*Line number:\t"+ste.getLineNumber());
		System.err.println("*Exception Message: "+e.getMessage());
		e.printStackTrace();
		//exit the app
		System.exit(-1);
	}
}
