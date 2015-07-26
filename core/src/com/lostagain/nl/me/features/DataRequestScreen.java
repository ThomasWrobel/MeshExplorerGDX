package com.lostagain.nl.me.features;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.gui.DataObjectSlot;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.gui.DataObjectSlot.OnDropRunnable;
import com.lostagain.nl.me.locationFeatures.RepairScreen;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.objects.DataObject;

/**
 * A single screen asking for one or more DataObjects in order to unlock it.
 * Unlocking it might trigger data under it, or it might reveal another link in a chain
 * 
 * Changes compared to the old system;
 * 
 * Unlike the old "repair screen" system we dont allow multiple different requests.
 * To handle that now you simple have a series of request screens, each handleing one type of concept to be inserted
 * (it can, however, ask for a few of the same type)
 * 
 * We also dont cache answers in advance anymore, as its always possible the database could have changed
 * 
 * @author Tom
 *
 */
public class DataRequestScreen extends VerticalPanel implements GenericMeshFeature {
	final static String logstag = "ME.DataRequestScreen";
	
	MeshIcon itemToConnectToIfUnlocked;
	Runnable runThisWhenUnlocked;
	Runnable runThisWhenMistakeMade;
	
	//data

	/** if the security is making the user pass a query, this string is the query **/
	private String  QueryPass;
	
	/** number of objects needed to pass **/
	int numObjectsRequired;
	
	/** if the security has a description for the question, this string is that description **/
	private String SecurityDiscription="";


	
	
	//visuals
	Label title       = new Label("Progress Prevented");
	Label explanation;
	
	/**
	 * An array of what is basicaly ConceptObjectSlots designed to accept the answers for this request screen
	 */
	ArrayList<ConceptRequester> allObjectRequesters = new ArrayList<ConceptRequester>();
	
	/**
	 * the parent location the "owns" this (not nesscerily directly linked to)
	 */
	private DataRequestManager parent;
	
	HorizontalPanel SlotBar = new HorizontalPanel();
	
	/**
	 * Either parameter can be null, but not both else this whole object is pointless ;)
	 * 
	 * @param itemToConnectToIfUnlocked - we show this object and draw a line to it when the correct data is entered
	 * @param runThisWhenUnlocked - we run this once ALL the correct data is entered
	 * @param runThisWhenMistakeMade - we run this every time a wrong concept is entered (again, can be null)
	 * 
	 */
	public DataRequestScreen(DataRequestManager parent,
							 String securedByQuery,
							 int objectsRequired,
							 String description,
						     MeshIcon itemToConnectToIfUnlocked,
							 Runnable runThisWhenUnlocked,	
							 Runnable runThisWhenMistakeMade) {
		
		super();
		
		this.parent=parent;
		this.QueryPass = securedByQuery;
		this.numObjectsRequired = objectsRequired;
		SecurityDiscription=description;
		this.itemToConnectToIfUnlocked = itemToConnectToIfUnlocked;
		this.runThisWhenUnlocked       = runThisWhenUnlocked;
		this.runThisWhenMistakeMade    = runThisWhenMistakeMade;
		
		super.getStyle().clearBackgroundColor();
		super.setPadding(5f);
		super.setSpaceing(3f);
		

		//now setup the widgets		
		explanation = new Label(description);
		
		explanation.getStyle().clearBackgroundColor();
		explanation.setToscale(new Vector3(0.8f,0.8f,0.8f));
		
		title.getStyle().clearBackgroundColor();	
		SlotBar.getStyle().clearBackgroundColor();
		
		SlotBar.setSpaceing(15f);
		
		this.add(title);
		this.add(explanation);
		this.add(SlotBar);
		
		
		//tell it to refresh the request screen
		refreshSlots();
		
		
	}

	private void refreshSlots() {
		
		for (int i = 0; i < numObjectsRequired; i++) {
			
			//Label testLabel = new Label("|slot "+i);
			//testLabel.getStyle().clearBackgroundColor();
			
			ConceptRequester newslot = new ConceptRequester(QueryPass,this);
			allObjectRequesters.add(newslot);
			
			SlotBar.add(newslot);
			
		}
		
		//temp		
		
	}
	
	protected void testAllRequestedObjectS() {
		boolean passedAllTests = true;
		
		for (ConceptRequester requester : allObjectRequesters) {
			if (!requester.isAnwsered()){
				passedAllTests=false; 
			}
		}
		
		//passedAllTests

		Gdx.app.log(logstag,"~~~~~~~~~~~~~~~~~~~passedAllTests:"+passedAllTests);
		if (passedAllTests){
			setAsUnlocked();
			
		}
		

	}

	private void setAsUnlocked() {

		Gdx.app.log(logstag,"_________title1:"+title.isVisible());
		Gdx.app.log(logstag,"_________hubstate1:"+parent.parentsLocation.currentState);
		title.setText("Concepts Accepted.Progress Open"); //setting this erases all text...why?

		Gdx.app.log(logstag,"_________title2:"+title.isVisible());
		
		if (itemToConnectToIfUnlocked!=null){
			itemToConnectToIfUnlocked.show();
			
			//we can only link if we have a parent MeshIcon too
			//either the hub we are in, or our own container if directly on the landscape
			if (parentIcon!=null){
				parentIcon.addLineTo(itemToConnectToIfUnlocked);
				
			}
		}
		
		if (runThisWhenUnlocked!=null){
			runThisWhenUnlocked.run();
		}
		
	}
	
	@Override
	public AnimatableModelInstance getAnimatableModelInstance() {
		return this;
	}
	
	@Override
	public void updateApperance(float alpha, FeatureState currentState) {
		setOpacity(alpha);
		
	}
	
	
	
	
	/**
	 * A box that asks for an object in order to unlock.
	 * Once all these are unlocked, so is the location
	 * By default, there is only one lock per location. But
	 * there can be upto 6 
	 * **/
	static class ConceptRequester extends ConceptObjectSlot {

		Boolean isAnwsered = false;
		DataRequestScreen sourcescreen;
		//ArrayList<SSSNode> acceptableAnswers;
		//String protectionString = "";
		Query AnswersQuery;
		
		enum RequestorState {
			empty,thinking,rejected,accepted;
			
		}
		RequestorState current = RequestorState.empty;
		
		/**
		 * @param protectionString - the query determining the acceptable answers
		 * @param source
		 */
		public ConceptRequester(String protectionString,final DataRequestScreen source){

			//this.acceptableAnswers=acceptableAnswers;
			//setColor(0.8f, 0.1f, 0.1f, 1.0f);			
			//	super.setSize(100, 75);			
			//super.setOrigin(Align.center);
			//super.debug();
			sourcescreen = source;
			//this.protectionString = protectionString;

			AnswersQuery = new Query(protectionString);
			
			super.setOnDropRun(new OnDropRunnable() {

				@Override
				public void run(ConceptObject drop) {

					SSSNode ItemNode = drop.itemsnode;


					Gdx.app.log(logstag,"~~~~~~~~~~~~~~~~~~~item dropped uri="+ItemNode.getPURI());


					//					


				}

			});



		}

		public void setModeFaded() {
			super.getStyle().setBackgroundColor(new Color(0.1f, 0.8f, 0.1f, 0.2f));
		}


		protected void setModeAccepted(SSSNode itemNode) {

			super.getStyle().setBackgroundColor(new Color(0.1f, 0.8f, 0.1f, 1.0f));	
			super.getStyle().setBorderColor(Color.GREEN);
			
			isAnwsered = true;

		}
		private void debatingIfToAcceptStyle() {

			current = RequestorState.thinking;
			Gdx.app.log(logstag,"debating If To Accept");
			super.getStyle().setBorderColor(Color.ORANGE);
		}

		protected void rejectConcept(ConceptObject object) {
			Gdx.app.log(logstag,"rejecting concept:");
			super.getStyle().setBorderColor(Color.RED);
			
			animatedRejection(); 
			
			if (sourcescreen.runThisWhenMistakeMade!=null){
				sourcescreen.runThisWhenMistakeMade.run();
			}
			
			
			//ensure concept is first added (silly but this is so later we can have a nice rejection animation
			//it needs to know what to eject!
			STMemory.clearCurrentlyHeld();
			 MainExplorationView.setCursor(null);
			objectCurrentlyStored = object;
						
			
			ejectConcept();
			
		}

	

		public boolean isAnwsered(){
			
			return isAnwsered;
		}

		@Override
		public boolean willAccept(final ConceptObject object){

			final SSSNode ItemNode = object.itemsnode;		
			Gdx.app.log(logstag,"~testing uri="+ItemNode.getPURI());
			Gdx.app.log(logstag,"protection Query="+AnswersQuery.getAsString());
	
			DoSomethingWithNodesRunnable RunWhenDone = new DoSomethingWithNodesRunnable(){

				@Override
				public void run(ArrayList<SSSNode> newnodes, boolean invert) {
					
					Gdx.app.log(logstag,"acceptableAnswers:"+newnodes.toString());
					Gdx.app.log(logstag,"testing:"+ItemNode.getPURI());
					
					ArrayList<SSSNode> acceptableAnswers = newnodes;
					
					//test					
					if (acceptableAnswers.contains(ItemNode)){
						
						Gdx.app.log(logstag,"concept accepted");
					
						setModeAccepted(ItemNode);
						
						Gdx.app.log(logstag,"setting as locked");
						ConceptRequester.this.setCurrentMode(SlotMode.Locked);
												
						current = RequestorState.accepted;
						
						//test if there's any more answers needed
						sourcescreen.testAllRequestedObjectS();
						
					} else {
						
						Gdx.app.log(logstag,"concept not accepted");
												
						rejectConcept(object); 

						current = RequestorState.rejected;
						
					}
					
					
				}

			};
			
			//Note; It might be accepted temporily, only to be chucked out when the result comes in.
			//This is why we have a "decideing" style as well as accepted/rejected			
			debatingIfToAcceptStyle();
			QueryEngine.processQuery(AnswersQuery, false, null, RunWhenDone);
		
			
		
			if (current != RequestorState.rejected){

				Gdx.app.log(logstag,"still thinking or accepted");
				return true; //accepted or still thinking so assume true for now
			} else {

				Gdx.app.log(logstag,"rejected");
				return false; //we know its been rejected already so we return false
			}
			
		}

	

	}




	public void setRunThisWhenUnlocked(Runnable runThisWhenUnlocked) {
		this.runThisWhenUnlocked = runThisWhenUnlocked;
	}

	public void setRunThisWhenMistakeMade(Runnable runThisWhenMistakeMade) {
		this.runThisWhenMistakeMade = runThisWhenMistakeMade;
	}


	MeshIcon parentIcon = null;
	@Override
	public void setParentMeshIcon(MeshIcon icon) {
		parentIcon = icon;
		return;
	}

	@Override
	public MeshIcon getParentMeshIcon() {
		return parentIcon;
	}

	
	//temp experiment
	/*
	@Override
	protected void fireAllSizeChangeHandlers() {
		
		//directly trigger
		if (parentIcon!=null){
			
			Gdx.app.log(logstag,"_______________refreshAssociatedFeature______________"+this.getCenterOfBoundingBox());
			parentIcon.refreshAssociatedFeature();
			
		}
		
		//dont fire
		//super.fireAllSizeChangeHandlers();
		
		
	}*/
	
}
