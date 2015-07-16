package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
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
	
	public ConceptStoreObject(LocationHub locationHub) {
		parentLocation = locationHub;
		super.setSpaceing(5f);
		
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
		Label testLabelLala = new Label("Test Data Label:"+newConceptObject.itemsnode.getPLabel());
		testLabelLala.setLabelBackColor(Color.CLEAR);
		
		testLabelLala.setToscale(new Vector3(0.2f,0.2f,0.2f)); //half size

		
		
		this.add(testLabelLala);
		
	}
	
	
	
	

}
