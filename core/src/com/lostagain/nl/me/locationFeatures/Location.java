package com.lostagain.nl.me.locationFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Align;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.creatures.Creature;
import com.lostagain.nl.me.creatures.Population;
import com.lostagain.nl.me.features.DataRequestManager;
import com.lostagain.nl.me.features.LocationHub;
import com.lostagain.nl.me.features.MeshIcon;
import com.lostagain.nl.uti.MeshWorld;

/** manages the creation and basic attributes of locations in the the game world
 * a location will have a central hub, and it might have a population of creatures near it
 * **/
public class Location {

	final static String logstag = "ME.Location";
	/**
	 * When locations are made, this gives them a slight randomization in their x/y position
	 * This stops a grid of locations forming thats a bit too regular
	 */
	final static float PositionNoise = 50;
//	private static HashMap<SSSNode, LocationHub> AllLocationNEWHubs;
	
	public LocationsHub_old locationsHub; //old being phased out

	//the DataRequestManager, which manages if we are locked 
	DataRequestManager locationsLock;
	public LocationHub locationsNEWHub; //will slowly replace the above, its a new way to visualise a location
	
	ArrayList<Population> locationsPopulations = new ArrayList<Population>();
			
	
	
	public ArrayList<Population> getLocationsPopulations() {
		return locationsPopulations;
	}


	int locX = 0;
	int locy = 0;
	
	
	public SSSNode locationsnode;
	HashSet<SSSNodesWithCommonProperty> locationsPropertys; //will store all the SSSNodesWithCommonPropertys with the location in it. NOTE this will need refreshing if new sets are loaded 

	
	
	
	
	
	
	//TO DO: Move some stuff from LocationsHub to here.
	//Specifically its SSSNode, its position, and maybe other things.
	
	//This location marks the center of everything here, no need for the hub (which is at the location) to also have this stored separate

	//Also separate out Hub creation and make generation creation method
	// Hub
	// Population creation (for each possible population)

	private static final HashMap<SSSNode,LocationHub> AllLocationHubsNEW = new HashMap<SSSNode,LocationHub>();
	private static final HashMap<SSSNode,LocationsHub_old> AllLocationHubs = new HashMap<SSSNode,LocationsHub_old>();
	private static final HashMap<SSSNode,Location>     AllLocations = new HashMap<SSSNode,Location>();

	

	public Location(SSSNode locationsnode) {
		super();

		
		
		/*		
		this.locationsnode = locationsnode;
		
		AllLocations.put(locationsnode,this);
		
		//creating new location
		String domain = locationsnode.getPURI();
		Gdx.app.log(logstag, "domain s="+domain);

		Vector2 loc =  MeshWorld.locationFromDomain(domain);

		int X = (int) loc.x;  //(int) (Math.random()*2500);
		int Y = (int) loc.y; //500;
		
		//random y very slightly just for stylistic effect
		Y = (int) (Y + (Math.random()*200 -10));
		
		Gdx.app.log(logstag, "getting unused location. Testing:"+X+","+Y);

		X = getNextUnusedPosition(X,Y);
		
		
		createNewHubAt( X, Y);
		
		this.locX = X;
		this.locy = Y;
		*/
		
		//work out next free location around the domains natural position
		String domain = locationsnode.getPURI();
		Gdx.app.log(logstag, "domain is=="+domain);

		
		
		//Currently the location is taking from the domain (its makes a arbitary position from the letters)
		//We then check if theres any existing locations at that spot, and if so, we move around the domain to find the Next Unused Position
		//This results in locations forming a star like patturn around their domains center
		//In future we might want to look into different layout forms for different domains to help give them their own flavour
		Vector2 loc =  MeshWorld.locationFromDomain(domain);
		

		int X = (int) loc.x;  //(int) (Math.random()*2500);
		int Y = (int) loc.y; //500;	
		
		//random position very slightly just for stylistic effect
		
		Y = (int) (Y + (Math.random()*PositionNoise -(PositionNoise/2)));
		X = (int) (X + (Math.random()*PositionNoise -(PositionNoise/2)));
		
		Gdx.app.log(logstag, "getting unused location for "+locationsnode.getPLabel()+". Testing:"+X+","+Y);

		//X = getNextUnusedPosition(X,Y);
		Vector2 newLocation = getNextUnusedPosition(X,Y); 
		
		
		//set up as normal
		seupLocation(locationsnode, (int)newLocation.x, (int)newLocation.y); //just cast for now for rounding. Could also use Math.Floor. Not really important the precise half pixel.
		
	}
	
	private void getLocationsPropertys() {
		
		//in order to minimise the need for data querys
		//we get all the locations propertys on loading here
		
		locationsPropertys = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(locationsnode.getPURI());
		Gdx.app.log(logstag, "got locations propertys num="+locationsPropertys.size());
		
		
	}

	public Location(SSSNode locationsnode,int X,int Y) {
		
		super();		
		seupLocation(locationsnode, X, Y);
		
	}

	private void seupLocation(SSSNode locationsnode, int X, int Y) {
		this.locX = X;
		this.locy = Y;
		this.locationsnode = locationsnode;
			
		AllLocations.put(locationsnode,this);
		

		getLocationsPropertys();
		
		//creating new location
		String domain = locationsnode.getPURI();
		Gdx.app.log(logstag, "domain is="+domain);
		
		createNewHubAt(X,Y);
		
		//Note; Might be an idea to get all the populations first, then populate all the locations?
		//Certainly more efficient semantic wise. In some ways aspects of this game shows the weakness of the folded database system
		//while its easy to cross reference over multiple domains, its far more work to get all the details of a single node
				
		//BobsOutpost me:populatedBy smallInfovorPopulation.
	
		//get nodes populations (if any)
		//Note; Because we are looping over all the locations propertys, it would be efficiancy to refractor other things on the scene to this location.
		
		for (SSSNodesWithCommonProperty propertys : locationsPropertys) {
			
			if (propertys.getCommonPrec()==StaticSSSNodes.populatedBy){

				//create population and remember it		
				Gdx.app.log(logstag, "detected population of "+propertys.getCommonValue()+" on this scene");
				Population newpop=new Population(this,propertys.getCommonValue());				
				locationsPopulations.add(newpop);
				
			}
			
		}
		
		
		
		
	//	locationsPopulation = new Population(this);
	}
	
	
	/** gets a location, generating one if required **/
	public static Location getLocation(SSSNode linksToThisPC) {
		
				
		if (AllLocations.containsKey(linksToThisPC)){
			
			return AllLocations.get(linksToThisPC);
			
		} else {

			return new Location(linksToThisPC);

		}

	}
	
	/** gets an existing locationhub that is at this location sssnode. Note; hubs and locations share a common SSSNode
	 * identifying them.
	 * The hub is merely the "center" of the location. **/
	public static LocationsHub_old getExistingHub(SSSNode hubsNode) {		
		return AllLocationHubs.get(hubsNode);
	}
	
	public void createNewHubAt(int X,int Y){

		Gdx.app.log(logstag, "creating hub");
		
		locationsHub = new LocationsHub_old(locationsnode);
		AllLocationHubs.put(locationsnode,locationsHub);

		MainExplorationView.addnewlocationHub(locationsHub,X, -Y+1000); 		//new location hub system below, old one has inverted Y now to keep it out the way
		

		Gdx.app.log(logstag, "creating new hub");
			
		
		locationsNEWHub  = new LocationHub(locationsnode,this);
		locationsNEWHub.hide(); //hide by default (we will make visible after lockscreen) 
		

		AllLocationHubsNEW.put(locationsnode,locationsNEWHub);

		
		if (locationsnode!=PlayersData.computersuri){			
			//MainExplorationView.addnewlocationHub(locationsNEWHub,(X*2), (-Y*2)+1100);			//new hub has different layout, more spaced and (currently) inverted
			MainExplorationView.addnewlocationHub(locationsNEWHub,(X), (Y));			//new hub has different layout, more spaced and (currently) inverted
			
		} else {
			//new home might be at non-standard place
			MainExplorationView.addnewlocationHub(locationsNEWHub,(X), (Y));			
		}
		
		SSSNode firewallNode = getFirewallForLocation();
		
		//create lockscreen if any (has to be done after locationsNEWHub has a location set
		if (firewallNode!=null){
			locationsLock = new DataRequestManager(firewallNode,locationsNEWHub);
		
			
		}
			

		//only display if theres no locks or just 1
		//this is because the last lock is always on the locationhub itself and thus the hub should be visible if theres only 1 lock
		if (locationsLock==null || locationsLock.numberOfLocks()==1){
			
			locationsNEWHub.show();
			
		}	 else {
			
			locationsNEWHub.hide();	
		//	locationsNEWHub.show(); //temp
		}
		
		
		
		
	}
	
	
	/**
	 * slightly crude method for getting the firewall of the current location, if any
	 * Its messy as the SSS engine isn't really designed for indivudual cases of finding value given
	 * the predicate and subject.
	 * Its much more efficant when finding all the subjects fitting a given value and predicate.  
	 * @return
	 */
	private SSSNode getFirewallForLocation() {
		SSSNode securedBy = null;

		Gdx.app.log(logstag,"getting security for:"+locationsnode.PURI);

		if (ME.checkDatabaseIsLoaded(locationsnode)==false){
			
			Gdx.app.log(logstag,"WARNING COMPUTERS DATABASE NOT LOADED. THIS SHOULD NOT HAVE BEEN LINKED TOO YET");
			
		}

		HashSet<SSSNodesWithCommonProperty> sets = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(locationsnode.getPURI());
		
		Gdx.app.log(logstag,"sets:"+sets.size());

		for (SSSNodesWithCommonProperty sssNodesWithCommonProperty : sets) {

			if (sssNodesWithCommonProperty.getCommonPrec()==StaticSSSNodes.SecuredBy){

				securedBy = sssNodesWithCommonProperty.getCommonValue();

				Gdx.app.log(logstag,"security found:"+securedBy.getPURI());

				break;
			}

		}
		return securedBy;
	}

	/**
	 * Attempts to find the nearest free spot around the current point.
	 * 
	 * Tests clockwise by a regular number of degrees
	 * Moving out further if no free space found within that circle, and test again
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private static Vector2 getNextUnusedPosition(int x, int y) {
		
		Vector2 requestedPosition = new Vector2(x,y);
		
		//spacing distance
		float dis = 1000+PositionNoise; //should be set to at least the container size + the position variance
		Vector2 newPosition = null;
		boolean spaceFree=true;
		
		//we currently have a cap on 10 enlargements of the initial distance (so, 500x10) will be the maximum range it will check before giving up
		//on finding a free space
		//Note, it will have to be a pretty crowded location to ever get this far
		for (int i = 0; i < 10; i++) {
		
		Vector2 offset = new Vector2(0,dis*i);	
		
		int testingAng=0;
		
		for (testingAng = 0; testingAng < 360; testingAng=testingAng+90) {
			
			offset.rotate(testingAng); //rotate the offset to the new angle
			
			//get new position to test
			newPosition =  new Vector2(requestedPosition);	//the new position is the old one	
			newPosition.add(offset); //with the offset added to it
			
			Gdx.app.log(logstag, "Testing "+testingAng+" position:"+newPosition.x+","+newPosition.y);
			
			//test the position			
			spaceFree=testIfPositionFree(newPosition);
			
			if (spaceFree){
				return newPosition;//if free we just return the found position 
			}
			
		}

		Gdx.app.log(logstag, "ran out of positions in range,moving out ");
		
		}
		return newPosition;
		
	}
	
	
	
	
	private static boolean testIfPositionFree(Vector2 testingAng) {
		
		int CONHEIGHT = 500;
		int CONWIDTH = 500;
		
		//loop over all locations, displace X if its overlaps
	//	for (LocationsHub con : AllLocationHubs.values()) {
		for (LocationHub con : AllLocationHubsNEW.values()) {
			/*

			float miny = con.getY();
			float maxy = con.getY() + con.getHeight();

			float minx = con.getX();
			float maxx = con.getX() + con.getWidth();
*/
			
			BoundingBox bounds = con.getLocalCollisionBox(false);
			Vector3 min = new Vector3();
			bounds.getMin(min);
			
			Vector3 max = new Vector3();
			bounds.getMax(max);
			
			
			float miny = min.y;
			float maxy = max.y;

			float minx = min.x;
			float maxx = max.x;
			
			Gdx.app.log(logstag, "location range="+min.y+"-"+max.y+","+min.x+"-"+max.x);
			

			//Gdx.app.log(logstag, "minx="+minx+" maxx="+maxx+" x="+testingAng.x);
			//Gdx.app.log(logstag, "miny="+miny+" maxy="+maxy+" y="+testingAng.y);
			
			//if within y with margin
			if (((testingAng.y+CONHEIGHT)>miny-5) && (testingAng.y<maxy+5))
			{
				Gdx.app.log(logstag, "within y");
				
				if (((testingAng.x+CONWIDTH)>minx-5) && (testingAng.x<maxx+5)){

					Gdx.app.log(logstag, "within x and y - we hit "+con.LocationsNode);

					return false;

				}

			}

		}
				
		return true;
	}

	private static int getNextUnusedPosition_OldLinearMethod(int x, int y) {

		Boolean xchanged = true;

		int CONHEIGHT = 500;
		int CONWIDTH = 500;
			

		while (xchanged)
		{

			xchanged = false;


			Gdx.app.log(logstag, "getting unused location. Testing:"+x);

			//loop over all locations, displace X if its overlaps
			for (LocationsHub_old con : AllLocationHubs.values()) {

				float miny = con.getY();
				float maxy = con.getY() + con.getHeight();

				float minx = con.getX();
				float maxx = con.getX() + con.getWidth();

				Gdx.app.log(logstag, "loc="+con.displayLocation);
				Gdx.app.log(logstag, "miny="+miny+" maxy="+maxy+" y="+y);
				Gdx.app.log(logstag, "minx="+minx+" maxx="+maxx+" x="+x);
				//if within y with margin
				if (((y+CONHEIGHT)>miny-5) && (y<maxy+5))
				{
					Gdx.app.log(logstag, "within y");
					if (((x+CONWIDTH)>minx-5) && (x<maxx+5)){

						Gdx.app.log(logstag, "within x");

						x=(int) (x+con.getWidth());

						Gdx.app.log(logstag, "new x ="+x);
						xchanged=true;

						continue;

					}

				}



			}

		}

		return x;
	}
	public float getHubsY() {		
		return locationsNEWHub.getCenterOnStage().y;
	}

	public float getHubsX() {		
		return locationsNEWHub.getCenterOnStage().x;
	}
	
	public float getHubsZ() {		
		return locationsNEWHub.getCenterOnStage().y;
	}
	
	/**
	 * returns current number of creatures (used for visual corruption effects)
	 * @return
	 */
	public float getInfectionAmount(){
		int num = 0;
		
		for (Population population : locationsPopulations) {
			
			num=num+population.getCurrentNumberOfCreatures();
			
		}
		
		return num;
	}
	
	
	/**
	 * returns the locations near a certain point
	 * 
	 * @param position
	 * @param radius
	 */
	public static Set<Location> LocationsWithinRange(Vector3 position, float radius){
				
		Set<Location> locationsWithinRange = new HashSet<Location>();
		
		for (Location loc : AllLocations.values()) {
			
			float LocX = loc.getHubsX();
			float LocY = loc.getHubsY();
			float LocZ = 0;
			Vector3 locationsPosition = new Vector3(LocX,LocY,LocZ);
			
			float distanceTo = position.dst(locationsPosition);
			
			if (distanceTo<radius){
				locationsWithinRange.add(loc);
			}
			
		}

		return locationsWithinRange;
	}
	
	
	
	/**
	 * Returns the corruption level near a certain point
	 * 
	 * @param position
	 * @param radius
	 * 
	 **/
	public static float getCorruptionAt(Vector3 position, float radius){
						
		//start corruption at zero
		float corruptionTotal = 0;
		
		//per creature
		final float corruptionPerCreature = 0.02f;
		
		for (Location loc : AllLocations.values()) {
			
			float LocX = loc.getHubsX();
			float LocY = loc.getHubsY();
			float LocZ = 0;
			Vector3 locationsPosition = new Vector3(LocX,LocY,LocZ);
			
			float distanceTo = position.dst(locationsPosition);
			
			if (distanceTo<radius){
				
				float creatures = loc.getInfectionAmount(); //get total creatures at location			
				float corruptionOfLocation = creatures * corruptionPerCreature;
				float scaleDownByDistance = (corruptionOfLocation*(1-(distanceTo/radius)));
				corruptionTotal=corruptionTotal+scaleDownByDistance;
								
			}
			
		}
		
		
		
		
		return corruptionTotal;
	}

	/**
	 * returns the icon incoming links should goto.
	 * either the locationshub, or its first lock screen 
	 * @return
	 */
	public MeshIcon getLinkPoint() {
		
		if (locationsLock==null){
			return locationsNEWHub;
		} else {
			return locationsLock.getFirstLockIcon();
		}
		
		
	}
	
}
