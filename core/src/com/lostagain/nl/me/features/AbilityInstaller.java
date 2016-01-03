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
import com.lostagain.nl.GWTish.ComplexPanel;
import com.lostagain.nl.GWTish.Management.ZIndexAttribute;
import com.lostagain.nl.me.features.ConceptObjectSlot.OnDropRunnable;
import com.lostagain.nl.me.features.ConceptObjectSlot.SlotMode;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.GWTishModelManagement;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * Provides one slot that lets the player install features to their GUI or location
 * A progress bar is also there to indicate the installing, but it works much faster then scanners
 * 
 * @author Tom
 *
 */
public class AbilityInstaller extends VerticalPanel implements GenericMeshFeature,Animating {
	private static final String STANDARD_FEEDBACK_MESSAGE = "(please drop an ability onto the slot)";

	public final static String logstag = "ME.AbilityInstaller";

	static int width  = 380;
	static int height = 250;
	
	//data
	LocationHub parenthub;
	
	//widgets
	Label title = new Label("Installer");
	ConceptObjectSlot slot = new ConceptObjectSlot();

	Label feedback = new Label(STANDARD_FEEDBACK_MESSAGE);
	
	Label RunningSoftwareLabel = new Label("--",width-120f);
	
	boolean currentlyInstalling= false;
	float installDuration = 3.0f;
	float currentlyIntoInstall = 0f;
	private SSSNode queuedInstall; //ability about to install.
	
	ProgressBar installerBar = new ProgressBar(33,0,width-80,0,installDuration,0);
	
	public AbilityInstaller(LocationHub locationHub) {
		
	//	super(width, height,MODELALIGNMENT.TOPLEFT);
		super.getStyle().clearBackgroundColor();
		super.getStyle().clearBorderColor();
		super.setPadding(9f);
		
		//NOTE: at some point it might be nice to make this whole panel a drop target for the conceptobjects
		//but for this we will need to make sure it doesnt block specific drops on the slot as well
		
		//super.setAsHitable(true);
		/*
		super.addMouseUpHandler(new MouseUpHandler() {			
			@Override
			public void onMouseUp() {
				Gdx.app.log(logstag,"_-(fireTouchUp)-_");
				
				if (STMemory.isHoldingItem()){
					
					Gdx.app.log(logstag,"_-(mouse up while holding:"+STMemory.currentlyHeldNEW.itemsnode.getPLabel()+")-_");
					 
					installRequested(STMemory.currentlyHeldNEW.itemsnode);
				}
			}
		});*/
		
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
		
		//PosRotScale titlePosition = new PosRotScale(hw - (title.getScaledWidth()/2),-15f,3f);
		//attachThis(title, titlePosition);
		add(title);
		//PosRotScale slotPosition = new PosRotScale(hw - (slot.getScaledWidth()/2),-55f,3f);
		//attachThis(slot, slotPosition);
		add(slot);	
		//PosRotScale installerBarPosition = new PosRotScale(40f,-105f,3f);
		//attachThis(installerBar, installerBarPosition);
		add(installerBar);
		
		
		feedback.setToScale(new Vector3(0.6f,0.6f,0.6f));
		feedback.getStyle().clearBackgroundColor();
		
		//use vertical panel to auto-centralise
		VerticalPanel feedbackBar  = new VerticalPanel();
		feedbackBar.getStyle().clearBackgroundColor();
		feedbackBar.getStyle().clearBorderColor();	
		feedbackBar.setMinSize(width,40);		
		feedbackBar.add(feedback);
		
		//attachThis(feedbackBar, new PosRotScale(0f,-160f,3f));

		
		add(feedbackBar);
		RunningSoftwareLabel.setMaxWidth((width-30f)*(1.0f/0.6f)); //max width doesnt yet take into account scaleing
		
		RunningSoftwareLabel.getStyle().clearBackgroundColor();
		updateInstalledLabel();
		RunningSoftwareLabel.setToScale(new Vector3(0.4f,0.4f,0.4f));
		add(RunningSoftwareLabel);
		
		//attachThis(RunningSoftwareLabel, new PosRotScale(0f,-185f,3f));
		
		
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
		boolean isInstalled = PlayersData.playersLocationActiveSoftware.containsNode(ability);
		
		if (isInstalled){
			
			feedback.setText("(ability already installed)");
			feedback.getStyle().setColor(Color.RED);
			
			resetFeedbackAfterPause(3f);
			slot.ejectConcept(); 
			return;
		}
		
		
		
		//detect what ability is being installed
		
		
		
		//
		feedback.setText("(ability accepted, please wait)");
		
		//process install (we should first wait for the progress bar)
		//installerBar.setValue(10);
		
		//set bar animation going (installs at end of it)
		currentlyInstalling = true;
		GWTishModelManagement.addAnimating(this);
		queuedInstall = ability;
		 currentlyIntoInstall = 0f;
		 
		
		
		
	}

	private void processInstall(SSSNode ability) {
		
		HashSet<SSSNode> types =	ability.getAllClassesThisBelongsToo();
		Gdx.app.log(logstag, "processInstall ability"+ability.PURI);
		
		//should only be one type for now
		//but in future we could allow multi-types?

		
		if (types.contains(StaticSSSNodes.STMemoryAbility)){
			//its a type of inventory
			installInventory(ability);
		}
		
		if (types.contains(StaticSSSNodes.conceptgun)){ //NOTE: abilitys should always be subtypes of whats specified here
			
			if (PlayersData.playersGUI!=null){
				Gdx.app.log(logstag, "installing conceptgun");
			//its a type of concept gun and we already have a GUI
				installConceptGun(ability);
			} else {
			//else complain we have no GUI
				Gdx.app.log(logstag, "installing conceptgun requested but we have no gui yet");
				slot.ejectConcept(); //ejects it 
				feedback.getStyle().setColor(Color.RED);
				feedback.setText("ConceptGun Requires GUI to be installed!");
				//should reset after a period
				resetFeedbackAfterPause(3f);
				return;
			}
			
		}
		
		
		if (types.contains(StaticSSSNodes.decoder)){
			//its a type of language decoder
			installDecoder(ability);
		}
		//
		if ( types.contains(StaticSSSNodes.gui)   ){
			Gdx.app.log(logstag, "installing gui");
			installGUI(ability);
		}
		
		
		//add to the players information
		//(in future we use PlayersData as a save system)
		
		PlayersData.addItemToDatabase(ability, "local",false); //ensure its stored, but it might be already
		PlayersData.addSoftwareAsRunning(ability,false);
		
		//update running software label
		updateInstalledLabel();
		
		//remove concept from slot now its been used to install
		slot.ejectConcept(); //might not want to fire any negative looking "rejection" style eject
		
		//tell hub to update
		parenthub.reGenerateLocationContents();
	}

	private void updateInstalledLabel() {
		String runningSoftwareString = PlayersData.getAllRunningSoftwareAsString();
		RunningSoftwareLabel.setText("Currently Installed : "+runningSoftwareString);
		
	}
	
	
	private void installDecoder(SSSNode ability) {		
		//do nothing! merely this node being stored at the players location is enough
	}
	

	private void installConceptGun(SSSNode ability) {

		//temp only one gun type right now
		if (PlayersData.playersConceptGun==null){
			
			ConceptGunPanel newConceptGun = new ConceptGunPanel();
			newConceptGun.setToPosition(new Vector3(350f,1185f,0f));
			GWTishModelManagement.addmodel(newConceptGun);//,GWTishModelManagement.RenderOrder.zdecides);
		
			PlayersData.playersConceptGun = newConceptGun;
			
			newConceptGun.updateParameters(ability);
			
			feedback.setText("( "+ability.getPLabel()+" installed )");
			feedback.getStyle().setColor(Color.GREEN);
			resetFeedbackAfterPause(4f);
			
			MainExplorationView.infoPopUp.displayMessage("ConceptGun Installed", Color.GREEN);
			
		} else {
			
			//remove old from running
			PlayersData.removeSoftwareAsRunning(PlayersData.playersConceptGun.ConceptGunsNode,false);
			
			//update panel
			PlayersData.playersConceptGun.updateParameters(ability);			
			
			//set feedback
			feedback.setText("( "+ability.getPLabel()+" installed )");
			feedback.getStyle().setColor(Color.GREEN);
			resetFeedbackAfterPause(4f);
			
		    //set a message
			MainExplorationView.infoPopUp.displayMessage("New ConceptGun Installed", Color.GREEN);
			
		}
		
		if (PlayersData.playersGUI!=null){
			//enable button
			PlayersData.playersGUI.enableConceptGunButton();
		}
		
		
		
	}
	private void installGUI(SSSNode ability){
		//setToDefaultPosition()
		
		//if the gui does not already exists;
				if (PlayersData.playersGUI==null){
				
					NewGUIBar newGui = new NewGUIBar();
					//newGui.setToPosition(new Vector3(350f,1185f,0f));
					GWTishModelManagement.addmodel(newGui);//,GWTishModelManagement.RenderOrder.zdecides);
				
					PlayersData.playersGUI = newGui;
					
					newGui.updateParameters(ability);
					
					feedback.setText("( "+ability.getPLabel()+" installed )");
					feedback.getStyle().setColor(Color.GREEN);
					resetFeedbackAfterPause(4f);
					
					MainExplorationView.infoPopUp.displayMessage("GUI Installed", Color.GREEN);
					
				} else {
					//remove the old parameters from the players data
					//PlayersData.playerslocationcontents.removeNodeFromThisSet(PlayersData.playersInventoryPanel.inventorysNode);	

					//PlayersData.removeItemFromDatabase(PlayersData.playersInventoryPanel.inventorysNode);	
					PlayersData.removeSoftwareAsRunning(PlayersData.playersGUI.guisNode,false);
					
					
					
					//(the new one gets added in the processInstall function that calls this)
					
				    //else we update the existing panel to this new set of ability's for it
					PlayersData.playersGUI.updateParameters(ability);			
					

					feedback.setText("( "+ability.getPLabel()+" installed )");
					feedback.getStyle().setColor(Color.GREEN);
					resetFeedbackAfterPause(4f);
				//set a message
					MainExplorationView.infoPopUp.displayMessage("New GUI Installed", Color.GREEN);
					
					
				}
		
	}
	private void installInventory(SSSNode ability) {
		
		//if the inventory does not already exists;
		if (PlayersData.playersInventoryPanel==null){
		
			InventoryPanel inventory = new InventoryPanel();
		
			inventory.setToPosition(new Vector3(350f,1185f,0f));
			GWTishModelManagement.addmodel(inventory);//,GWTishModelManagement.RenderOrder.zdecides);
		
			PlayersData.playersInventoryPanel = inventory;
			
			//we no longer pin to screen, instead we go into the GUI panel
			if (PlayersData.playersGUI!=null){
				PlayersData.playersGUI.enablePlayersInventory();
			}
		
			inventory.updateParameters(ability);
			
			feedback.setText("( "+ability.getPLabel()+" installed )");
			feedback.getStyle().setColor(Color.GREEN);
			resetFeedbackAfterPause(4f);
			
			MainExplorationView.infoPopUp.displayMessage("Inventory Installed", Color.GREEN);
			
		} else {
			//remove the old parameters from the players data
			//PlayersData.playerslocationcontents.removeNodeFromThisSet(PlayersData.playersInventoryPanel.inventorysNode);	

			//PlayersData.removeItemFromDatabase(PlayersData.playersInventoryPanel.inventorysNode);	
			PlayersData.removeSoftwareAsRunning(PlayersData.playersInventoryPanel.inventorysNode,false);
			
			
			
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
				GWTishModelManagement.removeAnimating(this);
				queuedInstall = null;
			}
			
		}
	}
	/*
	@Override
	public void fireTouchUp() {
		Gdx.app.log(logstag,"_-(fireTouchUp)-_");
		
		if (STMemory.isHoldingItem()){
			
			Gdx.app.log(logstag,"_-(mouse up while holding:"+STMemory.currentlyHeldNEW.itemsnode.getPLabel()+")-_");
			 
			onDrop(STMemory.currentlyHeldNEW);
			 
		}
	
	}*/
	
	
	@Override
	public Vector3 getDefaultCameraPosition() {
		//gets the center of this email on the stage
		Vector3 center = getCenterOnStage();
		center.z = ScreenUtils.getSuitableDefaultCameraHeight();
		
		return center;
	}
		

}
