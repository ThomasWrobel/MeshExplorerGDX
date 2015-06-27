package com.lostagain.nl;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.ME.GameMode;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.locationFeatures.LocationsHub;
import com.lostagain.nl.me.objects.DataObject;

public class PlayersData {


	final static String logstag = "ME.PlayersData";
	//static Logger Log = Logger.getLogger("ME.PlayersData")
	//		;
	public static SSSNode computersuri = SSSNode.createSSSNode("HomeLocation","HomeLocation",ME.INTERNALNS,new SSSNode[]{StaticSSSNodes.Computer});
			
	 //default software contents
//	static SSSNode coin = SSSNode.createSSSNode("coin",ME.INTERNALNS+"coin", ME.INTERNALNS, new SSSNode[]{StaticSSSNodes.software});

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
	public static int homelocationX=0; //200
	public static int homelocationY=0;//500
	public static int homelocationZ=0;//cant be anything else

	
	
	
	/** mains a list of all the nodes of visited locations **/
	private static HashSet<SSSNode> hasVisitedArray = new  HashSet<SSSNode>();

	
	/** mains a list of all the nodes of known/scanned locations (but not necessarily open) **/
	private static HashSet<SSSNode> locationScanned = new  HashSet<SSSNode>();
	
	

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
		
		Gdx.app.log(logstag,"Home location uri="+homemessage.PURI);
		
	
		//This perhaps should be turned into a NTList to keep the players starting information external
		// would make editing easier
		
		// add starting objects
	//	playerslocationcontents.addNodeToThisSet(coin, "local");
		//Gdx.app.log(logstag,"adding coin");
		
		//ME.playersInventory.addItem(coin);
		
		//add starting messages
		//adding label

		//homediscription.add(homeDisLabel);
		
		Gdx.app.log(logstag,"adding message");
		
		//playerslocationcontents.addNodeToThisSet(homemessage, "local");
		
		
		HashSet<SSSNodesWithCommonProperty> sets = 	SSSNodesWithCommonProperty.getCommonPropertySetsContaining(PlayersData.homemessage.PURI);

		Gdx.app.log(logstag,"________message="+PlayersData.homemessage.PURI);
		
		Gdx.app.log(logstag,"sets with homemessage:"+sets.size());
		//
		
		Gdx.app.log(logstag,"players contents:"+PlayersData.playerslocationcontents.getCommonPrec().PURI+":"+PlayersData.playerslocationcontents.getCommonValue().PURI);
		
		for (SSSNode set : PlayersData.playerslocationcontents) {
    		
			Gdx.app.log(logstag,"____content____result="+set.PURI );

			Gdx.app.log(logstag,"____content____parents="+set.getDirectParentsAsString() );

			Gdx.app.log(logstag,"____content____parents="+set.getAllClassesThisBelongsToo() );
			
		}
		
		
		//hasLanguage.add(StaticSSSNodes.stdascii);
		//playerslocationcontents.addNodeToThisSet(StaticSSSNodes.asciidecoder, "local");
		//playerslocationcontents.addNodeToThisSet(StaticSSSNodes.scram1decoder, "local");
		
		
	//	SSSNode testInfovours = SSSNode.createSSSNode("smallInfovorPopulation","smallInfovorPopulation",ME.INTERNALNS);
		
		
		//BobsOutpost me:populatedBy smallInfovorPopulation.
		//SSSNodesWithCommonProperty homePop = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.populatedBy, testInfovours, new SSSNode[]{computersuri});
		
		//developers get some stuff automatically
		//wont work till inventory can add stuff like the gun automatically on first load
		if (ME.currentMode == GameMode.Developer ){
			//playerslocationcontents.add(StaticSSSNodes.ConceptGun1);
			
    	} 
		
		
		
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
	
	/** Determines if the player has already scanned this link or not **/
	public static boolean hasScanned(SSSNode locationsNode) {		
		return locationScanned.contains(locationsNode);		
	} 	
	
    /** Adds the node to the list of scanned locations. 
     * This prevents the need for the player to ever rescan links they have already **/
	public static void addToScannedLocations(SSSNode locationsNode) {
		locationScanned.add(locationsNode);
		return; 		
	}

	public static void knownsLanguage(SSSNode language,final Runnable firesIfTrue,final Runnable firesIfFalse) {
		
		//look for a decoder of the specified language
		//HashSet<SSSNode> AllKnownDecoders =  SSSNodesWithCommonProperty.getAllCurrentNodesInSetsFor(SSSNode.SubClassOf, StaticSSSNodes.decoder);
		
		//We look for a decoder thats on the players machine which knows this language
		//if necessary this stuff can be cached here to do it more efficiently.
		//but then, part of the point of this game is to test the SSS database system!
		
		//Decoder KnowsLanguage=language isOn=PlayersMachine
		
		
		Gdx.app.log(logstag,"______________testing if player has decoder for language "+language.getPURI());
		Gdx.app.log(logstag,"______________player should know language "+StaticSSSNodes.stdascii.getPURI());
		//ME.INTERNALNS+"knows="+language.getPURI() works
		// works
		//
		Query suitableDecodersOnPlayersSystemQuery = new Query(ME.INTERNALNS+"knows=\""+language.getPURI()+"\" "+ME.INTERNALNS+"decoder "+"me:isOn=\""+computersuri.getPURI()+"\"");
		
		if (suitableDecodersOnPlayersSystemQuery.hasNoErrors()){
			Gdx.app.log(logstag,"______no errors in query");
			
		}
		
		//some tests (no longer needed, had error in above query)
/*
		Gdx.app.log(logstag,"______________asciidecoder uri: "+StaticSSSNodes.asciidecoder.PURI);
		Gdx.app.log(logstag,"______________asciidecoder parents: "+StaticSSSNodes.asciidecoder.getAllClassesThisBelongsToo());
		
		HashSet<SSSNodesWithCommonProperty> sets = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(StaticSSSNodes.asciidecoder.getPURI());
		for (SSSNodesWithCommonProperty set : sets) 
		{

			Gdx.app.log(logstag,"______________asciidecoder has property: "+set.getCommonPrec()+"="+set.getCommonValue());	
				
		}
		
						
		Gdx.app.log(logstag,"_____________knows=ascii list is "+StaticSSSNodes.knowsAscii.getCommonPrec()+"="+StaticSSSNodes.knowsAscii.getCommonValue());	
		Gdx.app.log(logstag,"_____________should match val ="+language.getPURI()+" (and be in list following ::: below)");	
		
		Gdx.app.log(logstag,"_____________things with knows=ascii: "+StaticSSSNodes.knowsAscii.toString());	
		
		Gdx.app.log(logstag,"_____________ison=computer list is "+PlayersData.playerslocationcontents.getCommonPrec()+"="+PlayersData.playerslocationcontents.getCommonValue());	
		Gdx.app.log(logstag,"_____________should match val ="+computersuri.getPURI()+" (and be in list following ::: below)");	
		Gdx.app.log(logstag,"_____________things with  ison=computer: "+PlayersData.playerslocationcontents.toString());	
		
		//(Ascii decoder is in both lists....so, again, why doesn't the query pick it up? it should just be a intersection of these lists/
		//most likely problem; Its not interpreting the pred/val of these lists as the same ones requested
		//check that next
		
		Gdx.app.log(logstag,":::"+suitableDecodersOnPlayersSystemQuery.allUsedNodes().toString());
		
		*/
		

		DoSomethingWithNodesRunnable RunWhenDone = new DoSomethingWithNodesRunnable() {
			
			@Override
			public void run(ArrayList<SSSNode> newnodes, boolean invert) {
				
				Gdx.app.log(logstag,"got results from language test:"+newnodes.toString());
				if (newnodes!=null && newnodes.get(0)!=SSSNode.NOTFOUND){
					
					//language
					//http://darkflame.co.uk/meshexplorer#asciidecoder
										
					firesIfTrue.run();
					
				} else {
					firesIfFalse.run();
					
				}
				
			}
		};
		
		QueryEngine.processQuery(suitableDecodersOnPlayersSystemQuery, false, null, RunWhenDone);
		
		
		
		return;
		
	} 	
	
	
}
