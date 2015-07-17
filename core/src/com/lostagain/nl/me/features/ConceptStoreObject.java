package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * This is a panel designed to store concept objects
 * They are locked by default untill scanned, whereupon copys can be taken
 * 
 * @author Tom
 *  *
 */
public class ConceptStoreObject extends VerticalPanel implements GenericMeshFeature {
	

	LocationHub parentLocation;
	Label TitleLabel;
	
	public ConceptStoreObject(LocationHub locationHub) {
		parentLocation = locationHub;
		super.setSpaceing(5f);
		
		TitleLabel = new Label("Concept Store Object");
		TitleLabel.setToscale(new Vector3(0.6f,0.6f,0.6f)); 
		TitleLabel.setLabelBackColor(Color.CLEAR);
		
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

	public void addConceptObject(ConceptObject newConceptObject) {
		
		//this will need to be changed to a HorizontalPage + progressbar+conceptObjectSlot with the concept object in it.
		//Label testLabelLala = new Label("Test Data Label:"+newConceptObject.itemsnode.getPLabel());
		//.setLabelBackColor(Color.CLEAR);
		//
		//testLabelLala.setToscale(new Vector3(0.6f,0.6f,0.6f)); //half size

		ConceptObjectContainerBar newBar = new ConceptObjectContainerBar(newConceptObject);
		
		
		this.add(newBar);
		
	}
	
	class ConceptObjectContainerBar extends HorizontalPanel {
		
		float StandardWidth = 300;
		ProgressBar scanbar = new ProgressBar(30,0,200);
		ProgressBar scanbar2 = new ProgressBar(30,0,200);
		
		ConceptObjectSlot slot = new ConceptObjectSlot();
		
		public ConceptObjectContainerBar(ConceptObject newConceptObject){
			scanbar.setValue(15);
			
			add(scanbar);
			Label testLabelLala = new Label("|Test Label|");
			add(testLabelLala);
			Label testLabelLala2 = new Label("|Test Label2|");
			add(testLabelLala2);
			scanbar2.setValue(15);
			add(scanbar2);
		}
		
	}
	
	

}
