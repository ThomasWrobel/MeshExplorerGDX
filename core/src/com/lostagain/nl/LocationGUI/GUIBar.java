package com.lostagain.nl.LocationGUI;

import java.util.logging.Logger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;



public class GUIBar extends WidgetGroup {

	static Logger Log = Logger.getLogger("GUIBar");

	InterfaceButton myHome =     new InterfaceButton("My  Home");
	InterfaceButton myContents = new InterfaceButton("My  Data");
	InterfaceButton myLinks =    new InterfaceButton("My Links");
	InterfaceButton myEmails =   new InterfaceButton("My Emails");

	boolean closed = true;	
	boolean justopened=false;

	Long lastTime =0l;

	private boolean setup=false;
	
	public GUIBar() {
		super.setFillParent(true);
		super.setDebug(true);
		
		//super(DefaultStyles.linkstyle);		
		//super.setBackground(DefaultStyles.colors.newDrawable("white", Color.DARK_GRAY));
		myHome.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				MainExplorationView.gotoHomeLoc();	
				
		}});
		
		myEmails.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				MainExplorationView.gotoHomeLoc();
				PlayersData.homeLoc.gotoEmail();
				
		}});

		myLinks.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				MainExplorationView.gotoHomeLoc();
				PlayersData.homeLoc.gotoLinks();
				
		}});
		
		myHome.setPosition(10,430);		
		super.addActor(myHome);
		
		myEmails.setPosition(10,390);				
		super.addActor(myEmails);

		myLinks.setPosition(10,350);		
		super.addActor(myLinks);
		
		myContents.setPosition(10,310);		
		super.addActor(myContents);

		myContents.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				triggerInventoryView();

				ME.playersInventory.validate();


			}

		});


		Log.info("setting up");
		ME.playersInventory.setVisible(false);
		super.addActor(ME.playersInventory);
		
		//setupInventory();
		//closeInventory();
		
		//	ME.playersInventory.setPosition(22, 222);
		//	super.addActor(ME.playersInventory);

		super.validate();
	}

	private void triggerInventoryView() {
		Log.info("triggerInventoryView");
		if (closed){
			openInventory();
		} else {
			closeInventory();
		}

	}
	
	/*
	@Override
	public void validate(){
		//Log.info("validate super.getWidth()"+super.getWidth());
		//super.validate();
	//	super.removeActor(ME.playersInventory);
		//setupInventory();
		
	}*/
	
	private void openInventory() {
		Log.info("openInventory");
		
		if (!setup){
			setupInventory();
			setup=true;
			
		} 
		
		ME.playersInventory.setVisible(true);
		
		closed=false;
		justopened=true;
		
	}
	
	
	
	public void setupInventory() {
	
		Log.info("setupInventory");
		
		
		ME.playersInventory.setPrefWidth(super.getWidth());

		Log.info("width is "+super.getWidth());
		
		ME.playersInventory.setHeight(200);
		
		
		ME.playersInventory.pack();

		float X = myContents.getX();
		float Y = myContents.getY()-ME.playersInventory.getHeight();

		Log.info("popping up inventory at:"+X+","+Y);
		ME.playersInventory.setPosition(X, Y);
		
		
		super.validate();
	}

	public void closeInventory() {

		Log.info("closeInventory");
		
	//	if (!closed ){
			ME.playersInventory.setVisible(false);
			closed=true;
		//}
		
		/*
		if (!closed ){
			super.removeActor(ME.playersInventory);
			closed=true;
		}
		justopened=false;*/
	}

	protected static class InterfaceButton extends TextButton 
	{

		public InterfaceButton(String string) {
			super(string,DefaultStyles.buttonstyle);


		}


	}

}
