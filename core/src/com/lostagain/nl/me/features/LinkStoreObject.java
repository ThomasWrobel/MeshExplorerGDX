package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * This is a panel designed to store concept objects
 * They are locked by default untill scanned, whereupon copys can be taken
 * 
 * @author Tom
 *  *
 */
public class LinkStoreObject extends VerticalPanel implements GenericMeshFeature {

	final static String logstag = "ME.LinkStoreObject";


	LocationHub parentLocation;
	Label TitleLabel;
	
	public LinkStoreObject(LocationHub locationHub) {
		parentLocation = locationHub;
		super.setSpaceing(5f);
		super.setPadding(15f); //padding around border
		TitleLabel = new Label("Links");
		TitleLabel.setToscale(new Vector3(0.6f,0.6f,0.6f)); 
		TitleLabel.setLabelBackColor(Color.CLEAR);
		super.setBackgroundColor(Color.PURPLE);
		this.add(TitleLabel);
		
	}
	
	@Override
	public AnimatableModelInstance getAnimatableModelInstance() {
		return this;
	}
	@Override
	public void updateApperance(float alpha, FeatureState currentState) {

		setOpacity(alpha);
	}

	public void addLink(Location dest) {
		
		//this will need to be changed to a HorizontalPage + progressbar+conceptObjectSlot with the concept object in it.
		//Label testLabelLala = new Label("Test Data Label:"+newConceptObject.itemsnode.getPLabel());
		//.setLabelBackColor(Color.CLEAR);
		//
		//testLabelLala.setToscale(new Vector3(0.6f,0.6f,0.6f)); //half size

		LinkBar newBar = new LinkBar(dest);
		
		
		this.add(newBar);
		
	}
	
	//temp link object, will be replaced
	class LinkBar extends Widget {
		
	
		static final float StandardWidth = 300;
		ProgressBar scanbar = new ProgressBar(30,20,StandardWidth);
		
		
		ConceptObjectSlot slot = new ConceptObjectSlot();
		
		public LinkBar(Location dest){
			super(StandardWidth, 30);
			this.setBackgroundColor(Color.CLEAR);
			scanbar.setValue(15);
			Gdx.app.log(logstag,"adding scan bar widget.");
			
			super.attachThis(scanbar, new PosRotScale(0,0,3));
			
			
			
			Label testLabelLala = new Label("||");
			testLabelLala.setLabelBackColor(Color.CLEAR);	
			testLabelLala.setAlignment(MODELALIGNMENT.TOPLEFT);
			super.attachThis(testLabelLala, new PosRotScale(0,0,4));
			
			
		}
		
	}
	
	

}
