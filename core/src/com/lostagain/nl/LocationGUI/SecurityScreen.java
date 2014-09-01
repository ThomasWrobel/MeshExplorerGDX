package com.lostagain.nl.LocationGUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;








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
import com.lostagain.nl.ME;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.LocationGUI.ObjectFile.ObjectFileState;

public class SecurityScreen extends Group  implements LocationScreen {

	static Logger Log = Logger.getLogger("ME.SecurityScreen");
	  
	LocationContainer locationProtectedByThis;
	
	private SSSNode securedBy;

	/** if the security is making the user pass a query, this string is the query **/
	private String QueryPass;
	
	/** number of items we need that meet that requirements **/
	private int NumberOfObjectNeeded = 1;
	
	
	/** if the security has a description for the question, this string is that description **/
	private String SecurityDiscription="";
	
	
	/** Acceptable results for query pass **/
	public ArrayList<SSSNode> acceptableAnswers = new ArrayList<SSSNode>();

	private boolean readyForAnswer = false; //is true when everything is loaded and is ready to accept an answer
		
	Label LockedText = new Label ("COMPUTER LOCKED : ", DefaultStyles.linkstyle);
	Label RequirementsText = new Label ("Requirements : ", DefaultStyles.linkstyle);
	Label UnlockedLabel= new Label ("(COMPUTER UNLOCKED)", DefaultStyles.linkstyle);

	
	ArrayList<ObjectRequester> allObjectsRequested = new ArrayList<ObjectRequester>();
	
	
	public SecurityScreen(LocationContainer parentLocationContainer, SSSNode securedBy) {
		super();
		
	//	Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		locationProtectedByThis = parentLocationContainer;
		this.securedBy = securedBy;
		
		
		//securedBy = securedByThis;
		if (securedBy!=null){
			
			setAsLocked();
			
		} else {
			setAsUnlocked();
		}
		
		
	}

	
	@Override
	public void validate() {
		
		float HEIGHT = super.getParent().getHeight();
		float WIDTH = super.getParent().getWidth();
		
		super.setSize(WIDTH, HEIGHT);
		
		LockedText.setPosition(10, super.getHeight()-30);
		RequirementsText.setPosition(10, super.getHeight()-60);	
		
		UnlockedLabel.setCenterPosition((getWidth()/2),(getHeight()/2));
		
		//UnlockedLabel.setPosition((getWidth()/2),(getHeight()/2)-30);

		int i=0;
		 for (ObjectRequester req : allObjectsRequested) {
			
			// req.setPosition(getCenterX()-75, getCenterY()-50);

				req.setPosition(20+(i*(req.getWidth()+10)),getCenterY());
				 i++;
		}
		 
	}
	

	private void setAsUnlocked() {

		locationProtectedByThis.unlockComputer();
		
		//add to players data as unlocked
		if (!(locationProtectedByThis.LocationsNode ==null)){
			PlayersData.addUnlockedLink(locationProtectedByThis.LocationsNode);
		}
		
		//remove existing text if present
		if (LockedText!=null){
			super.removeActor(LockedText);
			super.removeActor(RequirementsText);
		}
		
		//add unlocked message and details
		UnlockedLabel = addText("(COMPUTER UNLOCKED)",10,super.getHeight()-30);
		
		
		
		
		
	}



	private Label addText(String string, float x, float f) {
		
		
		Label textlabel = new Label(string,DefaultStyles.linkstyle);
		textlabel.setPosition(x, f);
		super.addActor(textlabel);
		
		
		return textlabel;
		
		
	}


	private void addAnswerDropTarget() {
		
		//adds the area to click on to supply the answer
		//we might in future drag to this location if
		//that makes a more interesting interface
		int i=0;
		
		while(i<NumberOfObjectNeeded){
			
		
		ObjectRequester newObjRequester = new ObjectRequester(acceptableAnswers,this);
		
		allObjectsRequested.add(newObjRequester);
		
		newObjRequester.setPosition(20+(i*newObjRequester.getWidth()),getCenterY());
		
		super.addActor(newObjRequester);

		i++;
		}
		
		
	}
	



	
	private void setAsLocked() {
		
		//me:queryPass
		//add interface elements (non-dragable)
		 LockedText = addText("COMPUTER LOCKED : ",10,super.getHeight()-10);
		// RequirementsText = addText("Requirements Not Yet Met:", 10, super.getHeight()-	40);
		 RequirementsText.setText("Requirements Not Yet Met:");
		 
		 
		// Log.info("__________________getting protection string_________________________");
		 
		//get protection string
		HashSet<SSSNodesWithCommonProperty> securitysPropertys = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(securedBy.PURI);
		
		 //Log.info("__________________tings secured by: "+securedBy.PURI+" = "+securitysPropertys.size()+" _________________________");
		 
		for (SSSNodesWithCommonProperty propertset : securitysPropertys) {
			
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
		}
		
		if (SecurityDiscription==""){
			RequirementsText = addText(protectionString, 10,super.getHeight()-90);
		} else {
			RequirementsText = addText(SecurityDiscription, 10,super.getHeight()-90); ;
			
		}
		
		retrieveAnswersAsycn(protectionString);
		
		
		addAnswerDropTarget(); 
		
	}

	

	private void retrieveAnswersAsycn(String protectionString) {
		
		Log.info("protectionString="+protectionString);
		
		//some debug tests
		
		//Note; Green does not exist at this point!
		
		//C:\TomsProjects\MeshExplorerV2\desktop\semantics\TomsNetwork.ntlist#green

	//	SSSNode greennode2  = SSSNode.getNodeByUri("C:\\TomsProjects\\MeshExplorerV2\\desktop\\semantics\\TomsNetwork.ntlist#green");
		Log.info("_______total_______"+SSSNode.getAllKnownNodes().toString());
    	
		SSSNode greennode  = SSSNode.getNodeByLabel("green");
    	Log.info("_______g_______"+greennode.getEquivilentsAsString());
    	Log.info("_______g_______"+greennode.getPURI());
    	
    	SSSNode ColorNode = SSSNode.getNodeByLabel("color");    
    	Log.info("______f_______|_"+ColorNode.getEquivilentsAsString());
    	Log.info("______f_______|_"+ColorNode.getPURI());
		
		Query answers = new Query(protectionString);
		
		DoSomethingWithNodesRunnable RunWhenDone = new DoSomethingWithNodesRunnable(){

			@Override
			public void run(ArrayList<SSSNode> newnodes, boolean invert) {
				
				acceptableAnswers.clear();
				acceptableAnswers.addAll(newnodes);
				
				Log.info("answers="+acceptableAnswers.toString());
				
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
		return securedBy;
	}



	public void setSecuredBy(SSSNode securedBy) {
		this.securedBy = securedBy;
		
	}
	
	public void testAllRequestedObjectS(){
		
		Boolean locked = false;
		
		for (ObjectRequester objreq : allObjectsRequested) {
			
			if (!objreq.isAnwsered()){
				locked=true;
			}
			
		}
		
		if (!locked){
			
			Log.info("unlocking");
			
			setAsUnlocked();
			
		}
		
	}
	
	/** a box that asks for an object in order to unlock.
	 * Once all these are unlocked, so is the location
	 * By default, there is only one lock per location. But
	 * there can be upto 6 **/
	static class ObjectRequester extends Label {
		
		
		Boolean isAnwsered = false;
		
		public ObjectRequester(final ArrayList<SSSNode> acceptableAnswers,final SecurityScreen source){
			super("(-----)",DefaultStyles.linkstyle);

			setColor(0.8f, 0.1f, 0.1f, 1.0f);
			
			super.setSize(100, 75);
			
			super.setAlignment(Align.center);

			super.setCenterPosition(-50, -37);
			
			super.addListener(new ClickListener () {
				
				@Override
				public void clicked(InputEvent ev, float x , float y){
					
					SSSNode ItemNode = ME.playersInventory.currentlyHeld;
					
					//if not carrying item ignore
					if (Inventory.currentlyHeld==null){
						return;
					}


					Log.info("~item uri="+ItemNode.getPURI());
					//test
					
					if (acceptableAnswers.contains(ItemNode)){
						
						
						Log.info( "contains:"+acceptableAnswers.contains(ItemNode));
			
						ME.playersInventory.dropHeldItem(true);
						
						setModeAccepted(ItemNode);
						
						//remove from acceptable answers
						acceptableAnswers.remove(ItemNode);

						source.testAllRequestedObjectS();
						
					} else {
						
						source.rejectedAnsAnimation();		

						ME.playersInventory.dropHeldItem(true);
					}
				}
							
			});
			
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




