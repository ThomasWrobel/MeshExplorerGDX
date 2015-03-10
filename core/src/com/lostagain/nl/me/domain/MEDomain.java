package com.lostagain.nl.me.domain;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.me.LocationGUI.Location;

/**<br>
 * Defines a single domain in the game.<br>
 * The main game is played locally, not a real domain.<br>
 * Other people can add their own gameplay hosted on their own (real) domains.<br>
 * <br>
 * A domain has;<br>
 * <br>
 * a) A URL (not including any specific filename)<br>
 * b) A location in Meshworld X/Y (which is far away from others)<br>
 * c) A domain color map (used to tiny the background effect to give somewhat a unique feel between locations within the domain)<br>
 * d) A arraylist of locations at this domain. <br>
 * <br>
 * @author Tom
 *
 */
public class MEDomain {

	final static String logstag = "ME.MEDomain";
	
	
	/** the games home domain (containing the core database/gameplay)**/
	static MEDomain homeDomain=null;
	/** the domain the player is currently at **/
	static MEDomain currentDomain=null;
	
	String  DomainsDataBaseURL = "";




	Rectangle coversArea;//= new Rectangle(0,0,50,50); //defines the area this domain covers
	
	ColourMap domainsColourMap;
	
	public ColourMap getDomainsColourMap() {
		return domainsColourMap;
	}

	ArrayList<Location> AllDomainsLocations = new ArrayList<Location>();

	Color BaseColour;
	
	
	/**
	* all the domains in the game 
	* The core game has one local domain, with other peoples domains acting as "game expansions"
	* Anyone can add to the game! (provided I link to it, or some existing domain I like to links to it...etc)
	* The MEDomain object contains the database URL, which is the core data the game is based on **/
	public final static HashSet<MEDomain> knownDomains = new HashSet<MEDomain>();
	       		
	
	private  MEDomain(String DomainsDataBaseURL,Rectangle coversArea,Color BaseColour){
		
		this.coversArea=coversArea;
		this.DomainsDataBaseURL=DomainsDataBaseURL;
		this.BaseColour = BaseColour;
		//creates its color map
		domainsColourMap = new ColourMap(coversArea,BaseColour);
		
	}

	/** creates a new domain from the supplied url.
	 * in future this will get a safe location and a new base color for it.
	 * 
	 * NOTE: THIS DOES NOT HANDLE THE DOMAINS DATABASE. Load that into the semantics separately.
	 * Everytime a new domain is found it will have a new .ntlist to load
	 * In future I might deal with that here as well **/
	public static MEDomain createNewDomain(String databaseurl) {
		
		Color baseColor = new Color(0.0f,0.0f,0.5f,1.0f); //blue for now (temp)
		Rectangle locationsArea = new Rectangle(-750,-750,1500,1500); //locations area (temp)

		MEDomain newdomain = new MEDomain(databaseurl,locationsArea,baseColor);
		
  	    knownDomains.add(newdomain);
  	  
		return newdomain;
		
	}

	/** sets this domain as the home one.
	 * This should never be changed after its set **/
	public static void setAsHomeDomain(MEDomain homedomain) {
		if (MEDomain.homeDomain==null){
			MEDomain.homeDomain = homedomain;
		}
		
		
	}
	
	public static MEDomain getHomeDomain() {
		return homeDomain;
	}
	

	public String getDomainsDataBaseURL() {
		return DomainsDataBaseURL;
	}
	
	@Override
	public String toString(){
		return DomainsDataBaseURL+" ( which is "+BaseColour+")";
	}

	public static boolean domainloaded(String databaseurl) {
		
		for (MEDomain domainToTest : knownDomains) {
			
			if(domainToTest.getDomainsDataBaseURL().equalsIgnoreCase(databaseurl)){
				return true;
			}
			
		}
		
		return false;
	}

	public static Color getColorForPosition(Vector3 position) {
		
		
		Color colorAtThisPosition = currentDomain.domainsColourMap.getColourAtPosition(new Vector2(position.x,position.y)); //its only 2d, ignore position.z

		//Gdx.app.log(logstag,"colorAtThisPosition="+colorAtThisPosition);
		
		

		return colorAtThisPosition;
	}

	public static void setCurentDomain(MEDomain domain) {
		
		currentDomain = domain;
		
	}

}
