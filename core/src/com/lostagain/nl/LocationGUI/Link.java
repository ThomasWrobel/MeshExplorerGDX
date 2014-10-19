package com.lostagain.nl.LocationGUI;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.GenericProgressMonitor;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;

import java.util.logging.Logger;


public class Link extends WidgetGroup implements GenericProgressMonitor{

	static Logger Log = Logger.getLogger("ME.Link");
	
	Label gotoLinkButton =  new Label("",DefaultStyles.linkstyle);
	
	String Location = "";
	String LinkName = "";
	
	ModelInstance Linksline;
	//Label ProgressBar = new Label ("--->",DefaultStyles.linkstyle);
	ProgressBar scanPercentage = new ProgressBar(0, 100, 1, false, DefaultStyles.linkstyle);
	
	final static String UNKNOWEN = "UNKNOWEN- ";
	final static String SCANNING = "SCANNING- ";
	int PercentageScanned = 0;
	
	final static String LOCKED = "LOCKED ( ";
	
	//the current parent panel this is being viewed on.
	//(this determines where to send the clicks to start scanning,
	//as scanning is controlled by the parent panel)
	LinksScreen currentParent = null;
	
	enum LinkMode {
		Unknown,Scanning,Locked,Open;
	}

	LinkMode currentMode =  LinkMode.Unknown;
	boolean ComputerOpen;
	SSSNode linksToThisPC;

	private double TOTAL_LOAD_UNITS=1;
	private double LOAD_PROGRESS=0;

	private boolean realscan=false;
	int RealScanAmount =0;
	private static long scanStartTime=0l;

	public Link(String name,final String location,LinksScreen parent, boolean ComputerOpen) {

		//super(AxisLayout.horizontal().gap(5));
		//super(new AbsoluteLayout());
		
		setup(name, location, parent, ComputerOpen);	
		
		
		//super.add(gotoLinkButton);


	}

	private void setup(String name, final String location, LinksScreen parent,
			boolean ComputerOpen) {

		this.ComputerOpen=ComputerOpen;
		currentParent = parent; //should be changed if viewed from elsewhere
			
		super.setFillParent(true);
		super.setDebug(true, true);
	
		
		//default back
		//super.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255, 250,50, 55))));
		
		Location = location;
		
		//remove start if too long
		if (Location.length()>33){
			Location="..."+Location.substring(Location.length()-33, Location.length() );
		}
		
		
		LinkName = name;

		gotoLinkButton.setX(0);
		scanPercentage.setX(0);
		gotoLinkButton.setText(UNKNOWEN+Location);
	
		gotoLinkButton.setFillParent(true);
		gotoLinkButton.setWidth(44);
		
		scanPercentage.setFillParent(true);
		
		
		super.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				

					Log.info("trying to go to:"+linksToThisPC.toString());
							

					switch (currentMode)
					{
					case Unknown:
						 scan();
						break;
					case Locked:
						//as we already know its locked, we could probably
						//put a flag here to stop a re-check later?
						MainExplorationView.gotoLocation(linksToThisPC);
						break;
					case Open:
						//but probably not here - a locked pc deserves a double checked no?
						MainExplorationView.gotoLocation(linksToThisPC);
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

		Log.info(":"+linksToThisPC.toString());
		
		if (ComputerOpen){
			refreshBasedOnMode();
		}	
		
	}

	public Link(SSSNode sssNode, LinksScreen parent) {
		
		
		linksToThisPC=sssNode;
		

		//check if already known to be open
		Boolean unlocked = PlayersData.isLinkUnlockedByPlayer(linksToThisPC);
		
		
		setup(sssNode.getPLabel(),sssNode.getPURI(),parent, unlocked);	
		
	}

	private void scan(){
		
		Log.info("___________________________________sc");
		
		
		currentMode = LinkMode.Scanning;
		
		gotoLinkButton.setText(SCANNING+Location);
		gotoLinkButton.pack();
		//super.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255, 50,50, 255))));
		
	//	ProgressBar.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255, 250,50, 55))));


		
		//check if needs a new database loaded
		Boolean newDatabaseLoading=ME.checkForUnloadedDatabase(linksToThisPC);
		
		if (!newDatabaseLoading){
			
			Log.info("triggering scan");
			currentParent.startScanningLink(this);
			
		} else {

			Log.info("triggering remote scan");
			realscan=true;
			currentParent.startScanningLink(this);
			//we use the link as a loading bar for the real remote file!
			//in future we need to do this separately as many bars could be loading remote
			//sources at once
			SuperSimpleSemantics.setGenericLoadingMonitor(this);
			
		}

		scanStartTime = TimeUtils.millis();

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
	public void setScanningAmount(int Percentage){
		
		//float pixals = (super.size().width()/100)*Percentage;
	//	ProgressBar.setText("-"+Percentage);
		if (!realscan){
			int combined = (RealScanAmount+Percentage/2);
			scanPercentage.setValue(combined);
				
		} else {
			scanPercentage.setValue(Percentage);
		}
		
		
		
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

			setScanningAmount(PercentageScanned);
			scanComplete();
			
		} else {
		
			setScanningAmount(PercentageScanned);
		}
		
	}

	private void scanComplete() {
		ComputerOpen = true;
		
		//pre-prepare location
		//LocationContainer newlocation =  LocationContainer.getLocation(linksToThisPC);
		
		//detect if its secured by anything 
		if (linksToThisPC!=null){
			
			Log.info("-------------------------------detecting security");
			
			ArrayList<SSSNode> allSecuredPCs = SSSNodesWithCommonProperty.getAllNodesWithPredicate(StaticSSSNodes.SecuredBy);

			Log.info("-------------------------------allSecuredPCs="+allSecuredPCs.size());
			
			
			if (allSecuredPCs.contains(linksToThisPC)){
				
				ComputerOpen = false;

				Log.info("-------------------------------ComputerOpen="+ComputerOpen);
				
			} else {

				//add too unlocked list
				PlayersData.addUnlockedLink(linksToThisPC);
				
			}
						
		}

		
		
		
		refreshBasedOnMode();
		
		
	}

	void reCheckLinkLine() {
		if (Linksline==null){
							
			LocationContainer newlocation =  LocationContainer.getLocation(linksToThisPC);
		
			LocationContainer from = currentParent.parentLocationContainer;
			LocationContainer to = newlocation;
			
			//only refresh if the To and From are attached
			if (MainExplorationView.gameStage.getActors().contains(from, true) && MainExplorationView.gameStage.getActors().contains(to, true))			
			{
				Linksline = MainExplorationView.background.addConnectingLine(from,to);
			}
		
		}
	}
	
	public void stepForwardDownloadingAmount(int SPEEDSTEP) {
		
		PercentageScanned = PercentageScanned + SPEEDSTEP;
		Log.info("current percentage="+PercentageScanned);
		
		
		if (PercentageScanned>=100) {
			
			PercentageScanned = 100;		
			setScanningAmount(PercentageScanned);
			scanComplete();
			
		} else {
		
			setScanningAmount(PercentageScanned);
			
		}
		
		
	}
	
	
	
	public void refreshBasedOnMode() {
		
		if (!ComputerOpen){
			currentMode = LinkMode.Locked;
			setLockedStyle();
			gotoLinkButton.setText(LOCKED+Location+" )");
			gotoLinkButton.setColor( 220,0, 10, 30);

		} else {
			
			currentMode = LinkMode.Open;
			
			setOpenStyle();
			gotoLinkButton.setText(Location);
			gotoLinkButton.setAlignment(Align.center);
			gotoLinkButton.setColor(0, 210, 10, 40);
			
			
		}

		gotoLinkButton.pack();
		
		if (currentMode == LinkMode.Locked || currentMode == LinkMode.Open){

			Log.info("rechecking link lines");
			reCheckLinkLine();
		}
		
	}

	
	//The following commands are too reflect real file loading from ntlists on remote servers
	
	@Override
	public void setTotalProgressUnits(int i) {
		// TODO Auto-generated method stub
		TOTAL_LOAD_UNITS = i;
		
		updatePercentageScanned();
		
	}

	@Override
	public void addToTotalProgressUnits(int i) {
		
		TOTAL_LOAD_UNITS = TOTAL_LOAD_UNITS + i;
		updatePercentageScanned();
	}

	private void updatePercentageScanned() {
		
		//Note: We use "floor" here to ensure we are as pessimistic as possible when determining loading
		//Thus 99.9% isn't assumed to be 100% and thus triggering loaded complete!
		//PercentageScanned = (int) Math.floor(100.0*(LOAD_PROGRESS/TOTAL_LOAD_UNITS));
		RealScanAmount = (int) Math.floor(100.0*(LOAD_PROGRESS/TOTAL_LOAD_UNITS));
		Log.info("updatePercentageScanned = "+PercentageScanned);
		
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
			

		updatePercentageScanned();
	}

	@Override
	public void setCurrentProgress(int i) {
		LOAD_PROGRESS = i;

		updatePercentageScanned();
	}
	
	
	
}
