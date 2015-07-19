package com.lostagain.nl.me.features;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.GenericProgressMonitor;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.ME;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.DeckPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.gui.ScanManager;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.locationFeatures.LocationsHub;
import com.lostagain.nl.me.models.hitable;

/**
 * Controls the look and status of a single link
 * 
 * @author Tom
 */
class LinkBar extends DeckPanel implements GenericProgressMonitor, hitable {
	final static String logstag = "ME.LinkBar";

	SSSNode linksToThisPC;
	
	//logic
	enum LinkMode {
		Unknown,Scanning,Closed,Open;
	}
	LinkMode currentMode =  LinkMode.Unknown;
	


	String LocationsName = "";
	
	//int PercentageScanned = 0;	
	
	private double TOTAL_LOAD_UNITS=1;
	private double LOAD_PROGRESS=0;

	/** Determines if this is a link to a real url or just an internal fake link from one local location to another**/
	private boolean realURLLink=false;

	private LinkStoreObject parentLinkStore;

	
	//style
	static final float StandardWidth = 300;

	final static String CLOSED   = "CLOSED ( ";
	final static String UNKNOWEN = "UNKNOWEN- ";
	final static String SCANNING = "SCANNING- ";
	
	//bits
	ProgressBar scanbar = new ProgressBar(30,5,StandardWidth-10);
	Label gotoLinkLabel = null;

	ModelInstance Linksline;

	
	
	public LinkBar(SSSNode targetPC, LinkStoreObject parent){
		super(StandardWidth,30);
		this.parentLinkStore = parent;
		LocationsName = targetPC.getPLabel();
	
		//remove start if too long
		if (LocationsName.length()>33){
			LocationsName="..."+LocationsName.substring(LocationsName.length()-33, LocationsName.length() );
		}
		linksToThisPC = targetPC;
		
		scanbar.setValue(1);
		Gdx.app.log(LinkStoreObject.logstag,"adding scan bar widget.");
		scanbar.getStyle().setBackgroundColor(new Color(1f,0f,0f,0.8f));
		
				
		super.add(scanbar);

		gotoLinkLabel = new Label(LocationsName);
		gotoLinkLabel.setToscale(new Vector3(0.6f,0.6f,0.6f));
		
		gotoLinkLabel.setLabelBackColor(Color.CLEAR);
		super.add(gotoLinkLabel); 		
		setUnknownStyle();
		

		
		
		
		//ensure we are hitable
		super.setAsHitable(true);
		
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
		getStyle().setBackgroundColor(Color.RED);
		scanbar.hide();
		gotoLinkLabel.getStyle().setColor(Color.CYAN.cpy());
		gotoLinkLabel.getStyle().setShadowColor(Color.CYAN.cpy());
		
	}
	
	private void setOpenStyle(){
		getStyle().setBackgroundColor(Color.GREEN);
		getStyle().setBorderColor(Color.GREEN);
		scanbar.hide();
		gotoLinkLabel.getStyle().setColor(Color.CYAN.cpy());
		gotoLinkLabel.getStyle().setShadowColor(Color.CYAN.cpy());
	}
	private void setUnknownStyle(){
		getStyle().setBackgroundColor(Color.GRAY);
	
		gotoLinkLabel.getStyle().setColor(Color.GRAY.cpy());
		gotoLinkLabel.getStyle().setShadowColor(Color.BLACK.cpy());
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
			gotoLinkLabel.setText(CLOSED+""+LocationsName+" )");
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
		TOTAL_LOAD_UNITS = i;		//could probably put this inside the progress bar as internally it does much the same thing
		//updateSemanticScan();
	}

	@Override
	public void addToTotalProgressUnits(int i) {

		TOTAL_LOAD_UNITS = TOTAL_LOAD_UNITS + i;
		//updateSemanticScan();
	}

	@Override
	public void setCurrentProcess(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stepProgressForward() {
		LOAD_PROGRESS = LOAD_PROGRESS + 1;
		//updateSemanticScan();
	}

	@Override
	public void setCurrentProgress(int i) {
		LOAD_PROGRESS = i;

		if (!realURLLink){
			setStandardLinkScanningAmount((int)Math.floor(LOAD_PROGRESS));
		} else {
		//	updateSemanticScan();
		}
	}

	public void linkClicked(){
		Gdx.app.log(logstag,"trying to go to:"+linksToThisPC.getPURI());
		
		//double check if it was a realscan that the database is loaded (should not be needed if loading progress was handled correctly, which it currently is not)
		if (realURLLink){

			Gdx.app.log(logstag,"checking if url loaded:");
			
			
			boolean loaded = ME.checkDatabaseIsLoaded(linksToThisPC);
			
			if (loaded==false){

				Gdx.app.log(logstag,"canceling click as database not loaded");
				return;
			}
			
		}
		
		
		switch (currentMode)
		{
		case Unknown:
			 requestScan();
			break;
		case Closed:
			//as we already know its locked, we could probably
			//put a flag here to stop a re-check later?
			ME.gotoLocation(linksToThisPC);
			break;
		case Open:
			//but probably not here - a locked pc deserves a double checked no?
			ME.gotoLocation(linksToThisPC);
			break;
		case Scanning:
			break;
		default:
			break;


		}
	}
	
	
	
private void requestScan(){
		
		//super.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255, 50,50, 255))));
		
	//	ProgressBar.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255, 250,50, 55))));


		
		//check if needs a new database loaded
		Boolean newDatabaseLoading=ME.checkForUnloadedDomainAndLoad(linksToThisPC);
		
		boolean successfullyStarted = false;
		
		if (!newDatabaseLoading){
			
			Gdx.app.log(logstag,"triggering scan");
			
			//currentParent.startScanningLink(this); Linkscreen used to handle scans. Now its handled by the scan manager
			successfullyStarted = ScanManager.addNewScan(this); //will add a new scan to start scanning
			
			
			realURLLink=false;
			
			
		} else {

			Gdx.app.log(logstag,"triggering remote scan");
			
			realURLLink=true;

			this.setStandardLinkScanningAmount(0);
			
			//currentParent.startScanningLink(this);
			
			//we use the link as a loading bar for the real remote file!
			//in future we need to do this separately as many bars could be loading remote
			//sources at once
			SuperSimpleSemantics.setGenericLoadingMonitor(this);
			
			//assume true for now (the real url scan needs replacing really)
			successfullyStarted = true;
		}

		if (successfullyStarted){

			Gdx.app.log(logstag,"___________________________________starting link scan");
						
			currentMode = LinkMode.Scanning;
						
			gotoLinkLabel.setText(SCANNING+":"+LocationsName);
			
			Gdx.app.log(logstag,"_____________n width:"+gotoLinkLabel.getLocalBoundingBox().getWidth());
			Gdx.app.log(logstag,"_____________n height:"+gotoLinkLabel.getLocalBoundingBox().getHeight());
		}
		
		
		

	}
	
//used to indicate the link is being scanned
//speed is based on Node timed security / scanner speed
public void setStandardLinkScanningAmount(int Percentage){
	
	if (!realURLLink){
		scanbar.setValue(Percentage);
			
	} 
	
	//if percentage is 100% we fire competition
	if (Percentage>=100)
	{
		this.scanComplete();
	}
	
	
}
	@Override
	public void fireTouchDown() {
		Gdx.app.log(logstag,"touchdown on linkbar object");
		linkClicked();
		
		
	}

	@Override
	public void fireTouchUp() {
		Gdx.app.log(logstag,"touchdown up linkbar object");
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