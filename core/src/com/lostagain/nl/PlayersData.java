package com.lostagain.nl;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.LocationGUI.LocationContainer;

public class PlayersData {

	static Logger Log = Logger.getLogger("ME.PlayersData")
			;
	public static SSSNode computersuri = SSSNode.createSSSNode("HomeMachine","HomeMachine",ME.INTERNALNS,new SSSNode[]{StaticSSSNodes.Computer});
			
	 //default software contents
	static SSSNode coin = SSSNode.createSSSNode("coin",ME.INTERNALNS+"coin", ME.INTERNALNS, new SSSNode[]{StaticSSSNodes.software});

	 //default message contents
	static SSSNode homemessage = SSSNode.createSSSNode("homepc/WelcomeMessage.txt","homepc/WelcomeMessage.txt", ME.INTERNALNS, new SSSNode[]{StaticSSSNodes.messages});
	
	//stores the computers description
	static SSSNodesWithCommonProperty homediscription = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.DescriptionOf, computersuri);
	
	
	//make a new common property set to store what this pc has unlocked	
	static SSSNodesWithCommonProperty playersunlocks = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.UnlockedBy, computersuri);
	
	//and what is currently on this pc
	public static SSSNodesWithCommonProperty playerslocationcontents = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.isOn, computersuri);

	/** the home location container **/
	public static LocationContainer homeLoc;

	/** mains a list of all the nodes of visited locations **/
	private static ArrayList<SSSNode> hasVisitedArray = new  ArrayList<SSSNode>();
	
	

	/** mains a list of all the nodes of all known languages **/
	private static ArrayList<SSSNode> hasLanguage = new  ArrayList<SSSNode>();
	
	
	
	/** should be run whenever a player unlocks a location,
	 * or if a location had no security **/
	public static void addUnlockedLink(SSSNode linkuri){
		playersunlocks.addNodeToThisSet(linkuri, "local");				
	}
	

	/** tests if a link is already unlocked **/
	public static Boolean isLinkUnlockedByPlayer(SSSNode linkuri){		
		return playersunlocks.containsNode(linkuri);		
	}


	public static void setup() {
		
		
		
		//This perhaps should be turned into a NTList to keep the players starting information external
		// would make editing easier
		
		// add starting objects
	//	playerslocationcontents.addNodeToThisSet(coin, "local");
		//Log.info("adding coin");
		
		//ME.playersInventory.addItem(coin);
		
		//add starting messages
		//adding label

		SSSNode homeDisLabel = SSSNode.createSSSNode("Something bob gave me a copy of. Hope he wont get in trouble for it.","HomeMachineDiscription",ME.INTERNALNS);
		homediscription.add(homeDisLabel);
		
		Log.info("adding message");
		
		playerslocationcontents.addNodeToThisSet(homemessage, "local");
		
		
		HashSet<SSSNodesWithCommonProperty> sets = 	SSSNodesWithCommonProperty.getCommonPropertySetsContaining(PlayersData.homemessage.PURI);

		Log.info("________message="+PlayersData.homemessage.PURI);
		
		Log.info("sets with homemessage:"+sets.size());
		//
		
		Log.info("players contents:"+PlayersData.playerslocationcontents.getCommonPrec().PURI+":"+PlayersData.playerslocationcontents.getCommonValue().PURI);
		
		for (SSSNode set : PlayersData.playerslocationcontents) {
    		
			Log.info("____homemessagep____result="+set.PURI );
			
		}
		
		
		hasLanguage.add(StaticSSSNodes.stdascii);
		
		
	}


	public static Boolean playerHas(SSSNode objectsnode) {
		
		return playerslocationcontents.containsNode(objectsnode);
	}


	
	public static boolean hasVisited(SSSNode locationsNode) {
		
		return hasVisitedArray.contains(locationsNode);
		
	} 	
	

	public static void addToVisitedLocations(SSSNode locationsNode) {
		hasVisitedArray.add(locationsNode);
		return; 
		
	}


	public static boolean knownsLanguage(SSSNode language) {
		
		return hasLanguage.contains(language);
		
	} 	
	
	
}
