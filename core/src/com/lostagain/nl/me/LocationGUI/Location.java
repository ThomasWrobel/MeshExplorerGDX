package com.lostagain.nl.me.LocationGUI;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.creatures.Population;
import com.lostagain.nl.uti.MeshWorld;

/** manages the creation and basic attributes of locations in the the game world
 * a location will have a central hub, and it might have a population of creatures near it
 * **/
public class Location {

	final static String logstag = "ME.Location";
	
	public LocationsHub locationsHub;
	Population locationsPopulation;
	
	int locX = 0;
	int locy = 0;
	
	public SSSNode locationsnode;
	
	
	
	//TO DO: Move some stuff from LocationsHub to here.
	//Specifically its SSSNode, its position, and maybe other things.
	
	//This location marks the center of everything here, no need for the hub (which is at the location) to also have this stored seperate

	//Also separate out Hub creation and make generation creation method
	// Hub
	// Population creation (for each possible population)
	
	private static final HashMap<SSSNode,LocationsHub> AllLocationContainers = new HashMap<SSSNode,LocationsHub>();
	private static final HashMap<SSSNode,Location> AllLocations = new HashMap<SSSNode,Location>();

	

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

		Vector2 loc =  MeshWorld.locationFromDomain(domain);

		int X = (int) loc.x;  //(int) (Math.random()*2500);
		int Y = (int) loc.y; //500;
		
		//random y very slightly just for stylistic effect
		Y = (int) (Y + (Math.random()*200 -10));
		
		Gdx.app.log(logstag, "getting unused location. Testing:"+X+","+Y);

		X = getNextUnusedPosition(X,Y);
		
		//set up as normal
		seupLocation(locationsnode, X, Y);
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
		
		//creating new location
		String domain = locationsnode.getPURI();
		Gdx.app.log(logstag, "domain is="+domain);
		
		createNewHubAt(X,Y);
		
		//create population
		locationsPopulation = new Population(this);
	}
	
	
	public static Location getLocationHub(SSSNode linksToThisPC) {
		
				
		if (AllLocations.containsKey(linksToThisPC)){
			
			return AllLocations.get(linksToThisPC);
			
		} else {

			return new Location(linksToThisPC);

		}

	}
	
	
	public void createNewHubAt(int X,int Y){

		Gdx.app.log(logstag, "creating hub");
		
		locationsHub = new LocationsHub(locationsnode);
		AllLocationContainers.put(locationsnode,locationsHub);
		
		MainExplorationView.addnewlocation(locationsHub,X, Y);
	}
	
	private static int getNextUnusedPosition(int x, int y) {

		Boolean xchanged = true;

		int CONHEIGHT = 500;
		int CONWIDTH = 500;

		while (xchanged)
		{

			xchanged = false;


			Gdx.app.log(logstag, "getting unused location. Testing:"+x);

			//loop over all locations, displace X if its overlaps
			for (LocationsHub con : AllLocationContainers.values()) {

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
	public float getHubsY(int align) {		
		return locationsHub.getY(align);
	}

	public float getHubsX(int align) {		
		return locationsHub.getX(align);
	}
}
