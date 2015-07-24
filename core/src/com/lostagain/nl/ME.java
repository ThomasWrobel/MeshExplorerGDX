package com.lostagain.nl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.darkflame.client.semantic.SSSIndex;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.me.creatures.Population;
import com.lostagain.nl.me.domain.MEDomain;
import com.lostagain.nl.me.features.ConceptObject;
import com.lostagain.nl.me.features.MeshIcon;
import com.lostagain.nl.me.gui.Inventory;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.objects.DataObject;
import com.lostagain.nl.uti.FileManager;

/**
 * Mesh Explorer. An open source, distributed game of deduction and exploration.
 * Powered by SuperSimpleSemantics
 * 
 * Home class will manage game setup and provide convenience shortcuts for major game
 * classes and variables. (in particularly moving from location to location)
 * 
 * It also will maintain a list of all the domain objects (MEDomain) that are currently loaded, creating
 * new ones when a new domain is opened for the first time (this happens at the same time as loading its index)
 * 
 * 
 * @author Thomas Wrobel (at least, the first author....)
 * 
 * **/
public class ME extends Game {

	final static String logstag = "ME";
		
	//semantics
	public final static String INTERNALNS = "http://darkflame.co.uk/meshexplorer#";	
	
    //global game stuff
	static ME game;
    static public Inventory playersInventory;   

	static public SpriteBatch interfaceSpriteBatch;
    public static BitmapFont font;  
    static MainMenuScreen menu;

	/** Used to tell if the player is at their home pc **/
	static boolean isAtHome=true;

	//purhapes eventually make this more fine grained by using a list of meshicons instead?
	public static  LinkedList<Location> LastLocation = new LinkedList<Location>();

	static Location currentTargetLocation;
	
	/** GameMode control's mode and params for each.
	 * Some global tweaks to the gameplay can be made here.
	 * For now its just speed of scans, which varies based on the currentgame mode**/
	public enum GameMode {		
		/** production mode turns debug logs off **/
		Production(20),
		/** logs on, normal scan speed **/
		Normal(20),
		/** logs on, speeds up scans (speed controlled in ScanManager)**/
		Developer(60);
		
		int ScanSpeed;		
		GameMode(int ScanSpeed){
			this.ScanSpeed=ScanSpeed;
		}
		
		public int getScanSpeed() {
			return ScanSpeed;
		}
		
	}
	
    public static final GameMode currentMode = GameMode.Normal;
	 
    @Override
	public void create() {
    	
    	//set up logging setting based on game mode
    	if (currentMode == GameMode.Production ){
    		Gdx.app.setLogLevel(Application.LOG_NONE);
    	} else {
    		Gdx.app.setLogLevel(Application.LOG_INFO);
    	}
    	
    	Gdx.app.log(logstag, "loading game..");
    	
    	game = this;
    	
    	
    	//when we  figure out how to use bitmap fonts in ui elements, we use the following
    	
    	font = new BitmapFont();//Gdx.files.internal("data/dfield.fnt"), new TextureRegion(texture), false);
    	
    //	font = new BitmapFont();
    	interfaceSpriteBatch = new SpriteBatch();

    	//create styles
    	DefaultStyles.setupStyles();

    	Gdx.app.log(logstag,"____");
    	//create inventory
    	playersInventory = new Inventory();

    	Gdx.app.log(logstag,"________");
    	//we clear the semantics before adding the player data (because that contains semantics
    	SuperSimpleSemantics.clearAllIndexsAndNodes();

    	//create starting computer and default player settings
	  	PlayersData.setup();
    	//----------------------------------------------------
	  	
    	Gdx.app.log(logstag,"_____________"); 
     //   
    	menu= new MainMenuScreen(game);
    	game.setScreen(menu);

    	//setup semantics
        setupSemanticsAndHomeDomain();

    	
        
    }

    /** setup the semantic database/processing which is the core engine for the games 
     * locations,puzzles and..well..everything **/
    public void setupSemanticsAndHomeDomain()
    {
    	//all off for production
    	if (currentMode == GameMode.Production ){
      	  Logger.getLogger("sss").setLevel(Level.OFF);
      	  Logger.getLogger("sss.DemoKnowledgeBase").setLevel(Level.OFF);
      	  Logger.getLogger("sss.SSSNodesWithCommonProperty").setLevel(Level.OFF);
      	  Logger.getLogger("sss.DemoKnowledgeBase").setLevel(Level.OFF);
      	  Logger.getLogger("sss.SSSNode").setLevel(Level.OFF);
      	  Logger.getLogger("sss.QueryEngine").setLevel(Level.OFF);
      	  Logger.getLogger("sss.JavaFileManager").setLevel(Level.OFF);    		  
          Logger.getLogger("sss.SSSIndex").setLevel(Level.OFF);
      	  
    	} else {
    	
    		//turn just some logs off
    		//Log.setLevel(Level.OFF);
    	  Logger.getLogger("sss");
    	  Logger.getLogger("sss.DemoKnowledgeBase").setLevel(Level.OFF);
    	  //  Logger.getLogger("sss.SSSNodesWithCommonProperty").setLevel(Level.WARNING);
    	  Logger.getLogger("sss.DemoKnowledgeBase").setLevel(Level.OFF);
    	  Logger.getLogger("sss.SSSNode").setLevel(Level.WARNING);
    	  Logger.getLogger("sss.QueryEngine").setLevel(Level.OFF);
    	  Logger.getLogger("sss.JavaFileManager").setLevel(Level.OFF);    		  
    	  //	  Logger.getLogger("sss.SSSIndex").setLevel(Level.OFF);
    	  
    	}
    	
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
    	        	//ArrayList<SSSNode> workingnodes = SSSNode.getAllKnownNodes();
    		      //  for (SSSNode sssNode : workingnodes) {						

        	      //  	Gdx.app.log(logstag,"_________"+sssNode.getPLabel());
        	        	
        	        	
					//}

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
    	  
    	
    	
    	  //its important to add the full path to the knowndatabases array
    	  //because the SSS will automatically expand short urls and filepaths to
    	  //absolute when needed internally
    	  //Thus if theres a "is this loaded already?" comparison, we need
    	  //to check full path against full path
    	  String fullPathOfHomeOntology = SuperSimpleSemantics.fileManager.getAbsolutePath("semantics/TomsNetwork.ntlist"); //semantics\\TomsNetwork.ntlist
    	  
    	  trustedIndexs.add(fullPathOfHomeOntology);    //semantics\\TomsNetwork.ntlist
 
    	  Gdx.app.log(logstag,"______fullPathOfHomeOntology:-:________"+fullPathOfHomeOntology);
    	  
    	  //create domain object from this database url
    	  MEDomain homedomain = MEDomain.createNewDomain(fullPathOfHomeOntology);
    	  MEDomain.setAsHomeDomain(homedomain); //set it as our home domain
    	  MEDomain.setCurentDomain(homedomain);

      	//  trustedIndexs.add("http://darkflame.co.uk/semantics/darksnet.ntlist"); //testing 
        //  knownDatabases.add("http://darkflame.co.uk/semantics/darksnet.ntlist");    	  
    	  
      	  SuperSimpleSemantics.loadIndexsAt(trustedIndexs); //we load the needed databases (which is only an array containing 1 database, but whatever...)
      	  
    }
    
    protected static void startgame() {
    	
    	 Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run() {

		    	 menu.start();
			}
    		
    	 });
    	 
    	
    	
	}

	@Override
	public void render() {
        super.render(); //important!
        
    }

    @Override
	public void dispose() {
     //   batch.dispose();
     //   font.dispose();
    }

    public static void centerViewOn(Location locationcontainer, float newZ, boolean addLocationToUndo){
    	centerViewOn(locationcontainer,  newZ,  addLocationToUndo,4000);
    }
    
    public static void centerViewOn(MeshIcon meshIcon,float newZ, int durationms) {
    	centerViewOn(meshIcon,newZ,false,durationms);
    	
    }
    public static void centerViewOn(MeshIcon meshIcon, int durationms) {
    	centerViewOn(meshIcon,ScreenUtils.getSuitableDefaultCameraHeight(),false,durationms);
    	
    }
    public static void centerViewOn(MeshIcon icon, float newZ, boolean addLocationToUndo,int speed){
    
		Vector3 dest = icon.transState.position.cpy();
		if (newZ==-1){
			dest.z = MainExplorationView.currentPos.z;
		} else {
			dest.z = newZ;
		}
	
		Gdx.app.log(MainExplorationView.logstag,"moving to: "+dest);
		
		MainExplorationView.camera.setTargetPosition(dest,speed); //new system replaces a lot below
		
    }
    
   
    
public static void centerViewOn(Location locationcontainer, float newZ, boolean addLocationToUndo,int speed){
	
		Gdx.app.log(MainExplorationView.logstag,"moving to z: "+newZ);
		//CurrentX=locationcontainer.getCenterX();  //getX()+(locationcontainer.getWidth()/2);
		//CurrentY=locationcontainer.getCenterY(); //getY()+(locationcontainer.getHeight()/2);
	
		float newX = locationcontainer.getHubsX(Align.center);
		float newY = locationcontainer.getHubsY(Align.center);
		
		
		
		Vector3 dest = new Vector3(newX,newY,newZ);
		
	
	
		Gdx.app.log(MainExplorationView.logstag,"moving to: "+dest);
		
		MainExplorationView.camera.setTargetPosition(dest,speed); //new system replaces a lot below
		
		
		
	
		
		//add the requested location to the  array list, but only if its different from
				//the last location.
		Location lastlocstored =null;;
		try {
			lastlocstored = ME.LastLocation.getLast();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (lastlocstored!=null && addLocationToUndo){
			
			if (locationcontainer!=lastlocstored){
				
				Gdx.app.log(MainExplorationView.logstag,"adding="+locationcontainer.locationsnode.toString());
	
				ME.LastLocation.add(locationcontainer);
				
				for (Location test : ME.LastLocation) {
					
					Gdx.app.log(MainExplorationView.logstag,"LastLocation="+test.locationsnode.getPLabel());
					
				}
				
			}
			
		} else {
			
			for (Location test : ME.LastLocation) {
				
				Gdx.app.log(MainExplorationView.logstag,"LastLocation="+test.locationsnode.getPLabel());
				
			}
			ME.LastLocation.add(locationcontainer);
			
		}
		
		
	}

public static void centerViewOn(Location locationcontainer, boolean addLocationToUndo){
	
		MainExplorationView.coasting = false;
		MainExplorationView.dragging = false;		
		ME.centerViewOn(locationcontainer, MainExplorationView.currentPos.z,addLocationToUndo); //set position in all dimensions but z which we keep the same
	}

public static void centerViewOn(Location currentlyOpenLocation2, float newZ){		
		
		MainExplorationView.coasting = false;
		MainExplorationView.dragging = false;		
		ME.centerViewOn(currentlyOpenLocation2, newZ,true); //set position in all dimensions but z which we specify
				
	}

public static void centerViewOn(Location currentlyOpenLocation2, float newZ,int durationms){		
	
	MainExplorationView.coasting = false;
	MainExplorationView.dragging = false;		
	ME.centerViewOn(currentlyOpenLocation2, newZ,true,durationms); //set position in all dimensions but z which we specify
			
}


public static void centerViewOn(Location locationcontainer){		
		
		MainExplorationView.coasting = false;
		MainExplorationView.dragging = false;		
	
		float newZ = locationcontainer.getHubsZ() +  ScreenUtils.getSuitableDefaultCameraHeight(); //MECamera.standardCameraHeightAboveLocations;
		ME.centerViewOn(locationcontainer, newZ,true); //set position in all dimensions but z which we use ths standard value for
				
	}




public static Ray getCurrentStageCursorRay() {
		
	Vector2 currentCursor = getCurrentCursorScreenPosition();
		
		Gdx.app.log(logstag, " testing for hits at: "+currentCursor.x+","+currentCursor.y);
		return MainExplorationView.camera.getPickRay(currentCursor.x, currentCursor.y);
	}

/** temp only, goes to the meshicon rather then the actual loc**/
public static void gotoLocation2(SSSNode linksToThisPC) {
	//flag if the user is home
	if (linksToThisPC.equals(PlayersData.computersuri)){
		ME.isAtHome = true;
	} else {
		ME.isAtHome = false;		  
	}

	//get the node screen.
	//This will automatically check if it already exists
	//else it will create a new one
	Location screen = Location.getLocation(linksToThisPC);
	ME.centerViewOn(screen.locationsNEWHub,4000);
	
	MainExplorationView.currentlyOpenLocation = screen;
}

public static void gotoLocation(SSSNode linksToThisPC) {
	
	
	
		//flag if the user is home
		if (linksToThisPC.equals(PlayersData.computersuri)){
			ME.isAtHome = true;
		} else {
			ME.isAtHome = false;		  
		}
	
		//get the node screen.
		//This will automatically check if it already exists
		//else it will create a new one
		Location screen = Location.getLocation(linksToThisPC);
		
	
	
		MainExplorationView.currentlyOpenLocation = screen;
		ME.centerViewOn(MainExplorationView.currentlyOpenLocation);
	
	
	
	}

public static Vector2 getCurrentStageCursorPosition() {
	
		float xc = Gdx.input.getX();
		float yc = Gdx.input.getY();//-gameStage.getHeight();
		
		Vector2 vec = new Vector2(xc,yc);
		 MainExplorationView.gameStage.screenToStageCoordinates(vec);
		
	//	 Gdx.app.log(logstag,"_____________:yc "+yc+"="+vec.y);
		
		return vec;
	}

public static Vector2 getCurrentCursorScreenPosition() {
	
		float xc = Gdx.input.getX();
		float yc = Gdx.input.getY();//-gameStage.getHeight();
		
		Vector2 vec = new Vector2(xc,yc);
		
	//	 Gdx.app.log(logstag,"_____________:yc "+yc+"="+vec.y);
		
		return vec;
	}

public static void disableMovementControl(boolean state) {
		MainExplorationView.movementControllDisabled = state;
		
	}

public static void disableDrag(){
		
		MainExplorationView.dragging = false;
		MainExplorationView.cancelnextdragclick = true;
		
	}
//

public static void addnewdrop(ConceptObject newdrop, float x, float y,float z) {
	 Gdx.app.log(MainExplorationView.logstag,"_____________:newdrop ");
	 MainExplorationView.infoPopUp.displayMessage("Concept Node dropping:"+newdrop.itemsnode.getPLabel());
	 
	 // x = (int)x - (newdrop.getWidth()/2);
	//  y = (int)y - (newdrop.getHeight()/2); //No need as its centralized anyway
	 
	 
	 newdrop.setToPosition(new Vector3(x,y,z));
	 newdrop.show();
	 newdrop.setAsDropped();
	 
		double deg = (Math.random()*30)-15; 		
		newdrop.setToRotation(new Quaternion(Vector3.Z, (float) deg));
	 
	 Population.testForReactionsToNewDrop(newdrop,x,y);
	 

		Gdx.app.log(logstag,"newdrop transform is now="+newdrop.getTransform()); 
}



public static void addnewdrop(DataObject newdrop, float x, float y) {
	
		 Gdx.app.log(MainExplorationView.logstag,"_____________:dropping ");
		 
		 MainExplorationView.infoPopUp.displayMessage("Concept Node dropping:"+newdrop.itemsnode.getPLabel());
		 
		 
		//Image dropimage = new Image(newdrop);		
		newdrop.setPosition((int)x - (newdrop.getWidth()/2),(int)y- (newdrop.getHeight()/2));
		
		double deg = (Math.random()*30)-15; 		
		newdrop.setRotation((float) deg);
		
		//ensure its clickable (else how will you pick it up?)
		
		newdrop.setTouchable(Touchable.enabled);
			
		
		MainExplorationView.gameStage.addActor(newdrop);
		
		//now we test for reactions to the drop
		Population.testForReactionsToNewDrop(null,x,y); //drop specific reactions not implemented yet anyway, hence the null
		
		
	}

public static void gotoLastLocation() {
	
		Gdx.app.log(MainExplorationView.logstag,"goto to last location");
		
		for (Location test : ME.LastLocation) {			
			Gdx.app.log(MainExplorationView.logstag,"LastLocations="+test.locationsnode.getPLabel());			
		}
		
		if (ME.LastLocation.size()==0){
			return;
		}
		
	
		//remove current location (which should be the last added)
		ME.LastLocation.removeLast();			
		
		if (ME.LastLocation.size()==0){
				return;
		}
		
		
		//goto the last one if theres one
		Location requested = ME.LastLocation.getLast(); //gwt can't use peeklast
		
	
		if (requested!=null){
			
			Gdx.app.log(MainExplorationView.logstag,"last location is:"+requested.locationsnode.getPLabel());		
			ME.LastLocation.removeLast();			
			ME.centerViewOn( requested,false );
			
		} else {
	
			Gdx.app.log(MainExplorationView.logstag,"no last location");
			
		}
	}

public static void gotoHomeLoc() {
		
		MainExplorationView.infoPopUp.displayMessage("Heading Home..");
		
		ME.centerViewOn( PlayersData.homeLoc);
	
	}

/**
 * checks for unloaded databasess this uri could be part of and starts loading if needed
 * 
 * @param linksToThisPC
 * @return
 */
public static Boolean checkForUnloadedDomainAndLoad(SSSNode linksToThisPC) {
	
	
	String label = linksToThisPC.getPURI();

	Gdx.app.log(logstag,"testing uri:"+label);
	
	if (label.contains(".ntlist#")){
	
		Gdx.app.log(logstag,"detected database");
		String databaseurl = label.substring(0, label.indexOf("#"));
		
		Gdx.app.log(logstag,"database url:"+databaseurl);
		Gdx.app.log(logstag,"databases known:"+MEDomain.knownDomains);
		
		
		//test if already loaded
		if (!MEDomain.domainloaded(databaseurl)){
			//!knownDomains.contains(databaseurl)
			
			//we add it straight away before its loaded as we don't want to start loading it again
			//Note; we should have a separate list for "currently loading" in case another link to the same database is come accross
			//while this one is still loading. Then that new runWhenDone should also wait for the same database to load rather then being enabled straight away
									
			//create domain object we use the create function as that finds a spare location for us
			//this also adds it to MEDomains domain list
			MEDomain newDomain = MEDomain.createNewDomain(databaseurl);
						
			//create database
			Gdx.app.log(logstag,"_____________________domains database not loaded:");
			SuperSimpleSemantics.loadIndexAt(newDomain.getDomainsDataBaseURL());
			
			
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
