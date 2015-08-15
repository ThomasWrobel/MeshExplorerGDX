package com.lostagain.nl.me.features;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.GWTish.Button;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;
import com.lostagain.nl.shaders.MySorter.ZIndexAttribute;
/**
 * provides togglable shortcuts for various game functions such as going home, activating the concept gun, or opening the inventory
 * 
 * @author Tom
 *
 */
public class NewGUIBar extends VerticalPanel {
	final static String logstag = "ME.NewGUIBar";

	ArrayList<InterfaceButton> allButtons = new ArrayList<InterfaceButton>();
	
	InterfaceButton goback =     new InterfaceButton("<< Back",true);

	final static String CGunOpen   =  "My CGun<<";
	final static String CGunClosed =  "My CGun>>";

	InterfaceButton myCGun =     new InterfaceButton(CGunClosed,false); //will be false by default later when the gun is in the game correctly

	InterfaceButton myHome =     new InterfaceButton("My  Home",true);
	InterfaceButton myContents = new InterfaceButton("My  Data",false); //not visible unless we have data (will be removed in favor of temp memory)
	InterfaceButton mySTMemory = new InterfaceButton("My STMem",false); //not visible unless we have data (new temp memory)

//	InterfaceButton myLinks =    new InterfaceButton("My Links",true);
//	InterfaceButton myEmails =   new InterfaceButton("My Emails",true);
	boolean pinned = false; //are we pinned to the interface 		

	/**
	 * describes the state of this gui, but atm there really isnt one, its just a static thing for the whole game.
	 * in future we might allow different styles?
	 */
	public SSSNode guisNode;

	//private boolean hasInventoryPanel = false;
	
	
	
	public NewGUIBar() {
		super();
		
		getStyle().setBackgroundColor(new Color(0.1f,0.1f,0.1f,0.8f));
		setPadding(8f);
				
		//set up the button handlers
		myCGun.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick() {				
				Gdx.app.log(logstag, "myCGun clicked");
				
				//toggle concept gun
				if (!PlayersData.playersConceptGun.isVisible()){
					
					PlayersData.playersConceptGun.show();
					PlayersData.playersConceptGun.setEnabled(true);

					myCGun.setText(CGunOpen);
					myCGun.setDownStyle();
					
					ME.disableMovementControl(true);
					
				} else {
					PlayersData.playersConceptGun.hide();				
					PlayersData.playersConceptGun.setEnabled(false);

					myCGun.setText(CGunClosed);
					myCGun.setUpStyle();
				
					ME.disableMovementControl(false);
				}				
				
			}
		} );
		



		myHome.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick() {	

				ME.gotoHomeLoc();	
				PlayersData.homeLoc.locationsHub.gotoSecplace();

			//	setOnlyButtonDown(myHome);
				myHome.setDownForABit();
			}});
		
		/*
		myEmails.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				MainExplorationView.gotoHomeLoc();
				PlayersData.homeLoc.locationsHub.gotoEmail();

				setOnlyButtonDown(myEmails);

		}});

		myLinks.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				MainExplorationView.gotoHomeLoc();
				PlayersData.homeLoc.locationsHub.gotoLinks();


				setOnlyButtonDown(myLinks);

		}});
		 */


		//myHome.setPosition(5,440);	
		//myEmails.setPosition(5,410);
		//	myLinks.setPosition(5,380);				
		//myContents.setPosition(5,350);		
		/*
		myContents.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				triggerInventoryView();

				//ME.playersInventory.validate();


			}

		});*/

/*

		mySTMemory.setName(DataObjectDropTarget.DROPSPOTTYPENAME); //sets this label as a drop target
		mySTMemory.setUserObject(this); //tells this label to use the GUIBar to handle drop functions	

		//actually we set them all labels as drop targets, so that dragging anywhere will add an item
		myHome.setName(DataObjectDropTarget.DROPSPOTTYPENAME); //sets this label as a drop target
		myHome.setUserObject(this); //tells this label to use the GUIBar to handle drop functions	
		myCGun.setName(DataObjectDropTarget.DROPSPOTTYPENAME); //sets this label as a drop target
		myCGun.setUserObject(this); //tells this label to use the GUIBar to handle drop functions	
		goback.setName(DataObjectDropTarget.DROPSPOTTYPENAME); //sets this label as a drop target
		goback.setUserObject(this); //tells this label to use the GUIBar to handle drop functions	


		mySTMemory.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick() {	


				if (STMemory.isHoldingItem()){
					clickedWhileHolding();					
				}

				triggerInventoryView();



			}

		});*/

		goback.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick() {	
				goback.setDownForABit();
				ME.gotoLastLocation();
			}

		});


		allButtons.add(myCGun);
		allButtons.add(goback);
		allButtons.add(myHome);		
		allButtons.add(mySTMemory);
	
		
		layoutButtons();
		
		setToDefaultPosition();
		
		setZIndex(100);
		
	}
	
	
	/**
	 * adds a z index override and sets it to the supplied value
	 * use clear to revert to natural ordering
	 * @param opacity
	 */
	public void setZIndex(int index){
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial();
		infoBoxsMaterial.set(new ZIndexAttribute(index));
		
	}
	
	/**
	 * adds a z index override and sets it to the supplied value
	 * use clear to revert to natural ordering
	 * @param opacity
	 */
	public void clearZIndex(int index){
		//get the material from the model
		Material infoBoxsMaterial =this.getMaterial();	
		infoBoxsMaterial.remove(ZIndexAttribute.ID);
		
		
	}
	
	
	
	
	
	
	
	private void setToDefaultPosition() {
		Gdx.app.log(logstag, "setting tyo default position");
		this.setToScale(new Vector3(0.4f,0.4f,0.4f));
		
		MainExplorationView.camera.attachThisRelativeToScreen(this,0,0,222f); //0,0 is top left
		
		
		this.pinned=true;
	}
	private void layoutButtons(){
		Gdx.app.log(logstag, "laying out buttons");
		//clear existing
		this.clear();
		
		//this.add(new Label("GUI TEST:"));
		
		
		for (InterfaceButton button : allButtons) {
			if (button.isVisible==true){
				
				button.setToScale(new Vector3(1f,1f,1f)); //ensure its at the default scale before adding
				button.show();
				add(button);
				
			} else {
				Gdx.app.log(logstag, "  button not visible");
				button.hide(); //ensure its hidden
			}
		}
		
		//inventory at end if we have it
		 if ( PlayersData.playersInventoryPanel  != null){
			 PlayersData.playersInventoryPanel.show();
			 PlayersData.playersInventoryPanel.setToScale(new Vector3(1f,1f,1f));
			 add(PlayersData.playersInventoryPanel);
				
		 }
		
		
		
	}

	/*
	protected void setOnlyButtonDown(InterfaceButton thisone) {

		for (InterfaceButton link : allButtons) {

			//the data button is left alone as that toggles on/off separately
			if (link==myContents){
				continue;
			}

			if (link!=thisone){
				link.setUpStyle();
			} else {
				thisone.setDownStyle();
			}

		}

	}*/

	/**
	 * Interface buttons are designed to be the same size as concept slots
	 * This is so the interface fits neatly when the inventory is added at the button
	 * @author Tom	 
	 **/
	static class InterfaceButton extends Label {
		
		static float width  = ConceptObjectSlot.WIDTH;
		static float height = ConceptObjectSlot.HEIGHT;
		
		public Boolean isVisible = true;		
		public Boolean isDisabled = false;
		
		public InterfaceButton(String Label,boolean isVisible) {
			super(Label);
			//super.setSizeAs(width, height);
			super.getStyle().clearBackgroundColor();
			
			this.isVisible=isVisible;
			
			ModelManagment.addmodel(this, RenderOrder.zdecides);
			setUpStyle();
			if (isVisible){
				this.show();
			} else {
				this.hide();
			}
			
		}
			
		public void setDisabledStyle(){
			
			isDisabled = true;		
			//super.getStyle().setBackgroundColor(DefaultStyles.lockedLabel);
			
			super.getStyle().setColor(DefaultStyles.lockedLabel);;
			
		}
		
		public void setUpStyle(){

			isDisabled = false;	
			super.getStyle().setColor(DefaultStyles.unlockedLabel);;
			
			//super.getStyle().setBackgroundColor( DefaultStyles.unlockedLabel);
			
			
			
		}
			
		public void setDownStyle(){

			isDisabled = false;		
			super.getStyle().setColor(DefaultStyles.labelpressed);
			//super.getStyle().setBackgroundColor(DefaultStyles.labelpressed);	
			
			
		}
		
		public void setDownForABit(){

			setDownStyle();
			
			Timer.schedule(new Task() {
				
				@Override
				public void run() {
					setUpStyle();
				}
			}, 0.5f);
			
		}
	}

	/**
	 * The gui doesn't really have any custom params right now.
	 * In future we might allow different nodes to give different styles to it.
	 * it should also remember what buttons are enabled?
	 * @param ability
	 */
	public void updateParameters(SSSNode ability) {
		// TODO Auto-generated method stub
		
	}
	
	public void enableConceptGunButton() {
		myCGun.isDisabled = false;
		myCGun.isVisible = true;
		this.layoutButtons(); 
	}
	public void enablePlayersInventory() {		
		//hasInventoryPanel  = true;
		PlayersData.playersInventoryPanel.setToScale(new Vector3(1f,1f,1f));
		add(PlayersData.playersInventoryPanel);
		
		
	}
	
}
