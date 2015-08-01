package com.lostagain.nl.me.features;

import java.util.HashSet;
import java.util.Vector;

import javax.xml.datatype.Duration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.me.features.ConceptObjectSlot.OnDropRunnable;
import com.lostagain.nl.me.features.ConceptObjectSlot.SlotMode;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * Provides one slot that lets the player install features to their GUI or location
 * A progress bar is also there to indicate the installing, but it works much faster then scanners
 * 
 * @author Tom
 *
 */
public class AbilityInstaller extends Widget implements GenericMeshFeature,Animating {
	private static final String STANDARD_FEEDBACK_MESSAGE = "(please drop an ability onto the slot)";

	final static String logstag = "ME.AbilityInstaller";

	static float width  = 380f;
	static float height = 200f;
	
	//data
	LocationHub parenthub;
	
	//widgets
	Label title = new Label("Installer");
	ConceptObjectSlot slot = new ConceptObjectSlot();

	Label feedback = new Label(STANDARD_FEEDBACK_MESSAGE);
	
	
	boolean currentlyInstalling= false;
	float installDuration = 3.0f;
	float currentlyIntoInstall = 0f;
	private SSSNode queuedInstall; //ability about to install.
	
	ProgressBar installerBar = new ProgressBar(33,0,width-80,0,installDuration,0);
	
	public AbilityInstaller(LocationHub locationHub) {
		
		super(width, height,MODELALIGNMENT.TOPLEFT);
		super.getStyle().clearBackgroundColor();
		super.getStyle().clearBorderColor();
		this.parenthub=locationHub;
		
		//setup and style widgets
		slot.setCurrentMode(SlotMode.InOnly); 
		title.getStyle().clearBackgroundColor();
		title.getStyle().clearBorderColor();	
		//add handlers
		slot.setOnDropRun(new OnDropRunnable() {
			@Override
			public void run(ConceptObject drop) {
				installRequested(drop.itemsnode);
			}
		});
		
		//position widgets
		float hw = width/2;
		float hh = height/2;
		
		PosRotScale titlePosition = new PosRotScale(hw - (title.getScaledWidth()/2),-15f,3f);
		attachThis(title, titlePosition);
		
		PosRotScale slotPosition = new PosRotScale(hw - (slot.getScaledWidth()/2),-55f,3f);
		attachThis(slot, slotPosition);
		
		PosRotScale installerBarPosition = new PosRotScale(40f,-105f,3f);
		attachThis(installerBar, installerBarPosition);
		
		
		
		feedback.setToScale(new Vector3(0.6f,0.6f,0.6f));
		feedback.getStyle().clearBackgroundColor();
		
		//use vertical panel to auto-centralise
		VerticalPanel feedbackBar  = new VerticalPanel();
		feedbackBar.getStyle().clearBackgroundColor();
		feedbackBar.getStyle().clearBorderColor();	
		feedbackBar.setMinSize(width,40);		
		feedbackBar.add(feedback);
		
		attachThis(feedbackBar, new PosRotScale(0f,-160f,3f));
		
		
	}

	private void installRequested(SSSNode ability){
		Gdx.app.log(logstag," installRequested:"+ability);
		
		//check if its an ability if not reject it
		boolean isAbility = ability.isOrHasParentClass(StaticSSSNodes.ability.PURI);		
		
		if (!isAbility){
			slot.ejectConcept(); //ejects it 
			feedback.getStyle().setColor(Color.RED);
			feedback.setText("Not an Ability!");
			//should reset after a period
			resetFeedbackAfterPause(3f);
			return;
		}
		
		//check we havnt got this one installed already
		boolean hasAbility = PlayersData.playerslocationcontents.containsNode(ability);
		
		if (hasAbility){
			
			feedback.setText("(ability already installed)");
			feedback.getStyle().setColor(Color.RED);
			
			resetFeedbackAfterPause(3f);
			slot.ejectConcept(); 
			return;
		}
		
		//detect what ability is being installed
		feedback.setText("(ability accepted, please wait)");
		
		//process install (we should first wait for the progress bar)
		//installerBar.setValue(10);
		
		//set bar animation going (installs at end of it)
		currentlyInstalling = true;
		ModelManagment.addAnimating(this);
		queuedInstall = ability;
		 currentlyIntoInstall = 0f;
		 
		
		
		
	}

	private void processInstall(SSSNode ability) {
		
		HashSet<SSSNode> types =	ability.getAllClassesThisBelongsToo();
		//should only be one type for now
		//but in future we could allow multi-types?
		
		if (types.contains(StaticSSSNodes.STMemoryAbility)){
			//its a type of inventory
			installInventory(ability);
		}
		if (types.contains(StaticSSSNodes.conceptgun)){
			//its a type of concept gun
			installConceptGun(ability);
		}
		if (types.contains(StaticSSSNodes.decoder)){
			//its a type of language decoder
			installDecoder(ability);
		}
		
		
		//add to the players information
		//(in future we use PlayersData as a save system)
		
		PlayersData.addItemToDatabase(ability, "local");
		
		//remove concept from slot now its been used to install
		slot.ejectConcept(); //might not want to fire any negative looking "rejection" style eject
		
		//tell hub to update
		parenthub.reGenerateLocationContents();
	}
	
	
	private void installDecoder(SSSNode ability) {		
		//do nothing! merely this node being stored at the players location is enough
	}
	

	private void installConceptGun(SSSNode ability) {

		//temp only one gun type right now
		MainExplorationView.usersGUI.setmyCGunVisible(true);
		feedback.setText("( "+ability.getPLabel()+" installed )");
		feedback.getStyle().setColor(Color.GREEN);
		resetFeedbackAfterPause(4f);
		
		MainExplorationView.infoPopUp.displayMessage("ConceptGun Installed", Color.GREEN);
		
	}
	
	private void installInventory(SSSNode ability) {
		
		//if the inventory does not already exists;
		if (PlayersData.playersInventoryPanel==null){
		
			InventoryPanel testInventory = new InventoryPanel();
			testInventory.setToPosition(new Vector3(350f,1185f,0f));
			ModelManagment.addmodel(testInventory,ModelManagment.RenderOrder.zdecides);
		
			PlayersData.playersInventoryPanel = testInventory;
			
			testInventory.updateParameters(ability);
			
			feedback.setText("( "+ability.getPLabel()+" installed )");
			feedback.getStyle().setColor(Color.GREEN);
			resetFeedbackAfterPause(4f);
			
			MainExplorationView.infoPopUp.displayMessage("Inventory Installed", Color.GREEN);
			
		} else {
			//remove the old parameters from the players data
			//PlayersData.playerslocationcontents.removeNodeFromThisSet(PlayersData.playersInventoryPanel.inventorysNode);	

			PlayersData.removeItemFromDatabase(PlayersData.playersInventoryPanel.inventorysNode);	
			
			//(the new one gets added in the processInstall function that calls this)
			
		    //else we update the existing panel to this new set of abilitys for it
			PlayersData.playersInventoryPanel.updateParameters(ability);			
			

			feedback.setText("( "+ability.getPLabel()+" installed )");
			feedback.getStyle().setColor(Color.GREEN);
			resetFeedbackAfterPause(4f);
		//set a message
			MainExplorationView.infoPopUp.displayMessage("New Inventory Installed", Color.GREEN);
			
			
		}
		
		
	}

	private void resetFeedbackAfterPause(float pauseLength) {
		
		Timer.schedule(new Task() {				
			@Override
			public void run() {
				feedback.getStyle().setColor(Color.WHITE);
				feedback.setText(STANDARD_FEEDBACK_MESSAGE);
				
			}
		}, pauseLength);
		
		
		
		
	}
	
	
	@Override
	public AnimatableModelInstance getAnimatableModelInstance() {
		return this;
	}

	@Override
	public void updateApperance(float alpha, FeatureState currentState) {
		//just opacity for now
		this.setOpacity(alpha);
		title.setOpacity(alpha);
		slot.setOpacity(alpha);
		installerBar.setOpacity(alpha);
		feedback.setOpacity(alpha);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	MeshIcon parent;
	@Override
	public void setParentMeshIcon(MeshIcon ICON) {
		parent = ICON;
	}

	@Override
	public MeshIcon getParentMeshIcon() {
		return parent;
	}

	@Override
	public void updateAnimationFrame(float deltatime) {
		if (currentlyInstalling){
			
			currentlyIntoInstall=currentlyIntoInstall+deltatime;
			
			//percent into install
			//float percentage = (currentlyIntoInstall/installDuration) * 100.0f; 
			
			installerBar.setValue(currentlyIntoInstall);
			
			
			if (currentlyIntoInstall>installDuration){				
				processInstall(queuedInstall);
				currentlyInstalling = false;
				currentlyIntoInstall=0f;
				installerBar.setValue(0);
				ModelManagment.removeAnimating(this);
				queuedInstall = null;
			}
			
		}
	}

}