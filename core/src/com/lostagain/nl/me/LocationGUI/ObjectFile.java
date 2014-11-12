package com.lostagain.nl.me.LocationGUI;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.ME;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.gui.DataObjectSlot;
import com.lostagain.nl.me.objects.DataObject;


/** An object filelink represents a single object node that can be downloaded **/
public class ObjectFile extends WidgetGroup {

	ProgressBar downloadPercentage;

	final static String logstag = "ME.ObjectFile";
	
	Label ObjectsLabel = new Label ("--->",DefaultStyles.linkstyle);

	final static String DOWNLOAD = " DOWNLOAD ";
	final static String DOWNLOADING = " DOWNLOADING ";
	
	final static String ALREADYHAVE = "Already In Storage. Take Copy: ";
	
	int PercentageScanned = 0;	
	
	//the current parent panel this is being viewed on.
	//(this determines where to send the clicks to start downloading,
	//as scanning is controlled by the parent panel)	
	ContentsScreen currentParent = null;
	
		
	enum ObjectFileState {
		Normal,Downloading,AlreadyHave;
	}

	ObjectFileState currentMode =  ObjectFileState.Normal;
	
	SSSNode objectsnode;
	DataObjectSlot objectstore = new DataObjectSlot();

	private float prefWidth=0;
	private float prefHeight=0;
	
	
	
	public ObjectFile(SSSNode object,ContentsScreen newContentsPage) {
		objectsnode=object;		
		downloadPercentage = new ProgressBar(0, 100, 1, false, DefaultStyles.linkstyle);
		
						
		setup(object, newContentsPage);	
	}

	private void setup(SSSNode object,ContentsScreen newContentsPage) {

		//super.setFillParent(true);
		super.setDebug(true,true);
		
		currentParent = newContentsPage;
		
		super.setWidth(currentParent.getWidth());
		super.setHeight(DataObject.getStandardHeight()+3);
		setPrefHeight(DataObject.getStandardHeight()+3);
		//setPrefWidth(DataObject.getStandardHeight()+3);
		
		

		
		super.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				if (currentMode == ObjectFileState.Normal){
					
					
					//remove download button and change mode to downloading
					download();
					
				}
			}
						
		});

		ObjectsLabel.setX(10);
		downloadPercentage.setX(0);;
	
		ObjectsLabel.setFillParent(true);
		//ObjectsLabel.setWidth(44);		
		downloadPercentage.setFillParent(true);
		
		super.addActor(downloadPercentage);
		super.addActor(ObjectsLabel);
		
		objectstore.setX(super.getWidth()-100);//temp
		super.addActor(objectstore);
		DataObject newobject = new DataObject(this.objectsnode);
		objectstore.onDrop(newobject);
		objectstore.lock();
		objectstore.setSlotEnabled(false);
		
		//check if user already has object		
		Boolean playerHas = PlayersData.playerHas(objectsnode);
		
		if (playerHas){
			//super.add(AbsoluteLayout.at(downloadButton, 50, 5));
			setStateAlreadyHave();
			
		} else 
			
		if (currentMode == ObjectFileState.Normal){
			
			ObjectsLabel.setText("  "+objectsnode.getPLabel());
			
			//if its an ability we give it special styling
			if (objectsnode.isOrHasParentClass(StaticSSSNodes.ability.getPURI())){

				ObjectsLabel.setText("  ~~"+objectsnode.getPLabel()+"~~");
			
				ObjectsLabel.setColor(DefaultStyles.SpecialDownloadLabel);
				
			}
			
		}
		
		
		
	}


	private void setPrefHeight(int i) {
		prefHeight = i;
		
	}
	
	@Override
	public float getPrefHeight(){
		return prefHeight;		
	}
	
	private void setPrefWidth(int i) {
		prefWidth = i;
		
	}
	
	@Override
	public float getPrefWidth(){
		return prefWidth;		
	}

	@Override
	public void validate() {
		super.validate();
		/*
		if (this.getParent()!=null){
		super.setWidth(this.getParent().getWidth());
		
		}
		super.setHeight(DataObject.getStandardHeight());

		//Gdx.app.log(logstag,"super lab width="+super.getWidth());
		//Gdx.app.log(logstag,"super lab height="+super.getHeight());
		
		downloadPercentage.validate();		
		ObjectsLabel.validate();*/
	}
	
	@Override
	public void layout(){
		
		if (currentParent!=null){
			
	//	super.setWidth(this.getParent().getWidth());
	//	super.setHeight(DataObject.getStandardHeight());

		objectstore.setX(currentParent.getWidth()-objectstore.getWidth()-20);
		
		
		downloadPercentage.setWidth(currentParent.getWidth()-objectstore.getWidth()-20);
		
		Gdx.app.log(logstag,"currentParent.getWidth()="+currentParent.getWidth());
		
		}

		
	}
	
	private void download(){
		
		currentMode = ObjectFileState.Downloading;
		
		
		
		 setScanningStyle();
		 
	//	ProgressBar.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(255, 250,50, 55))));


		currentParent.startScanningObjectFile(this);
		


	}
	
	private void setScanningStyle(){

		ObjectsLabel.setText(DOWNLOADING);
		ObjectsLabel.setColor(200, 200, 200, 50);
		
		
	}
	
	
	
	

	//used to indicate the link is being scanned
	//speed is based on Node timed security / scanner speed
	public void setScanningAmount(int Percentage){
		
		//float pixals = (super.size().width()/100)*Percentage;
		
		downloadPercentage.setValue(Percentage);
		
		
		//crude, we should really adjust size rather then re-adding it
		//super.add(AbsoluteLayout.at(ProgressBar, 0, 0,pixals,20));
		//have to re-add this one to make sure its on top *(dunno how to set z-index)
		//super.add(AbsoluteLayout.at(downloadButton, 50, 0));	
		
		
	}
	//node is locked by Security (only known after a scan)
	public void setLocked(){

	}
	//node is unlocked and can be accessed
	public void setOpen(){

	}
	public void setEnable(){
	//	downloadButton.setEnabled(true);

	}

	public void setDisable(){
		//downloadButton.setEnabled(false);

	}

	
	public void stepForwardDownloadingAmount(int SPEEDSTEP) {
		
		PercentageScanned = PercentageScanned + SPEEDSTEP;
		
		if (PercentageScanned>=100) {
			PercentageScanned = 100;

			setScanningAmount(PercentageScanned);
			downloadComplete();
			
		} else {
		
			setScanningAmount(PercentageScanned);
		}
	}
	
	private void setStateAlreadyHave(){
		
		//grey out color
	//	super.setStyles(Style.BACKGROUND.is(Background.solid(Color.argb(55, 55,55, 55))));
		
		downloadPercentage.setVisible(false);
		
		currentMode = ObjectFileState.AlreadyHave;
		
		ObjectsLabel.setText(ALREADYHAVE + "");
		ObjectsLabel.setColor(0, 50, 0, 50);
		//ObjectsLabel.validate();

		Gdx.app.log(logstag,"=========================triggering layout=");
		
		//ensure width is correct (kludge)
		//for some reason getParent doesnt work at this point so we use the stored parent...no clue why
		if (currentParent!=null){
				super.setWidth(currentParent.getWidth());
			Gdx.app.log(logstag,"super lab width="+currentParent.getWidth());
			Gdx.app.log(logstag,"super lab width="+super.getWidth());
		} else {
				Gdx.app.log(logstag,"=========================no parent=");
		}
			//super.setHeight(50);

			//objectstore.setX(currentParent.getWidth()-100);
		//add object
	
		objectstore.unlock();
		super.invalidate();
		
		//newobject.setScale(0.5f);		

	//	Gdx.app.log(logstag,"ObjectsLabel.getWidth()="+ObjectsLabel.getWidth());
		//Gdx.app.log(logstag,"super.getWidth()"+super.getWidth());
		//Gdx.app.log(logstag,"super.getPrefWidth()"+super.getPrefWidth());		
	//	Gdx.app.log(logstag,"newobject.getWidth()="+newobject.getWidth());

		//newobject.setPosition(currentParent.getWidth()-(newobject.getWidth()/2), 0); //right align to end of label (label should not be so big as to overlap
		
		//super.addActor(newobject);
		
		
	}

	private void downloadComplete() {

		 setStateAlreadyHave();
		 
		//add it to the users machine
		ME.playersInventory.addItem(new DataObject(objectsnode));
		
		
	}
}
