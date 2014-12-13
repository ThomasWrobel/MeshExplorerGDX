package com.lostagain.nl.me.creatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.LocationGUI.Location;

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
	//efficiancy!
	Boolean active = true; 
	
	//type of population (determains look and movement?)
	enum creaturetype {
		Infovore
	}
	
	//number of creatures in population
	int num = 0;
	
	//creature population centered around this location
	Location centeredOnThisLocation;
	
	
	//location spread
	int fromRadius = 400;
	int toRadius = 550;
	
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
	destructOn destructionType = destructOn.clicks; //defaults to a quert dropped on it, but a click is used to test atm
	int hitPoints = 1; //can have more in future, possibly scaled to an attack variable
	
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
		
		
	}

	private void copyfromexistingpopulation(Population source) {

		Gdx.app.log(logstag, "creating population from existing pop");
		
		this.destructionType = source.destructionType;
		this.drops = (ArrayList<SSSNode>) source.drops.clone();
		this.fromRadius = source.fromRadius;
		this.toRadius =  source.toRadius;
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
			BasicInfovore newcreature = new BasicInfovore(this,x, y, hitPoints,queryToDestroy,destructionType);
					
			newcreature.setColor(randomColorFromPop());
			
			
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
	 * defaults to green
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
	
}
