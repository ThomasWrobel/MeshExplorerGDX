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
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.me.LocationGUI.InterfaceButton;
import com.lostagain.nl.me.objects.DataObject;



public class GUIBar extends WidgetGroup implements DataObjectDropTarget {

	static Logger Log = Logger.getLogger("ME.GUIBar");
	final static String logstag = "ME.GUIBar";
	
	InterfaceButton goback =     new InterfaceButton("<< Back",true);
	
	final static String CGunOpen =  "My CGun<<";
	final static String CGunClosed =  "My CGun>>";
	InterfaceButton myCGun =     new InterfaceButton(CGunClosed,true); //will be false by default later when the gun is in the game currectly
	
	InterfaceButton myHome =     new InterfaceButton("My  Home",true);
	InterfaceButton myContents = new InterfaceButton("My  Data",false); //not visible unless we have data (will be removed in favor of temp memory)
	InterfaceButton mySTMemory = new InterfaceButton("My STMem",false); //not visible unless we have data (new temp memory)
	
	InterfaceButton myLinks =    new InterfaceButton("My Links",true);
	InterfaceButton myEmails =   new InterfaceButton("My Emails",true);
	Label backgroundobject;
	ArrayList<InterfaceButton> allLinks = new ArrayList<InterfaceButton>();
	
	//memory popup
	STMemory STMemoryPop = new STMemory();
	
	public ConceptGun ConceptGun = new ConceptGun();
	
	boolean closed = true;	
	
	boolean justopened=false;

	Long lastTime =0l;

	private boolean setup=false;
	private boolean stMemorySetup=false;
	/** should be set to true if the list of useable functions needs refreshing **/
	private boolean needsRepopulating = true;
	
	public boolean isNeedsRepopulating() {
		return needsRepopulating;
	}

	public void setNeedsRepopulating(boolean needsRepopulating) {
		this.needsRepopulating = needsRepopulating;
	}

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
		

		Gdx.app.log(logstag, "myCGun clicked");
		ConceptGun.setVisible(false);				
		ConceptGun.setEnabled(ConceptGun.isVisible());
		
		myCGun.addListener(new ClickListener () {	
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 		 		
		 		{
				Log.info("myCGun clicked _____");
				
				Gdx.app.log(logstag, "myCGun clicked");
				ConceptGun.setVisible(!ConceptGun.isVisible());				
				ConceptGun.setEnabled(ConceptGun.isVisible());
				
				if (ConceptGun.isVisible()){
					myCGun.setText(CGunOpen);
					myCGun.setDownStyle();
				} else {
					myCGun.setText(CGunClosed);
					myCGun.setUpStyle();
				}
				
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
				if (Inventory.currentlyHeld!=null){
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
		
		
		
		mySTMemory.setName(DataObjectDropTarget.DROPSPOTTYPENAME); //sets this label as a drop target
		mySTMemory.setUserObject(this); //tells this label to use the GUIBar to handle drop functions	
		
		//actually we set them all labels as drop targets, so that dragging anywhere will add an item
		myHome.setName(DataObjectDropTarget.DROPSPOTTYPENAME); //sets this label as a drop target
		myHome.setUserObject(this); //tells this label to use the GUIBar to handle drop functions	
		myCGun.setName(DataObjectDropTarget.DROPSPOTTYPENAME); //sets this label as a drop target
		myCGun.setUserObject(this); //tells this label to use the GUIBar to handle drop functions	
		goback.setName(DataObjectDropTarget.DROPSPOTTYPENAME); //sets this label as a drop target
		goback.setUserObject(this); //tells this label to use the GUIBar to handle drop functions	
		
		
		mySTMemory.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				
				if (Inventory.currentlyHeld!=null){
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
		
		
		
		//allLinks.add(myEmails);
		//allLinks.add(myLinks);
		
		allLinks.add(mySTMemory);
		//allLinks.add(myContents);
		allLinks.add(myHome);
		allLinks.add(goback);
		allLinks.add(myCGun);
		
		Gdx.app.log(logstag,"setting up");
		//ME.playersInventory.setVisible(false);

		STMemoryPop.setVisible(false);
		
		
		this.invalidate();
		//populateGUI();
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


	public void setSTMemVisible(boolean visible){

		Gdx.app.log(logstag,"setSTMemVisible set to:"+visible);
		if (mySTMemory.isVisible!=true){
			mySTMemory.isVisible=visible;
			needsRepopulating = true;
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
		
		if (needsRepopulating){
						
		super.clearChildren();
		
		
		
		int totalheight = 0;
		//we need to work out the total height first
		//this is 30 pixels per visible link
		for (InterfaceButton link : allLinks) {
			if (link.isVisible==true){
				totalheight=totalheight+30;
			}
		}
		
		
		this.setPosition(0, 0);//(MainExplorationView.guiStage.getHeight() - totalheight));

		this.setSize(95, MainExplorationView.guiStage.getHeight());
		
		addActor(backgroundobject);		
		addActor(STMemoryPop);
		addActor(ConceptGun); 
		
		ConceptGun.invalidate();
		//ConceptGun.validate();
		
		int starty = (int) MainExplorationView.guiStage.getHeight() - totalheight;
		int y = starty; //(int) MainExplorationView.guiStage.getHeight(); //start at 440 and work our way down the page with each new shortcut

		
		for (InterfaceButton link : allLinks) {		
			
			if (link.isVisible==true){
				
				link.setPosition(5,y);	
				

				Gdx.app.log(logstag,"adding link at:"+y);
				super.addActor(link);
				y=y+30;
			}
			
		}

		backgroundobject.setPosition(0,starty); //(0,y+20);	
		backgroundobject.setSize(95, totalheight);
		
		setupSTMemoryPop();
		needsRepopulating=false;
		}
	//	this.invalidate();
	}
	

	@Override
	public void layout(){
		//super.layout();

		Gdx.app.log(logstag,"laying out guibar");
		
		
		populateGUI();
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
		if (!stMemorySetup){
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
		
	//ME.playersInventory.setVisible(true);
		
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
		stMemorySetup=true;
		
		//super.validate();
	}
	public void setupInventory() {

		if (setup){			
			return;		
		} 
		/*
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
		
		*/
		super.validate();
		setup=true;
		
	}

	public void closeInventory() {

		Gdx.app.log(logstag,"closeInventory");
		
	//	if (!closed ){
			//ME.playersInventory.setVisible(false);
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
		
		Boolean success = STMemoryPop.addItem(Inventory.currentlyHeld);

		//if was successfully added we set currently held to nothing
		if (success){
				Inventory.currentlyHeld = null;
		} else {
			
			//should have some feedback here for STMemory full up
			
		}
		
	}

@Override
public boolean onDrop(DataObject drop) {

	Gdx.app.log(logstag,"on drop triggered");
	
	return STMemoryPop.onDrop(drop);
}

@Override
public void onDrag(DataObject dataObject) {
	
	
}

}
