package com.lostagain.nl.me.features;

import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.Widget;
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

	//data
	Location parentLocation;
	
	//widgets
	Label title = new Label("Installer");
	ConceptObjectSlot slot = new ConceptObjectSlot();
	ProgressBar installerBar = new ProgressBar(30,0,100);
	
	public AbilityInstaller(Location parentLocation) {
		super(400, 300,MODELALIGNMENT.TOPLEFT);
		this.parentLocation=parentLocation;
		
		//setup icons
		slot.setCurrentMode(SlotMode.InOnly); 
		
		
		
		//position widgets
		PosRotScale titlePosition = new PosRotScale(0f,0f,3f);
		this.attachThis(title, titlePosition);
		
		PosRotScale slotPosition = new PosRotScale(40f,40f,30f);
		this.attachThis(slot, slotPosition);
		
		PosRotScale installerBarPosition = new PosRotScale(40f,40f,60f);
		this.attachThis(installerBar, installerBarPosition);
		
		
		
	}

	private void installRequested(SSSNode ability){
		
		//detect what ability is being installed
		
		
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
