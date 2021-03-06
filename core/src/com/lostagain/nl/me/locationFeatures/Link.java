package com.lostagain.nl.me.locationFeatures;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.GenericProgressMonitor;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.gui.ScanManager;
import com.lostagain.nl.me.models.MessyModelMaker;

import java.util.logging.Logger;


public class Link extends WidgetGroup implements GenericProgressMonitor {

	static Logger Log = Logger.getLogger("ME.Link");
	final static String logstag = "ME.Link";
			
	Label gotoLinkButton =  new Label("",DefaultStyles.linkstyle);
	
	String LocationsName = "";
	String LinkName      = "";
	
	ModelInstance Linksline;
	//Label ProgressBar = new Label ("--->",DefaultStyles.linkstyle);
	ProgressBar scanPercentage = new ProgressBar(0, 100, 1, false, DefaultStyles.linkstyle);
	
	final static String UNKNOWEN = "UNKNOWEN- ";
	final static String SCANNING = "SCANNING- ";
	int PercentageScanned = 0;
	
	final static String CLOSED = "CLOSED ( ";
	
	//the current parent panel this is being viewed on.
	//(this determines where to send the clicks to start scanning,
	//as scanning is controlled by the parent panel)
	LinksScreen currentParent = null;
	
	enum LinkMode {
		Unknown,Scanning,Closed,Open;
	}

	LinkMode currentMode =  LinkMode.Unknown;
//	boolean ComputerOpen;
	SSSNode linksToThisPC;

	private double TOTAL_LOAD_UNITS=1;
	private double LOAD_PROGRESS=0;

	/** Determines if this is a link to a real url or just an internal fake link from one local location to another**/
	private boolean realURLLink=false;
	
	int RealScanAmount =0;
	private static long scanStartTime=0l;

	/*
	public Link(String name,final String location,LinksScreen parent, boolean ComputerScanned) {

		//super(AxisLayout.horizontal().gap(5));
		//super(new AbsoluteLayout());
		
		setup(name, location, parent, ComputerScanned);	
		
		
		//super.add(gotoLinkButton);


	}*/
	
	public Link(SSSNode sssNode, LinksScreen parent) {
		
		
		linksToThisPC=sssNode;
		
		//check if computer is known
		Boolean isScanned = updateMode();
		
		setup(sssNode.getPLabel(),sssNode.getPURI(),parent, isScanned);	
		
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
	
	private void setup(String name, final String location, LinksScreen parent,
			boolean DestinationScannedAlready) {

		//this.ComputerOpen=DestinationScannedAlready;
		
		currentParent = parent; //should be changed if viewed from elsewhere
			
		super.setFillParent(true);
		super.setDebug(true, true);
	
		
		//default back
		//super.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255, 250,50, 55))));
		
		LocationsName = location;
		
		//remove start if too long
		if (LocationsName.length()>33){
			LocationsName="..."+LocationsName.substring(LocationsName.length()-33, LocationsName.length() );
		}
		
		
		LinkName = name;

		gotoLinkButton.setX(0);
		scanPercentage.setX(0);
		gotoLinkButton.setText(UNKNOWEN+LocationsName);
	
		gotoLinkButton.setFillParent(true);
		gotoLinkButton.setWidth(44);
		
		scanPercentage.setFillParent(true);
		
		
		super.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				

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
				
						
		});
		
		super.addActor(scanPercentage);	
		
		super.addActor(gotoLinkButton);

		Gdx.app.log(logstag,":"+linksToThisPC.toString());
		
		//if its already scanned we refresh based on mode
		if (DestinationScannedAlready){
			refreshBasedOnMode();
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
			
			gotoLinkButton.setText(SCANNING+LocationsName);
			gotoLinkButton.pack();
			
			scanStartTime = TimeUtils.millis();
	
			
		}
		
		
		

	}
	
	@Override
	public void validate() {
		
		scanPercentage.validate();
		//gotoLinkButton.validate();
		gotoLinkButton.pack();
	}
	
	private void setLockedStyle(){
		
		//super.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255,255,30,20))));
		scanPercentage.setVisible(false);
		
	}
	private void setScanningStyle(){
		
	}
	
	private void setOpenStyle(){
		
		//super.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255,0,30,200))));
		scanPercentage.setVisible(false);
	}
	
	

	//used to indicate the link is being scanned
	//speed is based on Node timed security / scanner speed
	public void setStandardLinkScanningAmount(int Percentage){
		
		//float pixels = (super.size().width()/100)*Percentage;
	//	ProgressBar.setText("-"+Percentage);
		if (!realURLLink){
			//int combined = (RealScanAmount+Percentage/2);
			scanPercentage.setValue(Percentage);
				
		} 
		
		//if percentage is 100% we fire competition
		if (Percentage>=100)
		{
			this.scanComplete();
		}
		
		//not used anymore
		//else {
		//	int combined = (RealScanAmount+Percentage)/2;
		//	scanPercentage.setValue(RealScanAmount);
		//}
		
		
		
	}
	//node is locked by Security (only known after a scan)
	public void setLocked(){

	}
	//node is unlocked and can be accessed
	public void setOpen(){

	}
	public void setEnable(){
	//	gotoLinkButton.setDisabled(false);

	}

	public void setDisable(){
		//gotoLinkButton.setDisabled(true);
	}

	
	public void stepForwardScanningAmount(int SPEEDSTEP) {
		
		PercentageScanned = PercentageScanned + SPEEDSTEP;
		
		if (PercentageScanned>=100) {
			PercentageScanned = 100;

			setStandardLinkScanningAmount(PercentageScanned);
			scanComplete();
			
		} else {
		
			setStandardLinkScanningAmount(PercentageScanned);
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
				LocationsHub_old hubWithLink = Location.getExistingHub(hubsAlsoWithLinksSSSNode);
				
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
			
		
			LocationsHub_old from = currentParent.parentLocationContainer;
			LocationsHub_old to = newlocation.locationsHub;
			
			//only refresh if the To and From are attached
			if (MainExplorationView.gameStage.getActors().contains(from, true) && MainExplorationView.gameStage.getActors().contains(to, true))			
			{

				Gdx.app.log(logstag,"Adding new connecting line");
				Linksline = MessyModelMaker.addConnectingLine(from,to);
			} else {
				Gdx.app.log(logstag,"location not attached canr make link line");
			}
		
		}
	}
	
	public void stepForwardDownloadingAmount(int SPEEDSTEP) {
		
		PercentageScanned = PercentageScanned + SPEEDSTEP;
		Gdx.app.log(logstag,"current percentage="+PercentageScanned);
		
		
		if (PercentageScanned>=100) {
			
			PercentageScanned = 100;		
			setStandardLinkScanningAmount(PercentageScanned);
			scanComplete();
			
		} else {
		
			setStandardLinkScanningAmount(PercentageScanned);
			
		}
		
		
	}
	
	/** checks if the lines state has changed due to database changes (eg, the location being scanned elsewhere)
	 * It then refreshs the mode if its changed **/
	public void recheckAndRefresh() {
		
		LinkMode oldmode = currentMode;
		updateMode();
		if (currentMode!=oldmode){
			refreshBasedOnMode(); 
		}
		
	}
	
	public void refreshBasedOnMode() {
		
		if (currentMode == LinkMode.Unknown){
			return;
		}
		if (currentMode == LinkMode.Scanning){
			return;
		}
		if (currentMode == LinkMode.Closed){
			currentMode = LinkMode.Closed;
			setLockedStyle();
			gotoLinkButton.setText(CLOSED+LocationsName+" )");
			gotoLinkButton.setColor( 220,0, 10, 30);

		} 
		
		if (currentMode == LinkMode.Open) {
			
			currentMode = LinkMode.Open;
			
			setOpenStyle();
			gotoLinkButton.setText(LocationsName);
			gotoLinkButton.setAlignment(Align.center);
			gotoLinkButton.setColor(0, 210, 10, 40);
			
			
		}

		gotoLinkButton.pack();
		
		if (currentMode == LinkMode.Closed || currentMode == LinkMode.Open){

			Gdx.app.log(logstag,"rechecking link lines for link to:"+linksToThisPC);
			reCheckLinkLine();
		}
		
	}

	
	//The following commands are too reflect real file loading from ntlists on remote servers
	
	@Override
	public void setTotalProgressUnits(int i) {
		
		TOTAL_LOAD_UNITS = i;		
		updateSemanticScan();
		
	}

	@Override
	public void addToTotalProgressUnits(int i) {	
		
		TOTAL_LOAD_UNITS = TOTAL_LOAD_UNITS + i;
		updateSemanticScan();
		
	}

	private void updateSemanticScan() {
		
		//Note: We use "floor" here to ensure we are as pessimistic as possible when determining loading
		//Thus 99.9% isn't assumed to be 100% and thus triggering loaded complete!
		//PercentageScanned = (int) Math.floor(100.0*(LOAD_PROGRESS/TOTAL_LOAD_UNITS));
		double percd=100.0*(LOAD_PROGRESS/TOTAL_LOAD_UNITS);
		RealScanAmount = (int) Math.floor(percd);
		
		Gdx.app.log(logstag,"updatePercentageScanned = "+RealScanAmount+" ("+LOAD_PROGRESS+"/"+TOTAL_LOAD_UNITS+")");
		
		
		if (RealScanAmount>=100) {
			Gdx.app.log(logstag," new database loaded ");
			
			RealScanAmount = 100;	
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
				public void run() {
					Gdx.app.log(logstag," final link updates ");
					setStandardLinkScanningAmount(RealScanAmount);
					scanComplete();

				}
			});
			
		} else {
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
				public void run() {
					setStandardLinkScanningAmount(RealScanAmount);
				}
			});
		}
		
		/*
		if (PercentageScanned>=100) {
			
			//PercentageScanned = 100;
			
			//setScanningAmount(PercentageScanned);
			
			scanComplete();
			
		} else {
		
			//setScanningAmount(PercentageScanned);
			
		}		
		*/
		
		
	}

	@Override
	public void setCurrentProcess(String message) {
		//No support for loading messages. At least, yet.
	}

	@Override
	public void stepProgressForward() {
		LOAD_PROGRESS = LOAD_PROGRESS + 1;
			

		updateSemanticScan();
	}

	@Override
	public void setCurrentProgress(int i) {
		
		LOAD_PROGRESS = i;

		if (!realURLLink){
			setStandardLinkScanningAmount((int)Math.floor(LOAD_PROGRESS));
		} else {
			updateSemanticScan();
		}
	
		
		
	}
	
	
	
}
