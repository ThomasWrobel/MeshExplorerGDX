package com.lostagain.nl.me.LocationGUI;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
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

	static Logger Log = Logger.getLogger("ME.GUIBar");

	InterfaceButton goback =     new InterfaceButton("<< Back",true);
	InterfaceButton myHome =     new InterfaceButton("My  Home",true);
	InterfaceButton myContents = new InterfaceButton("My  Data",false); //not visible unless we have data
	InterfaceButton myLinks =    new InterfaceButton("My Links",true);
	InterfaceButton myEmails =   new InterfaceButton("My Emails",true);
	Label backgroundobject;
	ArrayList<InterfaceButton> allLinks = new ArrayList<InterfaceButton>();
	
	boolean closed = true;	
	boolean justopened=false;

	Long lastTime =0l;

	private boolean setup=false;
	
	public GUIBar() {
		
		super.setFillParent(true);
		super.setDebug(true);
		
		//super(DefaultStyles.linkstyle);		
		
		//super.setBackground(DefaultStyles.colors.newDrawable("white", Color.DARK_GRAY));
	
		LabelStyle back = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));
		
		Color ColorM = new Color(Color.DARK_GRAY);
		ColorM.a=0.5f;
		back.background = DefaultStyles.colors.newDrawable("white", ColorM);
		
		
				
		backgroundobject = new Label("",back);
		
		//backgroundobject.setSize(85, 250);
		backgroundobject.setPosition(0,340);	
		
		
		
		myHome.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				MainExplorationView.gotoHomeLoc();	
				PlayersData.homeLoc.locationsHub.gotoSecplace();
				
				setOnlyButtonDown(myHome);
				
		}});
		
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
		

		
		//myHome.setPosition(5,440);	
		//myEmails.setPosition(5,410);
	//	myLinks.setPosition(5,380);				
		//myContents.setPosition(5,350);		

		myContents.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				triggerInventoryView();

				ME.playersInventory.validate();
				

			}

		});

		
		goback.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				goback.setDownForABit();
				MainExplorationView.gotoLastLocation();
			}

		});
		
		allLinks.add(goback);
		allLinks.add(myHome);
		allLinks.add(myEmails);
		allLinks.add(myLinks);
		allLinks.add(myContents);
		
		Log.info("setting up");
		ME.playersInventory.setVisible(false);
	
		
		refreshlinks();

		super.validate();
	}
	
	protected void setOnlyButtonDown(InterfaceButton thisone) {
		
		for (InterfaceButton link : allLinks) {
			
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
		
	}


	public void setDataVisible(boolean visible){
		
		myContents.isVisible=visible;
		
		 refreshlinks();
		 setupInventory(); 
	}
	
	private void refreshlinks() {
		super.clearChildren();
		super.addActor(backgroundobject);
		super.addActor(ME.playersInventory);
		int y = 440; //start at 440 and work our way down the page with each new shortcut

		ME.playersInventory.validate();
		for (InterfaceButton link : allLinks) {		
			
			if (link.isVisible==true){
				
				link.setPosition(5,y);	
				super.addActor(link);
				y=y-30;
			}
			
		}

		backgroundobject.setSize(85, 200);
		backgroundobject.setPosition(0,y+20);			
			

		
	}

	private void triggerInventoryView() {
		Log.info("triggerInventoryView");
		if (closed){
			openInventory();
			myContents.setDownStyle();
			
		} else {
			closeInventory();
			myContents.setUpStyle();
			
			
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
		
			
		} 
		
		ME.playersInventory.setVisible(true);
		
		closed=false;
		justopened=true;
		
	}
	
	
	
	public void setupInventory() {

		if (setup){			
			return;		
		} 
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
		setup=true;
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
/*
	protected static class InterfaceButton extends TextButton 
	{

		public InterfaceButton(String string) {
			super(string,DefaultStyles.buttonstyle);


		}


	}
	*/
	
	

}
