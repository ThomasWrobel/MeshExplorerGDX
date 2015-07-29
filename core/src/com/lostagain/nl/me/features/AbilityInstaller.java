package com.lostagain.nl.me.features;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
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
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * Provides one slot that lets the player install features to their GUI or location
 * A progress bar is also there to indicate the installing, but it works much faster then scanners
 * 
 * @author Tom
 *
 */
public class AbilityInstaller extends Widget implements GenericMeshFeature {
	private static final String STANDARD_FEEDBACK_MESSAGE = "(please drop an ability onto the slot)";

	final static String logstag = "ME.AbilityInstaller";

	static float width  = 380f;
	static float height = 200f;
	
	//data
	LocationHub parenthub;
	
	//widgets
	Label title = new Label("Installer");
	ConceptObjectSlot slot = new ConceptObjectSlot();
	ProgressBar installerBar = new ProgressBar(30,0,100);
	
	Label feedback = new Label(STANDARD_FEEDBACK_MESSAGE);
	
	public AbilityInstaller(LocationHub locationHub) {
		
		super(width, height,MODELALIGNMENT.TOPLEFT);
		super.getStyle().clearBackgroundColor();
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
		
		PosRotScale installerBarPosition = new PosRotScale(40f,-80f,3f);
		attachThis(installerBar, installerBarPosition);
		
		
		
		feedback.setToScale(new Vector3(0.6f,0.6f,0.6f));
		feedback.getStyle().clearBackgroundColor();
		
		//use vertical panel to auto-centralise
		VerticalPanel feedbackBar  = new VerticalPanel();
		feedbackBar.getStyle().clearBackgroundColor();
		feedbackBar.getStyle().clearBorderColor();	
		feedbackBar.setMinSize(width,30);		
		feedbackBar.add(feedback);
		
		attachThis(feedbackBar, new PosRotScale(0f,-111f,3f));
		
		
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
			resetFeedbackAfterPause();
			return;
		}
		
		//check we havnt got this one installed already
		boolean hasAbility = PlayersData.playerslocationcontents.containsNode(ability);
		
		if (hasAbility){
			
			feedback.setText("(ability already installed)");
			feedback.getStyle().setColor(Color.RED);
			
			resetFeedbackAfterPause();
			slot.ejectConcept(); 
			return;
		}
		
		//detect what ability is being installed
		feedback.setText("(ability accepted, please wait)");
		
		//process install
		
		//first check if we have something the same TYPE
		//in which case we run the upgrade function (normally means just changing stats)
		
		//if not we create and add the new page
		
	}

	private void resetFeedbackAfterPause() {
		
		Timer.schedule(new Task() {				
			@Override
			public void run() {
				feedback.getStyle().setColor(Color.WHITE);
				feedback.setText(STANDARD_FEEDBACK_MESSAGE);
				
			}
		}, 3f);
		
		
		
		
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

}
