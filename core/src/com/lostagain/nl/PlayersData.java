package com.lostagain.nl;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.me.LocationGUI.Location;
import com.lostagain.nl.me.LocationGUI.LocationsHub;

public class PlayersData {

	static Logger Log = Logger.getLogger("ME.PlayersData")
			;
	public static SSSNode computersuri = SSSNode.createSSSNode("HomeLocation","HomeLocation",ME.INTERNALNS,new SSSNode[]{StaticSSSNodes.Computer});
			
	 //default software contents
	static SSSNode coin = SSSNode.createSSSNode("coin",ME.INTERNALNS+"coin", ME.INTERNALNS, new SSSNode[]{StaticSSSNodes.software});

	 //default message contents semantics\TomsNetwork.ntlist
	static SSSNode homemessage = SSSNode.createSSSNode("\"homepc/WelcomeMessage.txt\"","semantics\\TomsNetwork.ntlist#welcomemessage", ME.INTERNALNS, new SSSNode[]{StaticSSSNodes.messages});
	
	 //stores the computers description
	static SSSNode homeDisLabel = SSSNode.createSSSNode("Something bob gave me a copy of. Hope he wont get in trouble for it.","HomeLocationDiscription",ME.INTERNALNS);
	
	static SSSNodesWithCommonProperty homediscription = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.DescriptionOf, computersuri, new SSSNode[]{homeDisLabel});
	
	
	//make a new common property set to store what this pc has unlocked	
	static SSSNodesWithCommonProperty playersunlocks = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.UnlockedBy, computersuri);
	
	//and what is currently on the players location
	public static SSSNodesWithCommonProperty playerslocationcontents = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.isOn, computersuri, new SSSNode[]{homemessage,StaticSSSNodes.asciidecoder,StaticSSSNodes.prototype_scanner});
	
	/** the home location container **/
	public static Location homeLoc;
	
	//the location of the computer on the grid
	public static int homelocationX=200;
	public static int homelocationY=500;
	

	/** mains a list of all the nodes of visited locations **/
	private static ArrayList<SSSNode> hasVisitedArray = new  ArrayList<SSSNode>();

	
	
	

	/** mains a list of all the nodes of all known languages **/
	//private static ArrayList<SSSNode> hasLanguage = new  ArrayList<SSSNode>();
	
	
	
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
		
		Log.info("home location uri="+homemessage.PURI);
		
		
		//This perhaps should be turned into a NTList to keep the players starting information external
		// would make editing easier
		
		// add starting objects
	//	playerslocationcontents.addNodeToThisSet(coin, "local");
		//Log.info("adding coin");
		
		//ME.playersInventory.addItem(coin);
		
		//add starting messages
		//adding label

		//homediscription.add(homeDisLabel);
		
		Log.info("adding message");
		
		//playerslocationcontents.addNodeToThisSet(homemessage, "local");
		
		
		HashSet<SSSNodesWithCommonProperty> sets = 	SSSNodesWithCommonProperty.getCommonPropertySetsContaining(PlayersData.homemessage.PURI);

		Log.info("________message="+PlayersData.homemessage.PURI);
		
		Log.info("sets with homemessage:"+sets.size());
		//
		
		Log.info("players contents:"+PlayersData.playerslocationcontents.getCommonPrec().PURI+":"+PlayersData.playerslocationcontents.getCommonValue().PURI);
		
		for (SSSNode set : PlayersData.playerslocationcontents) {
    		
			Log.info("____content____result="+set.PURI );

			Log.info("____content____parents="+set.getDirectParentsAsString() );

			Log.info("____content____parents="+set.getAllClassesThisBelongsToo() );
			
		}
		
		
		//hasLanguage.add(StaticSSSNodes.stdascii);
		
		

		//playerslocationcontents.addNodeToThisSet(StaticSSSNodes.asciidecoder, "local");
		//playerslocationcontents.addNodeToThisSet(StaticSSSNodes.scram1decoder, "local");
		
		
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


	public static void knownsLanguage(SSSNode language,final Runnable firesIfTrue,final Runnable firesIfFalse) {
		
		//look for a decoder of the specified language
		//HashSet<SSSNode> AllKnownDecoders =  SSSNodesWithCommonProperty.getAllCurrentNodesInSetsFor(SSSNode.SubClassOf, StaticSSSNodes.decoder);
		
		//We look for a decoder thats on the players machine which knows this language
		//if necessary this stuff can be cached here to do it more efficiently.
		//but then, part of the point of this game is to test the SSS database system!
		
		//Decoder KnowsLanguage=language isOn=PlayersMachine
		
		
		Log.info("______________testing if player has decoder for language");
		
		Query realQuery = new Query(ME.INTERNALNS+"knows="+language.getPURI() +" "+ME.INTERNALNS+"decoder me:isOn="+computersuri);
		
		if (realQuery.hasNoErrors()){
			Log.info("______no errors in query");
			
		}
		
		Log.info("______no errors in query");
		
		
		Log.info(":::"+realQuery.allUsedNodes().toString());
		
		
		

		DoSomethingWithNodesRunnable RunWhenDone = new DoSomethingWithNodesRunnable() {
			
			@Override
			public void run(ArrayList<SSSNode> newnodes, boolean invert) {
				
				Log.info("got results from language test:"+newnodes.toString());
				if (newnodes!=null && newnodes.get(0)!=SSSNode.NOTFOUND){
					
					//language
					//http://darkflame.co.uk/meshexplorer#asciidecoder
										
					firesIfTrue.run();
					
				} else {
					firesIfFalse.run();
					
				}
				
			}
		};
		
		QueryEngine.processQuery(realQuery, false, null, RunWhenDone);
		
		
		
		return;
		
	} 	
	
	
}
