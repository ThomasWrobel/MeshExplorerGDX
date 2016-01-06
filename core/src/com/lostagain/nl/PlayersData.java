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
import com.lostagain.nl.me.features.ConceptGunPanel;
import com.lostagain.nl.me.features.InventoryPanel;
import com.lostagain.nl.me.features.NewGUIBar;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.locationFeatures.LocationsHub_old;
import com.lostagain.nl.me.objects.DataObject;

public class PlayersData {


	final static String logstag = "ME.PlayersData";
	//static Logger Log = Logger.getLogger("ME.PlayersData")
	//		;
	public static SSSNode computersuri = SSSNode.createSSSNode("Home","Home",ME.INTERNALNS,new SSSNode[]{StaticSSSNodes.Computer});
			
	 //default software contents
//	static SSSNode coin = SSSNode.createSSSNode("coin",ME.INTERNALNS+"coin", ME.INTERNALNS, new SSSNode[]{StaticSSSNodes.software});

	 //default message contents semantics\TomsNetwork.ntlist
	static SSSNode homemessage1 = SSSNode.createSSSNode("\"homepc/WelcomeMessage.txt\"","semantics\\TomsNetwork.ntlist#welcomemessage", ME.INTERNALNS, new SSSNode[]{StaticSSSNodes.messages});
	static SSSNode homemessage2 = SSSNode.createSSSNode("\"homepc/WelcomeMessage2.txt\"","semantics\\TomsNetwork.ntlist#welcomemessage2", ME.INTERNALNS, new SSSNode[]{StaticSSSNodes.messages});
	
	public static SSSNodesWithCommonProperty isAttachedToMessage2 = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.isOn, homemessage2, new SSSNode[]{StaticSSSNodes.standardgui});
	
	
	 //stores the computers description
	static SSSNode homeDisLabel = SSSNode.createSSSNode("Some software Bob gave me a copy of. Hope he wont get in trouble for it.","HomeLocationDiscription",ME.INTERNALNS);
	
	static SSSNodesWithCommonProperty homediscription = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.DescriptionOf, computersuri, new SSSNode[]{homeDisLabel});
	
	
	//make a new common property set to store what this pc has unlocked	
	static SSSNodesWithCommonProperty playersunlocks = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.UnlockedBy, computersuri);
	
	//and what is currently on the players location (that is, all the players known nodes)
	public static SSSNodesWithCommonProperty playerslocationcontents = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.isOn, computersuri, new SSSNode[]{homemessage1,homemessage2,StaticSSSNodes.asciidecoder,StaticSSSNodes.prototype_scanner});
	
	/**
	 * All the things currently running on the players location
	 * Everything here should also be in playerslocationcontents but not visa-versa
	 */
	public static SSSNodesWithCommonProperty playersLocationActiveSoftware = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.isRunningOn, computersuri, new SSSNode[]{StaticSSSNodes.asciidecoder,StaticSSSNodes.prototype_scanner});
	
	
	/** the home location container **/
	public static Location homeLoc;
	
	//the location of the computer on the grid
	public static int homelocationX=0; //200
	public static int homelocationY=0;//500
	public static int homelocationZ=0;//Can't be anything else

	
	
	
	/** mains a list of all the nodes of visited locations **/
	private static HashSet<SSSNode> hasVisitedArray = new  HashSet<SSSNode>();

	
	/** mains a list of all the nodes of known/scanned locations (but not necessarily open) **/
	private static HashSet<SSSNode> locationScanned = new  HashSet<SSSNode>();
	
	
	
	/**
	 * Below is all the players panels and features
	 * These are collected over the game, and reloaded from save games
	 * Each one might have many possible parameters, which are determined by its precise SSSNode's propertys.
	 * ie.
	 * A basic inventory has 7 slots (Capacity:7)
	 * A expanded one has nine (Capcity:9)
	 * 
	 */
	public static InventoryPanel playersInventoryPanel;
	
	/**
	 * This panel controlls the fireing of the concept gun, its visual representation and its ammo slot
	 */
	public static ConceptGunPanel playersConceptGun;
	
	/**
	 * This panel provides shortcuts for other gui functions like the inventory,concept gun, or just going back to the last location
	 */
	public static NewGUIBar playersGUI;
	
	
	
	

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
		
		Gdx.app.log(logstag,"Home location uri="+homemessage1.PURI);
		
	
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
		
		
		HashSet<SSSNodesWithCommonProperty> sets = 	SSSNodesWithCommonProperty.getCommonPropertySetsContaining(PlayersData.homemessage1.PURI);

		Gdx.app.log(logstag,"________message="+PlayersData.homemessage1.PURI);
		
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
		if (GameMode.currentGameMode == GameMode.Developer ){
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

	/**
	 * Adds a item to the players location.
	 * This acts like the players database of concepts and abilitys 
	 * @param ability
	 * @param string
	 **/
	public static void addItemToDatabase(SSSNode ability, String source,boolean regeneratePlayersLocation) {
		PlayersData.playerslocationcontents.addNodeToThisSet(ability, source); 
		//update the location
		if (regeneratePlayersLocation){
		homeLoc.locationsNEWHub.reGenerateLocationContents();
		}
	}


	/**
	 * Remove a item from the players location.
	 * This acts like the players database of concepts and ability's 
	 * 
	 **/
	public static void removeItemFromDatabase(SSSNode node,boolean regeneratePlayersLocation) {
		PlayersData.playerslocationcontents.removeNodeFromThisSet(node);	
		//update the location 
		if (regeneratePlayersLocation){
		homeLoc.locationsNEWHub.reGenerateLocationContents();
		}
	} 	
	
	/**
	 * Sets the specified node as running. Node should be a bit of software, and on the players machine already.
	 * Existing running software of the same type should probably be removed before triggering this 
	 **/
	public static void addSoftwareAsRunning(SSSNode ability,boolean regeneratePlayersLocation) {
		PlayersData.playersLocationActiveSoftware.addNodeToThisSet(ability, "internal"); 
		//update the location
		if (regeneratePlayersLocation){
			homeLoc.locationsNEWHub.reGenerateLocationContents();
		}
	}
	
	/**
	 * Sets the specified node as running. Node should be a bit of software, and on the players machine already.
	 * Existing running software of the same type should probably be removed before triggering this.
	 * Note; this will fire a hub regeneration. This is currently inefficient if your doing this before a AddSoftware, as that does the same 
	 **/
	public static void removeSoftwareAsRunning(SSSNode ability,boolean regeneratePlayersLocation) {
		PlayersData.playersLocationActiveSoftware.removeNodeFromThisSet(ability); 
		//update the location
		if (regeneratePlayersLocation){
			homeLoc.locationsNEWHub.reGenerateLocationContents();
		}
	}
	
	
	
	public static SSSNodesWithCommonProperty getAllRunningSoftware(){
		return playersLocationActiveSoftware;
	}
	
	/**
	 * returns a list of all running software as a string
	 * (currently just a temp method to help the ability installer visualize whats running.
	 * In future the Ability Installer/Ability Store will have to be sorted to be neater
	 * 
	 */
	public static String getAllRunningSoftwareAsString(){
		String allSoftwareNames = "";
		for (SSSNode software : playersLocationActiveSoftware) {
			//add
			allSoftwareNames = allSoftwareNames + software.getPLabel()+" , ";
			
			
		}
		return allSoftwareNames;
		
	}
	
	
	public static void loadPlayersGame(String filename){
		
		//clear all existing player data
		//player data has a unique URI to identify where it came from.
		//we use this URI to remove it
		
		//simply load the players ntindex file to load its new data
		//ensuring we use the unique player URI as the source of this new information
		
		//tell the interface to refresh
			
		
		
		
	}
	
	/**
	 * save the players game to the requested filename/path
	 * @param filename
	 **/
	public static void savePlayersGame(String filename){
		
		//get all the common property sets that effect the player
		//and the players location
		
		
		
		
	}

	
	
}
