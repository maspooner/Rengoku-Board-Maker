package org.spooner.java.BoardMaker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class BoardDOM{
	//methods
	public static void writeXML(BoardData data, File path) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError{
		//create a new document
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		//create and append the root element
		doc.appendChild(doc.createElement("board"));
		//append a text node to make it look nicer
		doc.getDocumentElement().appendChild(newLine(doc));
		//build the tiles onto the document
		buildTiles(doc, data.getTiles());
		//build the data onto the doucment
		buildData(doc, data);
		//create the source object and stream result
		DOMSource source = new DOMSource(doc);
		StreamResult fileResult = new StreamResult(path);
		//use a transformer to write to file
		TransformerFactory.newInstance().newTransformer().transform(source, fileResult);
	}
	public static BoardData parseXML(String path) throws SAXException, IOException, ParserConfigurationException{
		//create a document to represent the XML document using DOM
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
		//get rid of text white space
		doc.getDocumentElement().normalize();
		//get a list of the lines
		NodeList lines = doc.getElementsByTagName("line");
		//find the number of columns by looking at the first line and counting the number of tile tags
		int columns = ((Element) lines.item(0)).getElementsByTagName("tile").getLength();
		//construct the tile matrix
		Tile[][] tileMatrix = new Tile[lines.getLength()][columns];
		//for every line
		for(int i = 0; i < lines.getLength(); i++){
			//find the line element
			Element lineElement = (Element) lines.item(i);
			//get a list of the tiles
			NodeList tiles = lineElement.getElementsByTagName("tile");
			//for every tile
			for(int j = 0; j < tiles.getLength(); j++){
				//parse the tile element
				tileMatrix[i][j] = parseTile((Element) tiles.item(j));
			}
		}
		int tileSet = Integer.parseInt(doc.getElementsByTagName("tile_set").item(0).getTextContent());
		//go through all remaining elements to find extra data
		NodeList allElements = doc.getDocumentElement().getChildNodes();
		ArrayList<Element> otherElements = new ArrayList<Element>();
		for(int i = 0; i < allElements.getLength(); i++){
			//only elements
			if(allElements.item(i) instanceof Element){
				Element e = (Element) allElements.item(i);
				//an element not accounted for yet
				if(!(e.getTagName().equals("line") || e.getTagName().equals("tile_set"))){
					otherElements.add(e);
				}
			}
		}
		//load the sets
		TileSet.loadSet(tileSet);
		return new BoardData(tileMatrix, otherElements);
	}
	private static void buildTiles(Document doc, Tile[][] tiles){
		Element root = doc.getDocumentElement();
		//for each line
		for(int i = 0; i < tiles.length; i++){
			//create and append a child to the root
			Element line = doc.createElement("line");
			root.appendChild(line);
			//for each tile
			for(int j = 0; j < tiles[i].length; j++){
				//append the tile element
				line.appendChild(createTileElement(doc, tiles[i][j]));
			}
			//append a text node to make it look nicer
			root.appendChild(newLine(doc));
		}
	}
	private static void buildData(Document doc, BoardData data){
		Element root = doc.getDocumentElement();
		//create and append the tileset
		Element tileSet = doc.createElement("tile_set");
		tileSet.appendChild(doc.createTextNode(Integer.toString(TileSet.currentSet().getID())));
		root.appendChild(tileSet);
		root.appendChild(newLine(doc));
		if(data.getOthers() != null){
			//create and append the others
			for(Element e : data.getOthers()){
				root.appendChild(doc.importNode(e, true));
				root.appendChild(newLine(doc));
			}
		}
	}
	private static Element createTileElement(Document doc, Tile tile){
		Element tileElement = doc.createElement("tile");
		//append the text node as the id
		tileElement.appendChild(doc.createTextNode(Integer.toString(tile.getID())));
		//if has sides blocked
		if(!tile.allUnblocked()){
			//give it an attribute for it
			tileElement.setAttribute("blocked", createBlockedString(tile));
		}
		//a board change tile
		if(tile.getBoardChange() != null){
			//give it an attribute for it
			tileElement.setAttribute("bid", tile.getBoardChange());
			//and for the start point
			tileElement.setAttribute("spid", Integer.toString(tile.getStartID()));
			//and for the face
			tileElement.setAttribute("face", Integer.toString(tile.getFace()));
		}
		//if the foreground
		if(tile.getForeID() >= 0){
			//give it an attribute for it
			tileElement.setAttribute("fore", Integer.toString(tile.getForeID()));
		}
		//if an item tile
		if(tile.getAfter() >= 0){
			//give attributes for them
			//give it an attribute for it
			tileElement.setAttribute("after", Integer.toString(tile.getAfter()));
			//and for the start point
			tileElement.setAttribute("item", Integer.toString(tile.getItem()));
			//and for the face
			tileElement.setAttribute("fire", Integer.toString(tile.getFire()));
		}
		return tileElement;
	}
	private static String createBlockedString(Tile t){
		String blocked = "";
		for(boolean bool : t.getBlockedArray()){
			//b if blocked, o if open
			blocked += bool ? "b" : "o";
		}
		return blocked;
	}
	private static Tile parseTile(Element tileElement){
		//tile id attribute
		int tileID = Integer.parseInt(tileElement.getTextContent());
		//blocked attribute, false array if not present
		boolean[] blocked = parseBlocked(tileElement.getAttribute("blocked"));
		//board id attribute
		String boardID = tileElement.getAttribute("bid");
		//set to null if not existent
		if(boardID.isEmpty()) boardID = null;
		//start id attribute
		String startIDstr = tileElement.getAttribute("spid");
		//see if start id is there
		int startID = startIDstr.isEmpty() ? -1 : Integer.parseInt(startIDstr);
		//face attribute
		String faceAttr = tileElement.getAttribute("face");
		//see if face is there
		int face = faceAttr.isEmpty() ? 0 : Integer.parseInt(faceAttr);
		//foreground attribute
		String foreAttr = tileElement.getAttribute("fore");
		//see if fore is there
		int fore = foreAttr.isEmpty() ? -1 : Integer.parseInt(foreAttr);
		//after attribute
		String afterAttr = tileElement.getAttribute("after");
		//see if after is there
		int after = afterAttr.isEmpty() ? -1 : Integer.parseInt(afterAttr);
		//item attribute
		String itemAttr = tileElement.getAttribute("item");
		//see if item is there
		int item = itemAttr.isEmpty() ? -1 : Integer.parseInt(itemAttr);
		//fire attribute
		String fireAttr = tileElement.getAttribute("fire");
		//see if fire is there
		int fire = fireAttr.isEmpty() ? -1 : Integer.parseInt(fireAttr);
		return new Tile(tileID, blocked, boardID, startID, face, fore, after, item, fire);
	}
	private static Text newLine(Document doc){
		return doc.createTextNode("\n");
	}
	private static boolean[] parseBlocked(String blockedString){
		boolean[] blocked = new boolean[4];
		for(int i = 0; i < blocked.length && !blockedString.isEmpty(); i++){
			//side blocked if a c at the index
			blocked[i] = blockedString.charAt(i) == 'b';
		}
		return blocked;
	}
}
