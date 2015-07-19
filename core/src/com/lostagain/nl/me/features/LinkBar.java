package com.lostagain.nl.me.features;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Align;
import com.darkflame.client.interfaces.GenericProgressMonitor;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.DeckPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.locationFeatures.LocationsHub;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.newmovements.PosRotScale;

class LinkBar extends DeckPanel implements GenericProgressMonitor, hitable {
	final static String logstag = "ME.LinkBar";

	SSSNode linksToThisPC;
	
	//logic
	enum LinkMode {
		Unknown,Scanning,Closed,Open;
	}
	LinkMode currentMode =  LinkMode.Unknown;
	


	String LocationsName = "";
	
	int PercentageScanned = 0;
	
	
	private double TOTAL_LOAD_UNITS=1;
	private double LOAD_PROGRESS=0;

	/** Determines if this is a link to a real url or just an internal fake link from one local location to another**/
	private boolean realURLLink=false;

	private LinkStoreObject parentLinkStore;

	/** Necessary as part of hit detection **/
	private float lastHitDistance; 
	
	//style
	static final float StandardWidth = 300;

	final static String CLOSED   = "CLOSED ( ";
	final static String UNKNOWEN = "UNKNOWEN- ";
	final static String SCANNING = "SCANNING- ";
	
	//bits
	ProgressBar scanbar = new ProgressBar(30,10,StandardWidth-10);
	Label gotoLinkLabel = new Label("goto");

	ModelInstance Linksline;

	
	
	public LinkBar(SSSNode sssNode, LinkStoreObject parent){
		super(StandardWidth,30);
		this.parentLinkStore = parent;
		
		this.setBackgroundColor(Color.BLUE);
		scanbar.setValue(55);
		Gdx.app.log(LinkStoreObject.logstag,"adding scan bar widget.");
		
		super.add(scanbar);

		LocationsName = sssNode.getPLabel();
		
		Label nameLabel = new Label(LocationsName);
		nameLabel.setLabelBackColor(Color.CLEAR);	
		//testLabelLala.setAlignment(MODELALIGNMENT.TOPLEFT);
		super.add(nameLabel);
		

		
		//remove start if too long
		if (LocationsName.length()>33){
			LocationsName="..."+LocationsName.substring(LocationsName.length()-33, LocationsName.length() );
		}
		
	}
	
	/** rechecks the current state of this Link from the players database
	 * Is it already scanned? is the location it links too open?
	 * Updates "currentMode" variable  to reflect change **/
	private Boolean updateMode() {
		Boolean isScanned = PlayersData.hasScanned(linksToThisPC);
		if (isScanned){
			currentMode=LinkMode.Closed; //closed by default, as its already scanned
		}

		//check if already known & open/unlocked
		Boolean unlocked = PlayersData.isLinkUnlockedByPlayer(linksToThisPC);
		if (unlocked){
			currentMode=LinkMode.Open;
			//ComputerOpen = true;
		}
		return isScanned;
	}
	
	private void setLockedStyle(){
		setBackgroundColor(Color.RED);
	}
	
	private void setOpenStyle(){
		setBackgroundColor(Color.GREEN);
	}
	private void setUnknownStyle(){
		setBackgroundColor(Color.BLUE);
	}
	
	public void refreshBasedOnMode() {
		
		if (currentMode == LinkMode.Unknown){
			setUnknownStyle();
			return;
		}
		if (currentMode == LinkMode.Scanning){
			return;
		}
		if (currentMode == LinkMode.Closed){
			currentMode = LinkMode.Closed;
			setLockedStyle();
			gotoLinkLabel.setText(CLOSED+LocationsName+" )");
			//gotoLinkButton.setColor( 220,0, 10, 30);

		} 
		
		if (currentMode == LinkMode.Open) {
			
			currentMode = LinkMode.Open;
			
			setOpenStyle();
			gotoLinkLabel.setText(LocationsName);
			//gotoLinkButton.setAlignment(Align.center);
			//gotoLinkButton.setColor(0, 210, 10, 40);
			
			
		}

		if (currentMode == LinkMode.Closed || currentMode == LinkMode.Open){

			Gdx.app.log(logstag,"rechecking link lines for link to:"+linksToThisPC);
			reCheckLinkLine();
		}
		
	}
	
	void reCheckLinkLine() {
		boolean loaded = true;
		
		Gdx.app.log(logstag,"Checking Link Line:");
		
		if (realURLLink){
			
			Gdx.app.log(logstag,"testing reallink if any databases are still needed to be loaded before enableing link and new location");
			loaded = ME.checkDatabaseIsLoaded(linksToThisPC); //ensures any databases at this points too are loaded
			Gdx.app.log(logstag,"Loaded:"+loaded);
		}
		
		if (Linksline==null && loaded ){
							

			Gdx.app.log(logstag,"Checking Link Line 2:");
			
			Location newlocation =  Location.getLocation(linksToThisPC);
			
		
			LocationHub from = parentLinkStore.parentLocation;
			
			LocationHub to = newlocation.locationsNEWHub;
			//TODO: Draw line between above
		
		}
	}
	
	private void scanComplete() {
		//ComputerOpen = true;
		
		//add to scanned list so it wont be needed to be scanned again
		PlayersData.addToScannedLocations(linksToThisPC);
		
		//pre-prepare location ?
		//LocationContainer newlocation =  LocationContainer.getLocation(linksToThisPC);
		
		//detect if its secured by anything 
		if (linksToThisPC!=null){
			
			Gdx.app.log(logstag,"-------------------------------detecting security");
			
			ArrayList<SSSNode> allSecuredPCs = SSSNodesWithCommonProperty.getAllCurrentNodesWithPredicate(StaticSSSNodes.SecuredBy);

			//Gdx.app.log(logstag,"-------------------------------allSecuredPCs="+allSecuredPCs.size());
			//Gdx.app.log(logstag,"-------------------------------allSecuredPCs="+allSecuredPCs.toString());
			
			
			if (allSecuredPCs.contains(linksToThisPC)){				
				//ComputerOpen = false;
				currentMode = LinkMode.Closed;				
				
			} else {
				
				currentMode = LinkMode.Open;
				//add too unlocked list
				PlayersData.addUnlockedLink(linksToThisPC);
				
			}
						
		}

		//refresh link appearance based on current LinkMode
		refreshBasedOnMode();
		
		//do the same for other matching links in game (so scanning one copy opens all pointing to the same place)
		if (currentMode == LinkMode.Closed || currentMode == LinkMode.Open){
			
			
			
			
			//find other hubs with links to linksToThisPC
			
			//first we get the hubs specifically connected to linksToThisPC		
			//ie connectedTo MysteriousGateway
			SSSNodesWithCommonProperty hubsLinkingToSame =  SSSNodesWithCommonProperty.getSetFor(StaticSSSNodes.CONNECTEDTO, linksToThisPC); 
					
			//we should also add hubs connected to everyone
			//me:connectedto=me:everyone
			//because these hubs will obviously point to linksToThisPC too
			SSSNodesWithCommonProperty hubsLinkingToEveryone =  SSSNodesWithCommonProperty.getSetFor(StaticSSSNodes.CONNECTEDTO, StaticSSSNodes.EVERYONE); 
			
			//combine the sets into a newone (note; we dont want to change the above sets!)
			HashSet<SSSNode> hubsLinkingToSamePlace = new HashSet<SSSNode>();
			if(hubsLinkingToSame!=null){
				hubsLinkingToSamePlace.addAll(hubsLinkingToSame);
			}
			if(hubsLinkingToEveryone!=null){
				hubsLinkingToSamePlace.addAll(hubsLinkingToEveryone);
			}
			
			if (hubsLinkingToSamePlace.isEmpty()){
				Gdx.app.log(logstag,"Error with hubs connected to query, no results found but there should be at least one");
			}
			
			
			Gdx.app.log(logstag,"Hubs linking to "+linksToThisPC+":"+hubsLinkingToSamePlace+" ");	
			
			//trigger refresh on their links page (slightly inefficient as it checks all the links on that machine, rather then just the
			//one that changed)
			//SSSNodesWithCommonProperty can be iterated over directly as its ultimately just a a set of nodes
			for (SSSNode hubsAlsoWithLinksSSSNode : hubsLinkingToSamePlace) {

				//note should exclude self from link refresh
				if (hubsAlsoWithLinksSSSNode==linksToThisPC){
					continue;
				}
				
				Gdx.app.log(logstag,"(Getting hub for SSSNode:"+hubsAlsoWithLinksSSSNode+" ");				
				LocationsHub hubWithLink = Location.getExistingHub(hubsAlsoWithLinksSSSNode);
				
				if(hubWithLink==null){
					//if none found to update we return
					Gdx.app.log(logstag,"(no existing hubs found to update)");										
					continue;					
				} else {
					Gdx.app.log(logstag,"(updating links on hub :"+hubWithLink.LocationsNode+" ");		
				}
				
				//now tell that page to rechecks all its links and lines
				hubWithLink.linksPage.recheckLinksAndLines();
				
			}
			
		}
		
		
		
		
		
	}
	
	@Override
	public void setTotalProgressUnits(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToTotalProgressUnits(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCurrentProcess(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stepProgressForward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCurrentProgress(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PosRotScale getTransform() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fireTouchDown() {
		Gdx.app.log(logstag,"touchdown on linkbar object");
	}

	@Override
	public void fireTouchUp() {
		Gdx.app.log(logstag,"touchdown up linkbar object");
	}

	@Override
	public void fireDragStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLastHitsRange(float range) {
		lastHitDistance = range;
		
	}

	@Override
	public float getLastHitsRange() {
		return lastHitDistance;
	}

	@Override
	public boolean isBlocker() {
		return true;
	}

	@Override
	public boolean rayHits(Ray ray) {
		boolean hit = Intersector.intersectRayBoundsFast(ray, this.getLocalCollisionBox());
		Gdx.app.log(logstag,"testing for hit on linkbar object:"+hit);
		return hit;
	}
	
}