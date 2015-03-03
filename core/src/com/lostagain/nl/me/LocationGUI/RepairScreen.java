package com.lostagain.nl.me.LocationGUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;



















import javax.swing.GroupLayout.Alignment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.LocationGUI.ObjectFile.ObjectFileState;
import com.lostagain.nl.me.gui.DataObjectSlot;
import com.lostagain.nl.me.gui.Inventory;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.objects.DataObject;

/**
 * A repair screen displays the required nodes to unlock the location
 * Initially the player will think these are security screens, only later
 * realising they are simply broken and need repair to work
 * 
 * **/
public class RepairScreen extends Group  implements LocationScreen {

	//static Logger Log = Logger.getLogger("ME.RepairScreen");

	final static String logstag = "ME.RepairScreen";

	LocationsHub locationProtectedByThis;
	
	private SSSNode neededData;

	/** if the security is making the user pass a query, this string is the query **/
	private String QueryPass;
	
	/** number of items we need that meet that requirements **/
	private int NumberOfObjectNeeded = 1;
	
	
	/** if the security has a description for the question, this string is that description **/
	private String SecurityDiscription="";
	
	
	/** Acceptable results for query pass **/
	public ArrayList<SSSNode> acceptableAnswers = new ArrayList<SSSNode>();

	private boolean readyForAnswer = false; //is true when everything is loaded and is ready to accept an answer
		
	Label LocationClosedText = new Label ("DATA NODES REQUIRED : ", DefaultStyles.linkstyle);
	Label SupplyData = new Label ("Supply Needed NODES : ", DefaultStyles.linkstyle);
	
	Label RequirementsText = new Label ("Requirements : ", DefaultStyles.linkstyle);
	Label UnlockedLabel= new Label ("(LOCATION OPEN)", DefaultStyles.linkstyle);
	Label DetailsLabel= new Label ("(LOCATION OPEN)", DefaultStyles.linkstyle);
	
	ArrayList<ObjectRequester> allObjectsRequested = new ArrayList<ObjectRequester>();
	
	boolean needslayout = true;
	
	public RepairScreen(LocationsHub parentLocationContainer, SSSNode securedBy) {
		super();
		
	//	Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		locationProtectedByThis = parentLocationContainer;
		this.neededData = securedBy;
		
		
		//securedBy = securedByThis;
		if (securedBy!=null){
			
			setAsLocked();
			
		} else {
			setAsUnlocked();
		}
		
		
	}

	
	@Override
	public void validate() {
		
		Gdx.app.log(logstag,"validate on repair screen:");
		
		if (needslayout){
			layout();
			needslayout=false;
		}
		 
	}
	
	public void layout(){
		
		float HEIGHT = super.getParent().getHeight();
		float WIDTH = super.getParent().getWidth();
		
		super.setSize(WIDTH, HEIGHT);
		Gdx.app.log(logstag,"HEIGHT of repair screen:"+HEIGHT);
		
		LocationClosedText.setPosition(10, super.getHeight()-30);
		SupplyData.setPosition(10, super.getHeight()-50); 
		
		RequirementsText.setPosition(10, super.getHeight()-70);	
		
		UnlockedLabel.setAlignment(Align.center);		
	    UnlockedLabel.setPosition((getWidth()/2)-(UnlockedLabel.getWidth()/2),(getHeight()/2)+30);
	    
		DetailsLabel.setAlignment(Align.center);
		DetailsLabel.setPosition((getWidth()/2)-(DetailsLabel.getWidth()/2),(getHeight()/2)-30);
		
		Gdx.app.log(logstag,"getY(Align.center):"+getY(Align.center));
		
		int i=0;
		 for (ObjectRequester req : allObjectsRequested) {
			
			// req.setPosition(getCenterX()-75, getCenterY()-50);

				req.setPosition(25+(i*(req.getWidth()+10)),getY(Align.center));
				 i++;
		}
		
		
	}
	private void setAsUnlocked() {

		Gdx.app.log(logstag," repair screen setAsUnlocked:");
		locationProtectedByThis.unlockComputer();
		
		//add to players data as unlocked
		if (!(locationProtectedByThis.LocationsNode ==null)){
			PlayersData.addUnlockedLink(locationProtectedByThis.LocationsNode);
		}
		
		
		//remove existing text if present
		if (LocationClosedText!=null){
			super.removeActor(LocationClosedText);
			super.removeActor(SupplyData);
			super.removeActor(RequirementsText);
		}
		
		//get details		
		SSSNodesWithCommonProperty DiscriptionSet = SSSNodesWithCommonProperty.getSetFor(StaticSSSNodes.DescriptionOf, locationProtectedByThis.LocationsNode);
		
		Gdx.app.log(logstag,"Getting discription for location:"+locationProtectedByThis.LocationsNode.getPLabel());
		
		
		String Discription = "(No Details)";
		if (DiscriptionSet!=null)
		{
			 Discription = DiscriptionSet.get(0).getPLabel();
		}
		
		
		//add unlocked message and details
		UnlockedLabel = addText("(LOCATION UNLOCKED)",10,super.getHeight()-20);
		
		//add details
		DetailsLabel = addText(Discription,10,super.getHeight()-40);
		DetailsLabel.setWidth(300);
		
		
		needslayout=true;
		
	}



	private Label addText(String string, float x, float f) {
		
		
		Label textlabel = new Label(string,DefaultStyles.linkstyle);
		textlabel.setPosition(x, f);
		textlabel.setWrap(true);
		
		
		super.addActor(textlabel);
		

		needslayout=true;
		
		return textlabel;
		
		
	}


	private void addAnswerDropTargets() {
		
		//adds the area to click on to supply the answer
		//we might in future drag to this location if
		//that makes a more interesting interface
		int i=0;
		
		while(i<NumberOfObjectNeeded){
			
		
			ObjectRequester newObjRequester = new ObjectRequester(acceptableAnswers,this);
			
		
		allObjectsRequested.add(newObjRequester);
		
		newObjRequester.setPosition(20+(i*newObjRequester.getWidth()),getY(Align.center));
		
		super.addActor(newObjRequester);

		i++;
		}
		

		needslayout=true;
	}
	



	
	private void setAsLocked() {

		Gdx.app.log(logstag,"on repair screen setAsLocked:");
		//me:queryPass
		//add interface elements (non-dragable)
		
		
		// LockedText = addText("LOCATION LOCKED : ",10,super.getHeight()-10);
		 

		LocationClosedText.setPosition(10, super.getHeight()-30);
		SupplyData.setPosition(10, super.getHeight()-50); 
		
		 super.addActor(LocationClosedText);		 
		 super.addActor(SupplyData);
		 
		 
		 // RequirementsText = addText("Requirements Not Yet Met:", 10, super.getHeight()-	40);
		 RequirementsText.setText("Requirements Not Yet Met:");
		 
		 
		 Gdx.app.log(logstag,"__________________getting protection string_________________________");
		 
		//get protection string
		HashSet<SSSNodesWithCommonProperty> securitysPropertys = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(neededData.PURI);
		
		
		 Gdx.app.log(logstag,"__________________details of security: "+neededData.PURI+" = "+securitysPropertys.size()+" _________________________");
		 
		for (SSSNodesWithCommonProperty propertset : securitysPropertys) {
			
			 Gdx.app.log(logstag,"_____prop:"+propertset.getCommonPrec()+" = "+propertset.getCommonValue());
			
			if (propertset.getCommonPrec()==StaticSSSNodes.queryPass){
				QueryPass = propertset.getCommonValue().getPLabel();
				
			}
			
			
			//get the description, if any.			
			if (propertset.getCommonPrec()==StaticSSSNodes.clueText){
				SecurityDiscription = propertset.getCommonValue().getPLabel();				
			}
			
			//get the number of items that match we need (default 1)			
			if (propertset.getCommonPrec()==StaticSSSNodes.ReqNum){
				
				NumberOfObjectNeeded = Integer.parseInt(propertset.getCommonValue().getPLabel());
				
				
			}
			
		}
		
		String protectionString = QueryPass;
		
		if (protectionString!=null)
		{
			//strip quotes
			if (protectionString.startsWith("\"")){
				protectionString = protectionString.substring(1);
				
			}
			if (protectionString.endsWith("\"")){
				protectionString = protectionString.substring(0, protectionString.length()-1);
						
			}
		} else {

			Gdx.app.log(logstag,"warning protectionString is null!");
		}
		
		if (SecurityDiscription==""){
			RequirementsText = addText(protectionString, 10,super.getHeight()-90);
		} else {
			RequirementsText = addText(SecurityDiscription, 10,super.getHeight()-90); ;
			
		}
		
		retrieveAnswersAsycn(protectionString);
		
		
		addAnswerDropTargets(); 

		needslayout=true;
	}

	

	private void retrieveAnswersAsycn(String protectionString) {
		
		Gdx.app.log(logstag,"protectionString="+protectionString);
		
		//some debug tests
		
		//Note; Green does not exist at this point!
		
		//C:\TomsProjects\MeshExplorerV2\desktop\semantics\TomsNetwork.ntlist#green

	//	SSSNode greennode2  = SSSNode.getNodeByUri("C:\\TomsProjects\\MeshExplorerV2\\desktop\\semantics\\TomsNetwork.ntlist#green");
		Gdx.app.log(logstag,"_______total_______"+SSSNode.getAllKnownNodes().toString());
    	
		SSSNode greennode  = SSSNode.getNodeByLabel("green");
    	Gdx.app.log(logstag,"_______g_______"+greennode.getEquivilentsAsString());
    	Gdx.app.log(logstag,"_______g_______"+greennode.getPURI());
    	
    	SSSNode ColorNode = SSSNode.getNodeByLabel("color");    
    	Gdx.app.log(logstag,"______f_______|_"+ColorNode.getEquivilentsAsString());
    	Gdx.app.log(logstag,"______f_______|_"+ColorNode.getPURI());
		
		Query answers = new Query(protectionString);
		
		DoSomethingWithNodesRunnable RunWhenDone = new DoSomethingWithNodesRunnable(){

			@Override
			public void run(ArrayList<SSSNode> newnodes, boolean invert) {
				
				acceptableAnswers.clear();
				acceptableAnswers.addAll(newnodes);
				
				Gdx.app.log(logstag,"answers="+acceptableAnswers.toString());
				
				//flag as ready for answer
				readyForAnswer = true;
				
				
			}
			
		};
		
		QueryEngine.processQuery(answers, false, null, RunWhenDone);
		
		
		
	}



	

	
	
	
	
	private void rejectedAnsAnimation() {
		/*
		VisualGuide.setImage(redback);
		
		//delay before going back to norm
		GameTimer resetcolor = new GameTimer(550, new Callback<String>(){

			@Override
			public void onSuccess(String result) {
				VisualGuide.setImage(blueback);
				
			}

			@Override
			public void onFailure(Throwable cause) {
				
			}
			
		});
		*/
	//	resetcolor.start();
		//
	}
	
	
	private void acceptedAnsAnimation() {
		
	//	VisualGuide.setImage(greenback);
		
	}



	public SSSNode getSecuredBy() {
		return neededData;
	}



	public void setSecuredBy(SSSNode securedBy) {
		this.neededData = securedBy;
		
	}
	

	public void setAllRequestObjectsFaded(){
		
		for (ObjectRequester objreq : allObjectsRequested) {
			
			objreq.setModeFaded();
			
			
		}
	}
	
	public void testAllRequestedObjectS(){
		
		Boolean locked = false;
		
		for (ObjectRequester objreq : allObjectsRequested) {
			
			if (!objreq.isAnwsered()){
				locked=true;
			}
			
		}
		
		if (!locked){
			
			Gdx.app.log(logstag,"unlocking");			
			setAsUnlocked();

			Gdx.app.log(logstag,"setAllRequestObjectsFaded");	
			setAllRequestObjectsFaded();

			validate(); 
		}
		
	}
	
	
	
	/** a box that asks for an object in order to unlock.
	 * Once all these are unlocked, so is the location
	 * By default, there is only one lock per location. But
	 * there can be upto 6 **/
	static class ObjectRequester extends DataObjectSlot {

		Boolean isAnwsered = false;
		 RepairScreen sourcescreen;
		 
		public ObjectRequester(final ArrayList<SSSNode> acceptableAnswers,final RepairScreen source){
			
			setColor(0.8f, 0.1f, 0.1f, 1.0f);			
		//	super.setSize(100, 75);			
			super.setOrigin(Align.center);
			super.debug();
			sourcescreen = source;
			
			super.onDropRun(new OnDropRunnable() {
				
				@Override
				public void run(DataObject drop) {
					
					SSSNode ItemNode = drop.itemsnode;
					

					Gdx.app.log(logstag,"~~~~~~~~~~~~~~~~~~~item dropped uri="+ItemNode.getPURI());
					

//					
					
					
				}
				
			});
			
				
			
		}
		
			public void setModeFaded() {

				setColor(0.1f, 0.8f, 0.1f, 0.2f);
			}


			protected void setModeAccepted(SSSNode itemNode) {
				
				//this.setText(itemNode.getPLabel());
							
				setColor(0.1f, 0.8f, 0.1f, 1.0f);
										
				isAnwsered = true;

			}

			public boolean isAnwsered(){
				return isAnwsered;
			}
			
			@Override
			public boolean willAccept(DataObject object){
				
				Gdx.app.log(logstag,"~testing uri="+object.itemsnode.getPLabel());
				SSSNode ItemNode = object.itemsnode;
				
				//test					
				if (sourcescreen.acceptableAnswers.contains(ItemNode)){
										
					Gdx.app.log(logstag,"contains:"+sourcescreen.acceptableAnswers.contains(ItemNode));
		
					//ME.playersInventory.dropHeldItem(true);					
					setModeAccepted(ItemNode);
					
					//lock this (so they cant get it out again)
					super.lock();
					
					//remove from acceptable answers
					sourcescreen.acceptableAnswers.remove(ItemNode);
					sourcescreen.testAllRequestedObjectS();
					
					return true;
					
				} else {
					
					sourcescreen.rejectedAnsAnimation();		
					return false;
					//ME.playersInventory.dropHeldItem(true);
				}
			}
		
		
	}
	
	
	
	/** a box that asks for an object in order to unlock.
	 * Once all these are unlocked, so is the location
	 * By default, there is only one lock per location. But
	 * there can be upto 6 **/
	static class ObjectRequesterOLD extends Label {
		
		
		Boolean isAnwsered = false;
		
		public ObjectRequesterOLD(final ArrayList<SSSNode> acceptableAnswers,final RepairScreen source){
			super("(-----)",DefaultStyles.linkstyle);

			setColor(0.8f, 0.1f, 0.1f, 1.0f);
			
			super.setSize(100, 75);
			
			super.setAlignment(Align.center);

			//super.setCenterPosition(-50, -37);
			super.setOrigin(Align.center);
			
			super.addListener(new ClickListener () {
				
				@Override
				public void clicked(InputEvent ev, float x , float y){
					
					SSSNode ItemNode = STMemory.currentlyHeld.itemsnode;
					
					//if not carrying item ignore
					if (STMemory.currentlyHeld==null){
						return;
					}


					Gdx.app.log(logstag,"~item uri="+ItemNode.getPURI());
					//test
					
					if (acceptableAnswers.contains(ItemNode)){
						
						
						Gdx.app.log(logstag,"contains:"+acceptableAnswers.contains(ItemNode));
			
						STMemory.dropHeldItem(true);
						
						setModeAccepted(ItemNode);
						
						//remove from acceptable answers
						acceptableAnswers.remove(ItemNode);

						source.testAllRequestedObjectS();
						
					} else {
						
						source.rejectedAnsAnimation();		

						STMemory.dropHeldItem(true);
					}
				}
							
			});
			
		}
		
	
		public void setModeFaded() {

			setColor(0.1f, 0.8f, 0.1f, 0.2f);
		}


		protected void setModeAccepted(SSSNode itemNode) {
			
			this.setText(itemNode.getPLabel());
						
			setColor(0.1f, 0.8f, 0.1f, 1.0f);
									
			isAnwsered = true;

		}

		public boolean isAnwsered(){
			return isAnwsered;
		}
		
	}

}




