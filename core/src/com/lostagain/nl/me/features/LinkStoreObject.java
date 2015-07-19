package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.semantic.SSSNode;
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
 * They are locked by default until scanned, whereupon copys can be taken
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
		
		super.getStyle().setBackgroundColor(Color.CLEAR);
		getStyle().setBorderColor(Color.CLEAR);
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


	public void addLink(SSSNode dest) {
		
		LinkBar newBar = new LinkBar(dest,this);
		
		this.add(newBar);
		
		
	}

	public void clearLinks() {
		super.clear();
		this.add(TitleLabel);
		
	}

	

}
