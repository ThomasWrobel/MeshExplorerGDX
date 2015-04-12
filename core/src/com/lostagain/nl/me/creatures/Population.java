package com.lostagain.nl.me.creatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.objects.DataObject;

/** represents a population of a particular type in a particular area**/
public class Population {
	

	final static String logstag = "ME.Population";
	
	//population node
	SSSNode population;
	
	//population name
	String name = "";
	
	//if its currently active
	//in future populations not near the camera wont be active, and thus we wont bother testing
	//them for clicks or animation updates
	//Efficiency!
	Boolean active = true; 
	
	//type of population (determines look and movement?)
	enum creaturetype {
		Infovore
	}
	
	//number of creatures in population
	int num = 0;
	
	//creature population centered around this location
	Location centeredOnThisLocation;
	
	
	//location spread
	int fromRadius = 200;
	int toRadius = 550;
	
	//z height
	int atHeight = -50;
	
	//all the color tints of the population
	ArrayList<Color> populationsColors = new ArrayList<Color>();
	
	//distribution type? 
	//random, sinwave, fixed
	
	//drops, if any
	ArrayList<SSSNode> drops=new ArrayList<SSSNode>();
	ArrayList<SSSNode> exactdrops=new ArrayList<SSSNode>();
	
	//destruction mode
	enum destructOn {
		query,clicks,cant
	}
	destructOn destructionType = null; //defaults to a clicks unless a query is specified
	int hitPoints = 10; //default can have more in future, possibly scaled to an attack variable
	
	//require node matching this query to destroy
	String queryToDestroy = "";
	
	//-------------------------
	
	ArrayList<Creature> populationsCreatures = new ArrayList<Creature>();
	
	
	 static HashMap<SSSNode ,Population > allPopulations = new HashMap<SSSNode ,Population >();
	 
	 
	 
	
	public Population(Location location, SSSNode populationnode) {
		
		centeredOnThisLocation = location;
		population             = populationnode;
		
		//trigger detail getting from source locations node
		Gdx.app.log(logstag, "creating population of "+population.getPLabel());
		
		//see if a population of this type has already been loaded
		//if so, we can copy its details
		if (allPopulations.containsKey(populationnode)){
			copyfromexistingpopulation(allPopulations.get(populationnode));//quicker
		} else {
			getdetailsfromnode(populationnode); //slower, should only be needed once per population type
		}
				
		//create from details
		createPopulation();
		
		
	}

	
	private void getdetailsfromnode(SSSNode populationnode) {
		
		name = populationnode.getPLabel();	
		
		Gdx.app.log(logstag, "creating "+populationnode.getPLabel()+" for first time");
		
		//get all details of population
		HashSet<SSSNodesWithCommonProperty> populationPropertys = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(populationnode.PURI);
		
		/***
		 * smallInfovorPopulation rdfs:subClassOf me:population.
		smallInfovorPopulation me:quantity 10.
		smallInfovorPopulation me:anydrop dropdata1.
		smallInfovorPopulation me:exactdrop dropdata2.
		smallInfovorPopulation me:anydrop dropdata3.
		smallInfovorPopulation me:killedon "fruit"
		 * 
		 * **/
		for (SSSNodesWithCommonProperty property : populationPropertys) {
			
			SSSNode currentPred = property.getCommonPrec();
			SSSNode currentValue = property.getCommonValue();
						
			if (currentPred == StaticSSSNodes.quantity){
				num = Integer.parseInt(currentValue.getPLabel());
			}
			
			if (currentPred== StaticSSSNodes.anydrop){
				drops.add(currentValue);
			}
			
			if (currentPred== StaticSSSNodes.exactdrop){
				exactdrops.add(currentValue);
			}
			if (currentPred == StaticSSSNodes.killedon){
				queryToDestroy = currentValue.getPLabel();
			//	destructionType = destructOn.query;
				
			}
			if (currentPred == StaticSSSNodes.toRadius){
				toRadius = Integer.parseInt(currentValue.getPLabel());
				
			}
			if (currentPred== StaticSSSNodes.fromRadius){
				fromRadius = Integer.parseInt(currentValue.getPLabel());				
			}
			if (currentPred== StaticSSSNodes.atHeight){
				atHeight = Integer.parseInt(currentValue.getPLabel());				
			}
			if (currentPred== StaticSSSNodes.hitPoints){
				hitPoints = Integer.parseInt(currentValue.getPLabel());				
			}
			
			
			if (currentPred== StaticSSSNodes.DBPediaColour){
				
				Color newColor = DefaultStyles.getColorFromString(currentValue.getPLabel());
				
				if (newColor!=null){
					populationsColors.add(newColor);	
				}
				
				
			}
			
			
		}
		
		//ensure destruction type is set
		if (destructionType==null){
			destructionType = destructOn.clicks;
			
			if (queryToDestroy.length()>1){
				destructionType = destructOn.query;
			}
			
			
		}
		
		
	}

	private void copyfromexistingpopulation(Population source) {

		Gdx.app.log(logstag, "creating population from existing pop");
		
		this.destructionType = source.destructionType;
		this.drops = (ArrayList<SSSNode>) source.drops.clone();
		this.fromRadius = source.fromRadius;
		this.toRadius =  source.toRadius;
		this.atHeight = source.atHeight;
		this.name =  source.name;
		this.num =  source.num;
		this.hitPoints =  source.hitPoints;
		this.population  =  source.population;
		this.populationsCreatures =  source.populationsCreatures;
		this.queryToDestroy =  source.queryToDestroy;
		
	}

	/** creates this population on the specified node if it doesn't exist already **/
	public void createPopulation(){
		
		//run this after getting the details
		//temp details below
		//num = 10;
		float cX = centeredOnThisLocation.getHubsX(Align.center);
		float cY = centeredOnThisLocation.getHubsY(Align.center);
						
		
		//create creatures
		double sinstep = (2*Math.PI)/num; 
		double angle = 0;
				
		for (int j = 0; j < num; j++) {
			
			//get next location
			angle = angle + sinstep;
			
			//pick radius
			double pr = 0;
			pr = fromRadius+Math.random()*(toRadius-fromRadius);
						
			float x = (float) (cX + Math.sin(angle)*pr);
			float y = (float) (cY + Math.cos(angle)*pr);
			
			//get drops
			
			//create based on type						
			BasicInfovore newcreature = new BasicInfovore(this,x, y,atHeight, hitPoints,queryToDestroy,destructionType);
				
			Color creaturesBaseColor = randomColorFromPop();
			
			//we set both the creatures normal color setting and its current color setting to this
			newcreature.setNormalColor(creaturesBaseColor);	
			newcreature.setColor(creaturesBaseColor);
			
			
			populationsCreatures.add(newcreature);
					
			
			
		}
		
		//asign drops
		ArrayList<SSSNode> droppool = new ArrayList<SSSNode>();
		
		//shouldnt add drops directly, should get a selection of subtypes and use them instead
		droppool.addAll(drops);
		//exact drops can be used directly
		droppool.addAll(exactdrops);
		
		int totalcreatures = populationsCreatures.size();
			
		//asign drops randomly to creatures in this population
		
		for (SSSNode drop : droppool) {
			
			//get random creature
			int picked = (int) (Math.random()*totalcreatures);
			
			Creature pickedToAddDropTo = populationsCreatures.get(picked);
			
			pickedToAddDropTo.addDrop(drop);
			
		}
		
		
		
	}

	
	/**
	 * Defaults to green right now
	 * @return
	 */
	private Color randomColorFromPop() {
		
		if (populationsColors.size()==0){
			return Color.GREEN;	
		}		
		
		int p = (int) (Math.random()*populationsColors.size());
		
		
		
		return populationsColors.get(p);
		
	}


	public void removeFromPopulation(Creature creature) {
		
		populationsCreatures.remove(creature);
		
	}
	
	
	public int getCurrentNumberOfCreatures(){
		return populationsCreatures.size();
	}
	
	
	
	/**
	 * 
	 * gets all the creatures in this population in range of a particular point
	 * 
	 *
	 * @param location - target location
	 * @param radius - range
	 * @return
	 */
	public ArrayList<Creature> getCreaturesNearbye(Vector3 location, float radius, boolean useCreaturesSight){
		
		ArrayList<Creature> creaturesWithinRange = new ArrayList<Creature>();
		
		for (Creature creature : populationsCreatures) {
			
			Vector3 creaturesPosition = creature.getCenter();
			float distanceTo = location.dst(creaturesPosition);
			
			if (useCreaturesSight){
				if (distanceTo<creature.getEyeSightRange()){
					creaturesWithinRange.add(creature);
				}
			}
			
			if (distanceTo<radius){
				creaturesWithinRange.add(creature);
			}
			
		}

		return creaturesWithinRange;
	}


	/**
	 * Test all nearby populations for any reactions to this drop
	 * 
	 * At the moment creatures will move towards the drop if they are in range
	 * @param newdrop
	 * @param x
	 * @param y
	 */
	public static void testForReactionsToNewDrop(DataObject newdrop, float x,
			float y) {

		Vector3 dropsPositionAsVector = new Vector3(x,y,Creature.zPlane); //zPlane is the default vertical position. As the locations are 2D and the creatures 3D, we need to assume the creatures are on the 2D plane
		 //In future the getCreaturesNearby function could be changed to ignore the z axis when testing for nearby things, but I cant see why creatures should move far from their zPlane anyway. The 3D is mostly for effects and style, not gameplay
		
		
		//first get nearby locations
		ArrayList<Location> nearbylocations = Location.LocationsWithinRange(new Vector2(x,y), 500);
		
		//then loop over them finding creatures near the drop point
		//we dont test all locations as its wastefull to test things far away
		ArrayList<Creature> allNearbyCreatures = new ArrayList<Creature>();
		for (Location location : nearbylocations) {
			
			Gdx.app.log(logstag, "dropped near: "+location.locationsnode);
			
			//get creatures nearby - a location can have a few populations so we need to test all of them
			for (Population pop : location.getLocationsPopulations()) {
								
				allNearbyCreatures.addAll(pop.getCreaturesNearbye(dropsPositionAsVector, 25,true)); //the true means it uses the creatures eyesight range. The 25 means even a blind creature can sense it within this distance
				
								
			}
			
		}
		

		Gdx.app.log(logstag, "dropped near: "+allNearbyCreatures.size()+"creatures");
		//now loop firing creatures reactions
		for (Creature creature : allNearbyCreatures) {
			
			creature.fireReactionToDrop(dropsPositionAsVector,newdrop);
			
		}
		
	}
	

	
}
