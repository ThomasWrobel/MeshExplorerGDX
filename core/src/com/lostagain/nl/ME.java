package com.lostagain.nl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.SSSGenericFileManager;
import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.LocationGUI.DefaultStyles;
import com.lostagain.nl.LocationGUI.Inventory;
import com.lostagain.nl.LocationGUI.LocationContainer;
import com.lostagain.nl.LocationGUI.LocationScreen;
import com.lostagain.nl.temp.SpiffyGenericTween;
import com.lostagain.nl.temp.SpiffyTweenConstructor;
import com.lostagain.nl.uti.FileManager;

/**
 * Mesh Explorer. An open source, distributed game of deduction and exploration.
 * Powered by SuperSimpleSemantics
 * 
 * Home class will manage game setup and provide convience shortcuts for major game
 * classes and variables.
 * 
 * @author Thomas Wrobel (at least, the first author....)
 * 
 * **/
public class ME extends Game {

	static Logger Log = Logger.getLogger("ME");
	
	//semantics
	public final static String INTERNALNS = "http://darkflame.co.uk/meshexplorer#";		
	public final static HashSet<String> knownDatabases = new HashSet<String>();
	       

    //global game stuff
	static ME game;
    static public Inventory playersInventory;   

	static public SpriteBatch batch;
    public static BitmapFont font;  
    static MainMenuScreen menu;
    
	 
    public void create() {
    	
    	game=this;
    	font = new BitmapFont();
    	batch = new SpriteBatch();
    	
    	Log.info("loading..");
    	
    	//create styles
    	DefaultStyles.setupStyles();

    	Log.info("____");
    	//create inventory
    	playersInventory = new Inventory();

    	Log.info("________");
    	//we clear the semantics before adding the player data (because that contains semantics
    	SuperSimpleSemantics.clearAllIndexsAndNodes();

    	//create starting computer
	  	PlayersData.setup();
    	
    	Log.info("_____________");
     //   
    	menu= new MainMenuScreen(game);
    	game.setScreen(menu);

    	//setup semantics
        setupSemantics();

    	
        
    }

    /** setup the semantic database/processing which is the core engine for the games 
     * locations,puzzles and..well..everything **/
    public void setupSemantics()
    {
  	  //turn some logs off
    //	Log.setLevel(Level.OFF);
    	   Logger.getLogger("sss.DemoKnowledgeBase").setLevel(Level.OFF);
    	  Logger.getLogger("sss.SSSNodesWithCommonProperty").setLevel(Level.OFF);
    	  Logger.getLogger("sss.DemoKnowledgeBase").setLevel(Level.OFF);
    	  Logger.getLogger("sss.SSSNode").setLevel(Level.WARNING);
    	  Logger.getLogger("sss.QueryEngine").setLevel(Level.OFF);
    	 // Logger.getLogger("sss.JavaFileManager").setLevel(Level.OFF);    		  
    	  Logger.getLogger("sss.SSSIndex").setLevel(Level.WARNING);
    	  
    		  
    	  SuperSimpleSemantics.setFileManager(new FileManager());	  
    	  
    	  SuperSimpleSemantics.setAutoloadLabels(true);
    	  SuperSimpleSemantics.setPreloadIndexs(true);
    	
    	  	  
    	  SuperSimpleSemantics.setup();
    	  
    	  SuperSimpleSemantics.setLoadedRunnable(new Runnable() {			
    			@Override
    			public void run() {

    	        	Log.info("_____________loaded ___");
    	        	
    		        //Use LibGDX's default Arial font.
    		      //  
    		        

    	        	ME.startgame();
    	        	
    	        	
    	        	final DoSomethingWithNodesRunnable display = new DoSomethingWithNodesRunnable(){

    	        		
						@Override
						public void run(ArrayList<SSSNode> newnodes,
								boolean invert) {
							
							Log.info("_____got results:______"+newnodes.size());

							Log.info("_____results:______"+newnodes.toString());
																					
		    	        	
		    	        	for (SSSNode sssNode : newnodes) {
		    	        		
								Log.info("________result="+sssNode.PURI);
								
							}
							
						}
    	        		
    	        	};
    	        /*
    	        	Timer.schedule(new Task(){

						@Override
						public void run() {

						  	/*
		    	        	Log.info("_____________running tests___");
							
							SSSNode greennode  = SSSNode.getNodeByLabel("green");
		    	        	Log.info("_______g_______"+greennode.getEquivilentsAsString());
		    	        	Log.info("_______g_______"+greennode.getPURI());
		    	        	
		    	        	SSSNode ColorNode = SSSNode.getNodeByLabel("color");
		    	        	
		    	        	
		    	        	Log.info("______f_______|_"+SSSNode.getNodeByLabel("green").PURI);
		    	        	Log.info("______f_______|_"+ColorNode.PURI);
		    	        	

		    				ArrayList<SSSNode> allSecuredPCs = SSSNodesWithCommonProperty.getAllNodesWithPredicate(StaticSSSNodes.SecuredBy);

		    				for (SSSNode set : allSecuredPCs) {
		    	        		
								Log.info("________result="+set.getPURI());
								
							}

							
		    	        	final Query test = Query.createQuerySafely("fruit color=green");
*/
		    	        	//SSSNodesWithCommonProperty.getAllNodesWithProperty(ColorNode, greennode, display, null);
		    	        	/*
		    	        	HashSet<SSSNodesWithCommonProperty> sets = SSSNodesWithCommonProperty.getCommonPropertySetsContaining("http://darkflame.co.uk/semantics/darksnet.ntlist#pear");
		    	        	Log.info("_______result s="+sets.size());
		    	        	
		    	        	for (SSSNodesWithCommonProperty set : sets) {
		    	        		
								Log.info("____Pear____result="+set.getCommonPrec().getPURI()+":"+set.getCommonValue().getPURI());
								
							}

		    	        	SSSNode gma = SSSNode.getNodeByLabel("Pair");
		    	        	
		    	        	sets = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(gma.PURI);
		    	        	
		    	        	 Log.info("______result s="+sets.size());
		    	        	for (SSSNodesWithCommonProperty set : sets) {
		    	        		
								Log.info("____Pair____result="+set.getCommonPrec().getPURI()+":"+set.getCommonValue().getPURI());
								
							}
		    	        	
		    	        	//NOTE: http://www.darkflame.co.uk/semantics/darksnet.ntlist#green
		    	        	// THE WWW is wrong.
		    	        	//should it be stripped?
		    	        	
		    	        	
		    	        	//results:______[granny smith apple, gobblygook, granny smith apple, pair, leafs, gobblygook, grape, coriader, spinach, leafs, grass, pear, grass, spinach, coriader, avacado]
		    	        	//              [tomato, pair, banana, apple, apple, grape, banana, fruit, orange, pear, strewbury, strewbury, tomato, orange, avacado]
		    	        	//______[grape, pear, avacado]
		    	        	
		    	        	
		    	        	
		    	        	//final Query test = Query.createQuerySafely("fruit (color=green)");
		    	        	
		    	        	// AND http://dbpedia.org/ontology/colour<~semantics\TomsNetwork.ntlist#green
		    	        	//Log.info("______________"+test.getAsString());
		    	        	//Log.info("______________"+test.hasNoErrors());
		    	        	
							//QueryEngine.processQuery(test, false, null, display);
						}
    	        		
    	        	}, 3);
    	  */
    	        	
    	        	
    			}
    		});
    	  

    	  final ArrayList<String> trustedIndexs = new  ArrayList<String>();  	  
    	  
    	  trustedIndexs.add("semantics\\TomsNetwork.ntlist");    

    	//  trustedIndexs.add("http://darkflame.co.uk/semantics/darksnet.ntlist"); //testing  
    	  
    	  knownDatabases.add("semantics\\TomsNetwork.ntlist");

        //  knownDatabases.add("http://darkflame.co.uk/semantics/darksnet.ntlist");
    	  
    	  
      	  SuperSimpleSemantics.loadIndexsAt(trustedIndexs);
      	  
    }
    
    protected static void startgame() {
    	
    	 Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run() {

		    	 menu.start();
			}
    		
    	 });
    	 
    	
    	
	}

	public void render() {
        super.render(); //important!
        
    }

    public void dispose() {
     //   batch.dispose();
     //   font.dispose();
    }


public static Boolean checkForUnloadedDatabase(SSSNode linksToThisPC) {
	
	String label = linksToThisPC.getPURI();

	Log.info("testing uri:"+label);
	
	if (label.contains(".ntlist#")){
	
		Log.info("detected database");
		String databaseurl = label.substring(0, label.indexOf("#"));
		
		Log.info("database url:"+databaseurl);
		
		
		//test if already loaded
		if (!knownDatabases.contains(databaseurl)){

			Log.info("_____________________database not loaded:");
			SuperSimpleSemantics.loadIndexAt(databaseurl);
			
			knownDatabases.add(databaseurl);
			
			return true;
		}
		
		

	}
	
	
	return false;
}

	
}
