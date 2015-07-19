package com.lostagain.nl.me.features;

import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;

/**
 * represents 1 single email page.
 * If a email is a reply to another one, it automatically links itself to that one
 * 
 * @author Tom
 *
 */
public class Email extends VerticalPanel implements GenericMeshFeature {

	Email parentEmail; //what this is a reply to
	EmailHub parentHub;
	
	
	public Email(SSSNode sssNode, SSSNode writtenIn) {

		Label test = new Label(sssNode.getPLabel());
		this.add(test);
		
	}

	@Override
	public AnimatableModelInstance getAnimatableModelInstance() {
		return this;
	}

	@Override
	public void updateApperance(float alpha, FeatureState currentState) {
		
		this.setOpacity(alpha);
		
	}

}