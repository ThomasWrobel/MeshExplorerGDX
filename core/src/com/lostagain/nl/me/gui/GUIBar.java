package com.lostagain.nl.me.gui;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.TimeUtils;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.me.LocationGUI.DefaultStyles;
import com.lostagain.nl.me.LocationGUI.InterfaceButton;



public class GUIBar extends WidgetGroup {

	static Logger Log = Logger.getLogger("ME.GUIBar");
	final static String logstag = "ME.GUIBar";
	
	InterfaceButton goback =     new InterfaceButton("<< Back",true);
	InterfaceButton myHome =     new InterfaceButton("My  Home",true);
	InterfaceButton myContents = new InterfaceButton("My  Data",false); //not visible unless we have data (will be removed in favor of temp memory)
	InterfaceButton mySTMemory = new InterfaceButton("ST Memory",true); //not visible unless we have data (will be removed in favor of temp memory)
	
	InterfaceButton myLinks =    new InterfaceButton("My Links",true);
	InterfaceButton myEmails =   new InterfaceButton("My Emails",true);
	Label backgroundobject;
	ArrayList<InterfaceButton> allLinks = new ArrayList<InterfaceButton>();
	
	//memory popup
	TempMemory STMemoryPop = new TempMemory();
	
	public ConceptGun ConceptGun = new ConceptGun();
	
	boolean closed = true;	
	
	boolean justopened=false;

	Long lastTime =0l;

	private boolean setup=false;
	private boolean setup2=false;
	
	//Experimental
	public DragAndDrop dragAndDrop = new DragAndDrop();
	
	public GUIBar() {
		
		
		//super.setFillParent(true);
		//super.setDebug(true);
		
		//super(DefaultStyles.linkstyle);		
		
		//super.setBackground(DefaultStyles.colors.newDrawable("white", Color.DARK_GRAY));
	
		LabelStyle back = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));
		
		Color ColorM = new Color(Color.DARK_GRAY);
		ColorM.a=0.5f;
		back.background = DefaultStyles.colors.newDrawable("white", ColorM);
		
		STMemoryPop.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				Log.info("_________TempMemory clicked _____");
				//if (Old_Inventory.currentlyHeld!=null){
				//	clickedWhileHolding();					
				//}
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 		 		
		 		{
				Log.info("_________TempMemory clicked _____");
				Gdx.app.log("Example", "touch started at (" + x + ", " + y + ")");
				
				return false;
				
		 		}


		});
		
	
		backgroundobject = new Label("",back);
		
		//backgroundobject.setSize(85, 250);
		backgroundobject.setPosition(0,340);	
		
		backgroundobject.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				Gdx.app.log(logstag,"clicked");
				if (Old_Inventory.currentlyHeld!=null){
					clickedWhileHolding();					
				}
				
		}});
		
		myHome.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				MainExplorationView.gotoHomeLoc();	
				PlayersData.homeLoc.locationsHub.gotoSecplace();
				
				setOnlyButtonDown(myHome);
				
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
		
		mySTMemory.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				
				if (Old_Inventory.currentlyHeld!=null){
					clickedWhileHolding();					
				}
				
				triggerInventoryView();

				

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
		//allLinks.add(myEmails);
		//allLinks.add(myLinks);
		allLinks.add(myContents);
		allLinks.add(mySTMemory);
		
		
		
		
		Gdx.app.log(logstag,"setting up");
		ME.playersInventory.setVisible(false);

		STMemoryPop.setVisible(false);
		
		populateGUI();
		//refreshlinks();

		//super.validate();
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
		
		if (myContents.isVisible!=true){
			myContents.isVisible=visible;
		
			invalidate();
		 //refreshlinks();
		 setupInventory(); 
		}
	}
	
	//@Override
	//public void validate(){
		//super.validate();
		//refreshlinks(); 
	//}
	
	/** places all the children on this widget **/
	public void populateGUI(){
		this.setSize(95, 220);
		
		super.clearChildren();
		addActor(backgroundobject);		
		addActor(STMemoryPop);
		addActor(ConceptGun); 
		ConceptGun.invalidate();
		//ConceptGun.validate();
		
		int y = 440; //start at 440 and work our way down the page with each new shortcut

		
		for (InterfaceButton link : allLinks) {		
			
			if (link.isVisible==true){
				
				link.setPosition(5,y);	
				super.addActor(link);
				y=y-30;
			}
			
		}

		backgroundobject.setPosition(0,y+20);	
		backgroundobject.setSize(95, 220);
		
		setupSTMemoryPop();
	
		this.invalidate();
	}
	


	private void triggerInventoryView() {
		
		Gdx.app.log(logstag,"triggerInventoryView");
		
		if (closed){
			openSTMemory();
			mySTMemory.setDownStyle();			
		} else {
			closeSTMemory();
			mySTMemory.setUpStyle();		
			
		}
		
		//old 
		/*
		if (closed){
			openInventory();
			myContents.setDownStyle();
			
		} else {
			closeInventory();
			myContents.setUpStyle();
			
			
		}*/

	}
	
	/*
	@Override
	public void validate(){
		//Log.info("validate super.getWidth()"+super.getWidth());
		//super.validate();
	//	super.removeActor(ME.playersInventory);
		//setupInventory();
		
	}*/
	private void openSTMemory() {
		Gdx.app.log(logstag,"open stm");
		if (!setup2){
			setupSTMemoryPop();
		
			
		} 
		STMemoryPop.setVisible(true);


		Gdx.app.log(logstag,"st width is "+STMemoryPop.getWidth());
		Gdx.app.log(logstag,"st height is "+STMemoryPop.getHeight());

		Gdx.app.log(logstag,"position of st mem is:"+STMemoryPop.getX()+","+STMemoryPop.getY());

		closed=false;
		justopened=true;
		
		//closed=false;
		//justopened=true;
		
	}
	
	private void closeSTMemory() {
		Gdx.app.log(logstag,"close stm");
		
		STMemoryPop.setVisible(false);
		closed=true;
		//closed=false;
		//justopened=true;
		
	}
	
	
	private void openInventory() {
		Gdx.app.log(logstag,"openInventory");
		
		if (!setup){
			setupInventory();
		
			
		} 
		
		ME.playersInventory.setVisible(true);
		
		closed=false;
		justopened=true;
		
	}
	
	
	public void setupSTMemoryPop(){
		
		//STMemoryPop
		Gdx.app.log(logstag,"setup STMemoryPop");
	//	super.addActor(STMemoryPop);
		
		//ME.playersInventory.setPrefWidth(super.getWidth());

		Gdx.app.log(logstag,"width is "+super.getWidth());
		
		STMemoryPop.setHeight(300);
		STMemoryPop.setPrefWidth(92);

		Gdx.app.log(logstag,"setup st width is "+STMemoryPop.getWidth());
		

		float X = mySTMemory.getX();
		float Y = mySTMemory.getY()-STMemoryPop.getHeight();

		Gdx.app.log(logstag,"setup setting position of st mem to:"+X+","+Y);
		
		STMemoryPop.setPosition(X, Y);
		STMemoryPop.invalidate();// .validate();
		setup2=true;
		
		//super.validate();
	}
	public void setupInventory() {

		if (setup){			
			return;		
		} 
		Gdx.app.log(logstag,"setupInventory");
		super.addActor(ME.playersInventory);
		
		ME.playersInventory.setPrefWidth(super.getWidth());

		Gdx.app.log(logstag,"width is "+super.getWidth());
		
		ME.playersInventory.setHeight(200);
		ME.playersInventory.pack();

		float X = myContents.getX();
		float Y = myContents.getY()-ME.playersInventory.getHeight();

		Gdx.app.log(logstag,"popping up inventory at:"+X+","+Y);
		ME.playersInventory.setPosition(X, Y);
		
		
		super.validate();
		setup=true;
		
	}

	public void closeInventory() {

		Gdx.app.log(logstag,"closeInventory");
		
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
	
	//is clicked while holding something, we attempt to add it to the temp memory
	public void clickedWhileHolding(){

		Gdx.app.log(logstag,"clickedWhileHolding");
		
		Boolean success = STMemoryPop.addItem(Old_Inventory.currentlyHeld);

		//if was successfully added we set currently held to nothing
		if (success){
				Old_Inventory.currentlyHeld = null;
		} else {
			
			//should have some feedback here for STMemory full up
			
		}
		
	}

}
