package com.lostagain.nl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.SSSGenericFileManager;
import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.darkflame.client.semantic.SSSIndex;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.me.LocationGUI.LocationsHub;
import com.lostagain.nl.me.LocationGUI.LocationScreen;
import com.lostagain.nl.me.gui.Old_Inventory;
import com.lostagain.nl.uti.FileManager;
import com.lostagain.nl.uti.SpiffyGenericTween;
import com.lostagain.nl.uti.SpiffyTweenConstructor;

/**
 * Mesh Explorer. An open source, distributed game of deduction and exploration.
 * Powered by SuperSimpleSemantics
 * 
 * Home class will manage game setup and provide convenience shortcuts for major game
 * classes and variables.
 * 
 * @author Thomas Wrobel (at least, the first author....)
 * 
 * **/
public class ME extends Game {

	final static String logstag = "ME";
	
//	static Logger Log = Logger.getLogger("ME");
	
	//semantics
	public final static String INTERNALNS = "http://darkflame.co.uk/meshexplorer#";		
	public final static HashSet<String> knownDatabases = new HashSet<String>();
	       

    //global game stuff
	static ME game;
    static public Old_Inventory playersInventory;   

	static public SpriteBatch batch;
    public static BitmapFont font;  
    static MainMenuScreen menu;
    
	 
    public void create() {
    	
    	game=this;
    	
    	
    	//when we  figure out how to use bitmap fonts in ui elements, we use the following
    	
    	font = new BitmapFont();//Gdx.files.internal("data/dfield.fnt"), new TextureRegion(texture), false);
    	
    //	font = new BitmapFont();
    	batch = new SpriteBatch();
    	
    	Gdx.app.setLogLevel(Application.LOG_INFO);
    	Gdx.app.log(logstag, "loading..");
    	
    	//create styles
    	DefaultStyles.setupStyles();

    	Gdx.app.log(logstag,"____");
    	//create inventory
    	playersInventory = new Old_Inventory();

    	Gdx.app.log(logstag,"________");
    	//we clear the semantics before adding the player data (because that contains semantics
    	SuperSimpleSemantics.clearAllIndexsAndNodes();

    	//create starting computer
	  	PlayersData.setup();
    	
    	Gdx.app.log(logstag,"_____________"); //stops after this in html version
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
    //Log.setLevel(Level.OFF);
    	  Logger.getLogger("sss");
    	  Logger.getLogger("sss.DemoKnowledgeBase").setLevel(Level.OFF);
    	//  Logger.getLogger("sss.SSSNodesWithCommonProperty").setLevel(Level.WARNING);
    	  Logger.getLogger("sss.DemoKnowledgeBase").setLevel(Level.OFF);
    	  Logger.getLogger("sss.SSSNode").setLevel(Level.WARNING);
    	  Logger.getLogger("sss.QueryEngine").setLevel(Level.OFF);
    	  Logger.getLogger("sss.JavaFileManager").setLevel(Level.OFF);    		  
    //	  Logger.getLogger("sss.SSSIndex").setLevel(Level.OFF);
    	  
    		  
    	  SuperSimpleSemantics.setFileManager(new FileManager());	  
    	  
    	  SuperSimpleSemantics.setAutoloadLabels(true);
    	  SuperSimpleSemantics.setPreloadIndexs(true);
    	
    	  	  
    	  SuperSimpleSemantics.setup();
    	  
    	  SuperSimpleSemantics.setLoadedRunnable(new Runnable() {			
    			@Override
    			public void run() {

    	        	Gdx.app.log(logstag,"_____________loaded ___");
    	        	
    		        //Use LibGDX's default Arial font.
    		      //  
    		        

    	        	ME.startgame();
    	        	
    	        	
    	        	final DoSomethingWithNodesRunnable display = new DoSomethingWithNodesRunnable(){

    	        		
						@Override
						public void run(ArrayList<SSSNode> newnodes,
								boolean invert) {
							
							Gdx.app.log(logstag,"_____got results:______"+newnodes.size());

							Gdx.app.log(logstag,"_____results:______"+newnodes.toString());
																					
		    	        	
		    	        	for (SSSNode sssNode : newnodes) {
		    	        		
								Gdx.app.log(logstag,"________result="+sssNode.PURI);
								
							}
							
						}
    	        		
    	        	};
    	        /*
    	        	Timer.schedule(new Task(){

						@Override
						public void run() {

						  	/*
		    	        	Gdx.app.log(logstag,"_____________running tests___");
							
							SSSNode greennode  = SSSNode.getNodeByLabel("green");
		    	        	Gdx.app.log(logstag,"_______g_______"+greennode.getEquivilentsAsString());
		    	        	Gdx.app.log(logstag,"_______g_______"+greennode.getPURI());
		    	        	
		    	        	SSSNode ColorNode = SSSNode.getNodeByLabel("color");
		    	        	
		    	        	
		    	        	Gdx.app.log(logstag,"______f_______|_"+SSSNode.getNodeByLabel("green").PURI);
		    	        	Gdx.app.log(logstag,"______f_______|_"+ColorNode.PURI);
		    	        	

		    				ArrayList<SSSNode> allSecuredPCs = SSSNodesWithCommonProperty.getAllNodesWithPredicate(StaticSSSNodes.SecuredBy);

		    				for (SSSNode set : allSecuredPCs) {
		    	        		
								Gdx.app.log(logstag,"________result="+set.getPURI());
								
							}

							
		    	        	final Query test = Query.createQuerySafely("fruit color=green");
*/
		    	        	//SSSNodesWithCommonProperty.getAllNodesWithProperty(ColorNode, greennode, display, null);
		    	        	/*
		    	        	HashSet<SSSNodesWithCommonProperty> sets = SSSNodesWithCommonProperty.getCommonPropertySetsContaining("http://darkflame.co.uk/semantics/darksnet.ntlist#pear");
		    	        	Gdx.app.log(logstag,"_______result s="+sets.size());
		    	        	
		    	        	for (SSSNodesWithCommonProperty set : sets) {
		    	        		
								Gdx.app.log(logstag,"____Pear____result="+set.getCommonPrec().getPURI()+":"+set.getCommonValue().getPURI());
								
							}

		    	        	SSSNode gma = SSSNode.getNodeByLabel("Pair");
		    	        	
		    	        	sets = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(gma.PURI);
		    	        	
		    	        	 Gdx.app.log(logstag,"______result s="+sets.size());
		    	        	for (SSSNodesWithCommonProperty set : sets) {
		    	        		
								Gdx.app.log(logstag,"____Pair____result="+set.getCommonPrec().getPURI()+":"+set.getCommonValue().getPURI());
								
							}
		    	        	
		    	        	//NOTE: http://www.darkflame.co.uk/semantics/darksnet.ntlist#green
		    	        	// THE WWW is wrong.
		    	        	//should it be stripped?
		    	        	
		    	        	
		    	        	//results:______[granny smith apple, gobblygook, granny smith apple, pair, leafs, gobblygook, grape, coriader, spinach, leafs, grass, pear, grass, spinach, coriader, avacado]
		    	        	//              [tomato, pair, banana, apple, apple, grape, banana, fruit, orange, pear, strewbury, strewbury, tomato, orange, avacado]
		    	        	//______[grape, pear, avacado]
		    	        	
		    	        	
		    	        	
		    	        	//final Query test = Query.createQuerySafely("fruit (color=green)");
		    	        	
		    	        	// AND http://dbpedia.org/ontology/colour<~semantics\TomsNetwork.ntlist#green
		    	        	//Gdx.app.log(logstag,"______________"+test.getAsString());
		    	        	//Gdx.app.log(logstag,"______________"+test.hasNoErrors());
		    	        	
							//QueryEngine.processQuery(test, false, null, display);
						}
    	        		
    	        	}, 3);
    	  */
    	        	
    	        	
    			}
    		});
    	  

    	  final ArrayList<String> trustedIndexs = new  ArrayList<String>();  	  
    	  
    	
    	  String fullPathOfHomeOntology = SuperSimpleSemantics.fileManager.getAbsolutePath("semantics\\TomsNetwork.ntlist");
    	  
    	  trustedIndexs.add("semantics\\TomsNetwork.ntlist");    
 
    	  //its important to add the full path to the knowndatabases array
    	  //necause the SSS will automaticaly expand short urls and filepaths to
    	  //absolute when needed internally
    	  //Thus if theres a "is this loaded already?" comparison, we need
    	  //to check full path against full path
    	  Gdx.app.log(logstag,"______fullPathOfHomeOntology________"+fullPathOfHomeOntology);
    	  knownDatabases.add(fullPathOfHomeOntology);

      	//  trustedIndexs.add("http://darkflame.co.uk/semantics/darksnet.ntlist"); //testing 
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

/**
 * checks for unloaded databasess this uri could be part of and starts loading if needed
 * 
 * @param linksToThisPC
 * @return
 */
public static Boolean checkForUnloadedDatabaseAndLoad(SSSNode linksToThisPC) {
	
	
	String label = linksToThisPC.getPURI();

	Gdx.app.log(logstag,"testing uri:"+label);
	
	if (label.contains(".ntlist#")){
	
		Gdx.app.log(logstag,"detected database");
		String databaseurl = label.substring(0, label.indexOf("#"));
		
		Gdx.app.log(logstag,"database url:"+databaseurl);
		Gdx.app.log(logstag,"databases known:"+knownDatabases);
		
		
		//test if already loaded
		if (!knownDatabases.contains(databaseurl)){

			Gdx.app.log(logstag,"_____________________database not loaded:");
			
			SuperSimpleSemantics.loadIndexAt(databaseurl);
			
			//we add it straight away before its loaded as we dont want to start loading it again
			//Note; we should have a seperate list for "currently loading" in case another link to the same database is come accross
			//while this one is still loading. Then that new runWhenDone should also wait for the same database to load rather then being enabled straight away
			knownDatabases.add(databaseurl);
			
			return true;
		}
		
		

	}
	
	
	return false;
}


/**
 * Will return false if its a database and the database is not loaded
 * 
 * Note; Currently does not just for a specific database thats ask. It tests if ALL *.ntlists are loaded.
 * This is because SSS does not (yet?) support individual database checking
 * 
 * @param linksToThisPC
 * @return
 */
public static boolean checkDatabaseIsLoaded(SSSNode linksToThisPC) {

	String label = linksToThisPC.getPURI();
	Gdx.app.log(logstag,"testing uri is loaded:"+label);	

	if (label.contains(".ntlist#")){
		
		String databaseurl = label.substring(0, label.indexOf("#"));
		Gdx.app.log(logstag,"database url:"+databaseurl);
		SSSIndex testThisIndex = SSSIndex.getIndex(databaseurl);

		if (testThisIndex!=null){
		
			if (SSSIndex.getGlobalIndexsLeftToLoad()==0){
				Gdx.app.log(logstag,"indexs should be all loaded true");
				return true;
			}else {
				Gdx.app.log(logstag,"indexs are not all loaded ");
				return false;
			}
			
		

		} else {
			Gdx.app.log(logstag,"no database with that url found");
			return false;
		}
		
		
	}
	Gdx.app.log(logstag,"not a database");
	return true;
}


//SSSIndex.getIndex(linksToThisPC.getPURI());

	
}
