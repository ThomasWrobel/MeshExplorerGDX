package com.lostagain.nl.me.features;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.ClickHandler;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.ToggleButton;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.objectType;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.shaders.MySorter.ZIndexAttribute;

/**
 * Experimental inventory class
 * will eventually replace the 2d on I hope
 * 
 * Essentially a list of slots that represents the players ability to store stuff and bring it with them
 * 
 * This really is a new inventory object, along with a new concept gun, should we put these in me.gui
 * 
 * TODO: dragabout function by dragging header?
 * 
 * @author Tom
 *
 */
public class InventoryPanel extends VerticalPanel  implements GenericMeshFeature,Animating  {

	final static String logstag = "ME.InventoryPanel";
	
	//node
	//eventually the inventory panel will be represented by a semantic node
	//This node will eventually store its capacity, as well as what its currently containing
	//SSSNode inventorysNode;
	//
	//playersInventory subclass ability
	//playersInventory capacity 7
	//playersInventory contains Node1
	//playersInventory contains Node2
	//playersInventory contains Node3
	//playersInventory contains Node4
	//etc
	//
	//We will then need to both update this state when changes are made, and one day provide a save to semantic
	//file option?
	
	
	//status
	SSSNode inventorysNode; //contains all the data that describes this ability.
	int NumberOfSlots = 7; //number of inventory slots
	boolean pinned = false; //are we pinned to the interface 		
		
	//boolean isCollapsed = false;
	
	enum DisclosureState {
		Collapsed,Collapsing,Expanding,Expanded
	}
	
	DisclosureState collapsedState = DisclosureState.Expanded;
	
	//widgets
	ArrayList<ConceptObjectSlot> inventorySlots = new ArrayList<ConceptObjectSlot>();
	Label Title = new Label("Inventory /\\");	
	ToggleButton pinButton;
	
	
	public InventoryPanel(){
		
		//Appearance
		getStyle().setBackgroundColor(new Color(0.1f,0.1f,0.1f,0.8f));
		setPadding(5f);
				
		//title		
		Title.getStyle().clearBackgroundColor();
		Title.getStyle().setColor(new Color(0.2f,0.9f,0.2f,1.0f));
		Title.setToScale(new Vector3(0.8f,0.8f,0.8f));
		
		
		//Pin button removed for the movement. Might reconsider it later
		//but its probably pointless for this panel type
		/*
		Label PinUp   = new Label("(  Pin  )");
		Label PinDown = new Label("( Float )");
		
		  PinUp.getStyle().clearBackgroundColor();
		PinDown.getStyle().clearBackgroundColor();
		
		  PinUp.setToScale(new Vector3(0.6f,0.6f,0.6f));
		PinDown.setToScale(new Vector3(0.6f,0.6f,0.6f));

		
		Runnable onChange = new Runnable(){

			@Override
			public void run() {

				Gdx.app.log(logstag,"---pin clicked---");
				if (pinButton.getValue()){
					setPinnedToCamera(true);					
				} else{
					setPinnedToCamera(false);	
				}
			}
			
		};*/
		

		//pinButton = new ToggleButton(PinUp,PinDown,onChange);
		//pinButton.getStyle().clearBackgroundColor();
		
		//HorizontalPanel topBar = new HorizontalPanel();
		//topBar.setPadding(2f);
		
		//topBar.getStyle().clearBackgroundColor();
		
		//topBar.add(Title);
		//topBar.add(pinButton);
		this.add(Title);
		
		//add click detection on title
		Title.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				Gdx.app.log(logstag,"---header clicked---");
				
				if (collapsedState == DisclosureState.Collapsed){
					expand();
					Title.setText("Inventory /\\");
					
				}
				if (collapsedState == DisclosureState.Expanded){
					collapse();
					Title.setText("Inventory \\/");
				}
					
				
			}
		});
		
		//add the slots
		for (int i = 0; i < NumberOfSlots; i++) {
			
			ConceptObjectSlot newSlot = new ConceptObjectSlot();
			this.add(newSlot);
			inventorySlots.add(newSlot);
			
		}
		
		//shrinktest		
		setToScale(new Vector3(0.65f,0.65f,0.65f));
		
	//	setToDefaultPosition();
		
	}
	
	//default position depends on screen res, as displacement is relative to camera center
	//	private void setToDefaultPosition() {
			
		
		//	this.setToScale(new Vector3(0.4f,0.4f,0.4f));
			
		//	MainExplorationView.camera.attachThisRelativeToScreen(this,0,59,222f); //0,0 is top left
			
			
		//	this.pinned=true;
	//	}

	protected void setPinnedToCamera(boolean b) {
		if (b){
			Gdx.app.log(logstag,"--pinning to camera---");
			//pin it
			
			//we need to get the existing displacement from cam  first
			PosRotScale existingTransform = this.getTransform();
			Vector3 position = existingTransform.position.cpy();
			Vector3 camera   = MainExplorationView.camera.getCamPosRotScale().position.cpy();
			
			Vector3 displacement = position.sub(camera); //set relative to cam
			//make closer			
			displacement.z = displacement.z + 150f;						
						
			PosRotScale dis  = new PosRotScale(displacement);
			dis.scale = existingTransform.scale.cpy();
			
			Gdx.app.log(logstag,"attaching at:"+dis.toString());
			
			//then attach it with that displacement
			MainExplorationView.camera.attachThis(this, dis);
			
			//
			
		} else {
			//unpin it
			Gdx.app.log(logstag,"--removing from camera---");
			MainExplorationView.camera.deattachThis(this);
						
			this.getTransform().position.z = this.getTransform().position.z - 150f;
			this.sycnTransform();
		}
		
	}

	@Override
	public AnimatableModelInstance getAnimatableModelInstance() {
		return this;
	}

	@Override
	public void updateApperance(float alpha, FeatureState currentState) {
		//currently just a fade in/out
		//in future we might offset fade by element to fade out from last slot first
		this.setOpacity(alpha);
		
		
		
		
	}
	MeshIcon parentMesh = null;
	@Override
	public void setParentMeshIcon(MeshIcon ICON) {
		parentMesh=ICON;
	}

	@Override
	public MeshIcon getParentMeshIcon() {
		return parentMesh;
	}
	
	//hide everything but the header (we should animate in future)
	private void collapse(){
		durationIntoCollapseAnimation = 0f;
		collapsedState = DisclosureState.Collapsing;
		ModelManagment.addAnimating(this);
		/*
		for (ConceptObjectSlot slot : inventorySlots) {
			remove(slot);	
			
		}
		
		isCollapsed = true;*/
	}
	
	//show everything
	private void expand(){
		durationIntoCollapseAnimation = 0f; //instantly appearing works, but when we reset the time to do it gradully it goes wrong scale-wise...hmm
		collapsedState = DisclosureState.Expanding;
		ModelManagment.addAnimating(this);
		
	//assumes none are yet added
		/*
		for (ConceptObjectSlot slot : inventorySlots) {
			
			slot.setToscale(new Vector3(1f,1f,1f));
			slot.show();
			
			add(slot); 
			
		}
		
		isCollapsed = false;*/
	}

	float collapseDuration = 0.5f;
	float durationIntoCollapseAnimation = 0f;
	
	@Override
	public void updateAnimationFrame(float deltaTime) {
		
		int total = inventorySlots.size();
		float timePerSlot = collapseDuration/total;
		durationIntoCollapseAnimation=durationIntoCollapseAnimation+deltaTime;
		
		for (int i = 0; i < total; i++) {
			
			ConceptObjectSlot slotToChange = null;
			
			//different depending on direction
			if (collapsedState==DisclosureState.Expanding){		

				 slotToChange = inventorySlots.get((total-1)-i);
				
			} else if (collapsedState==DisclosureState.Collapsing){
				

				 slotToChange = inventorySlots.get(i);
			}

			
			
			
			float targetTime = i*timePerSlot;
			
			//note; currently it keeps making them visible even if they are already
			//we really need to only add the latest,not try to  re-add them all
			if (durationIntoCollapseAnimation>targetTime){
				
				if (collapsedState==DisclosureState.Expanding){
					//ensure its not there already else we waste time and wreck scaling
					if (!this.hasAttachment(slotToChange))
					{
						slotToChange.setToScale(new Vector3(1f,1f,1f));
						slotToChange.show();					
						//add(slotToChange); 
						
						insert(slotToChange,2);
						
					}
					
				} else if (collapsedState==DisclosureState.Collapsing){
						remove(slotToChange);	
				}
				
			}
			
					
		}
		
		if (durationIntoCollapseAnimation>collapseDuration){
			
			if (collapsedState==DisclosureState.Expanding){
				collapsedState=DisclosureState.Expanded;				
			} else if (collapsedState==DisclosureState.Collapsing){
				collapsedState=DisclosureState.Collapsed;	
			}
			ModelManagment.removeAnimating(this);
		}
		
		
		
	}

	
	public void updateParameters(SSSNode ability) {
		inventorysNode=ability;
		
		//all property for ability
		HashSet<SSSNodesWithCommonProperty> populationPropertys = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(ability.PURI);
		for (SSSNodesWithCommonProperty property : populationPropertys) {
			
			if (property.getCommonPrec() == StaticSSSNodes.Capacity){
				NumberOfSlots = Integer.parseInt(property.getCommonValue().getPLabel()); //rather crude using label like this really
				Gdx.app.log(logstag,"-capacity now:"+NumberOfSlots);
			}
						
		}
		Gdx.app.log(logstag,"-capacity now:"+NumberOfSlots);
		//get capacity
		
		// get stats from node
		//NumberOfSlots = 5; //temp for testing
	
		
		updateSlotCapacity();
		
		//update color too (I mean, why not? lets have custom colors if the data is on the node)
		ArrayList<Color> col = DefaultStyles.getColorsFromNode(ability);
		
		if (col!=null && !col.isEmpty()){
			//Colors found, so we should set our colour;
			this.getStyle().setBackgroundColor(col.get(0));//just use first for now
			
			
		}
		
		
	}

	private void updateSlotCapacity() {
		if (NumberOfSlots>inventorySlots.size()){
			int difference = NumberOfSlots-inventorySlots.size();
			//add extra slots
			for (int i = 0; i < difference; i++) {
				
				ConceptObjectSlot newSlot = new ConceptObjectSlot();
				inventorySlots.add(newSlot);
				if ( collapsedState == DisclosureState.Expanded){
					this.add(newSlot); //only add for real is not collapsed
				}
				
				
			}
		}
		
		
		if (NumberOfSlots<inventorySlots.size()){			
			//remove slots (ejecting contents as we go)
			int difference = inventorySlots.size()-NumberOfSlots;
			//add extra slots
			for (int i = 0; i < difference; i++) {		
				ConceptObjectSlot slot = inventorySlots.get(inventorySlots.size()-1);
				inventorySlots.remove(slot);	
				slot.ejectConcept();
				this.remove(slot);
			}						
		}

		Gdx.app.log(logstag,"--total slots now:"+inventorySlots.size());
	}

	/**
	 * does this object block whats behind it?
	 * @return
	 */
	@Override
	public objectType getInteractionType() {
		return objectType.Blocker;
	}

	
	@Override
	public Vector3 getDefaultCameraPosition() {
		//gets the center of this email on the stage
		Vector3 center = getCenterOnStage();
		center.z = ScreenUtils.getSuitableDefaultCameraHeight();
		
		return center;
	}

	@Override
	public void setZIndex(int index, String group) {
		//set zindex of back material
		super.getStyle().addAttributeToShader(new ZIndexAttribute(index,group));
	
		//but we also need to apply it to all subobjects (only a little higher!)
		for (Widget childwidget : super.getChildren()) {
			childwidget.getStyle().addAttributeToShader(new ZIndexAttribute(index+1,group)); //NOTE; wont work for sub-sub objects as zindex isnt part of gwtish
		}
		
		
	}
}
