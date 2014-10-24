package com.lostagain.nl.me.creatures;

import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.me.LocationGUI.Location;

/** represents a population of a particular type in a particular area**/
public class Population {
	
	//population name
	String name = "";
	
	//type of population
	enum creaturetype {
		Infovore
	}
	
	//number of creatures in population
	int num = 0;
	
	//creature population centered around this location
	Location centeredOnThisLocation;
	
	
	//location spread
	int fromRadius = 350;
	int toRadius = 400;
	
	//distribution type? 
	//random, signwave, fixed
	
	//drops, if any
	SSSNode drops[];
	
	//destruction mode
	enum destructOn {
		query,clicks,cant
	}
	destructOn destructionType = destructOn.clicks; //defaults to a quert dropped on it, but a click is used to test atm
	int numOfClicks = 1; //can have more in future, possibly scaled to an attack variable
	
	//require node matching this query to destroy
	String queryString = "";
	
	//-------------------------
	
	Creature populationsCreatures[];
	
	
	public Population(Location location) {
		
		centeredOnThisLocation=location;
		
		//trigger detail getting from source locations node
		
		//create from details
		createPopulation();
		
		
	}

	/** creates this population on the specified node if it doesn't exist already **/
	public void createPopulation(){
		
		//run this after getting the details
		//temp details below
		num = 1;

		populationsCreatures = new Creature[num];
		
		float cX = centeredOnThisLocation.getHubsX(Align.center);
		float cY = centeredOnThisLocation.getHubsY(Align.center);
						
		
		//create creatures
		double sinstep = (2*Math.PI)/num; 
		double angle = 0;
		
		for (int j = 0; j < num; j++) {
			
			//get next location
			angle = angle + sinstep;
			float x = (float) (cX + Math.sin(angle)*fromRadius);
			float y = (float) (cY + Math.cos(angle)*fromRadius);
			
			//create based on type
						
			BasicInfovore newcreature = new BasicInfovore(x, y, this);
			populationsCreatures[j]=newcreature;
			
		}
		
		
	}
	
}
