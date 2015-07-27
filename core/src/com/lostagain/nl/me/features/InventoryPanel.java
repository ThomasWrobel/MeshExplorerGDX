package com.lostagain.nl.me.features;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.ToggleButton;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * Experimental inventory class
 * will eventually replace the 2d on I hope
 * 
 * Essentially a list of slots
 * @author Tom
 *
 */
public class InventoryPanel extends VerticalPanel  implements GenericMeshFeature  {

	final static String logstag = "ME.InventoryPanel";
	
	ArrayList<ConceptObjectSlot> inventorySlots = new ArrayList<ConceptObjectSlot>();

	int NumberOfSlots = 7; //number of inventory slots
	boolean pinned = false; //are we pinned to the interface 		
		
	//widgets
	Label Title = new Label("Inventory");
	
	ToggleButton pinButton;
	
	public InventoryPanel(){
		
		//Appearance
		this.getStyle().setBackgroundColor(new Color(0.1f,0.1f,0.1f,0.8f));
		
		
		//title		
		Title.getStyle().clearBackgroundColor();
		Title.setToscale(new Vector3(0.6f,0.6f,0.6f));
		
		
		Label PinUp = new Label("(  Pin  )");
		Label PinDown = new Label("(Float)");
		PinUp.getStyle().clearBackgroundColor();
		PinDown.getStyle().clearBackgroundColor();
		
		PinUp.setToscale(new Vector3(0.6f,0.6f,0.6f));
		PinDown.setToscale(new Vector3(0.6f,0.6f,0.6f));

		
		Runnable onPin = new Runnable(){

			@Override
			public void run() {

				Gdx.app.log(logstag,"---pin clicked---");
				if (pinButton.getValue()){
					setPinnedToCamera(true);					
				} else{
					setPinnedToCamera(false);	
				}
			}
			
		};
		

		pinButton = new ToggleButton(PinUp,PinDown,onPin);
		HorizontalPanel topBar = new HorizontalPanel();
		topBar.setPadding(10f);
		
		topBar.getStyle().clearBackgroundColor();
		
		topBar.add(Title);
		topBar.add(pinButton);
		this.add(topBar);
		
		//add the slots
		for (int i = 0; i < NumberOfSlots; i++) {
			
			ConceptObjectSlot newSlot = new ConceptObjectSlot();
			this.add(newSlot);
			inventorySlots.add(newSlot);
			
		}
		
	}
	
	protected void setPinnedToCamera(boolean b) {
		if (b){
			//pin it
			
			//we need to get the existing displacement from cam  first
			Vector3 position = this.getTransform().position.cpy();
			Vector3 camera   = MainExplorationView.camera.getCamPosRotScale().position.cpy();
			
			Vector3 displacement = position.sub(camera);
			
			//then attach it with that displacement
			MainExplorationView.camera.attachThis(this, new PosRotScale(displacement));
			
		} else {
			//unpin it
			Gdx.app.log(logstag,"--removing from camera---");
			MainExplorationView.camera.deattachThis(this);
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
	
	
	
	
}
