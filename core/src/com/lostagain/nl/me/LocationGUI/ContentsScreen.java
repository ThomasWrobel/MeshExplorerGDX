package com.lostagain.nl.me.LocationGUI;

import java.util.ArrayList;


import java.util.Iterator;



import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.me.LocationGUI.ObjectFile.ObjectFileState;

public class ContentsScreen  extends Container<ScrollPane>  implements LocationScreen {
	static Logger Log = Logger.getLogger("ContentsScreen");

	Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	
	int DownloadSpeed = 10;	//will become players connection speed
	ArrayList<ObjectFile> objectsBeingDownloaded = new ArrayList<ObjectFile>();
	
	final Timer	 objectDownloader = new Timer();

	final Task objectDownloadTask;
	private ArrayList<SSSNode> allContentsNodes = new  ArrayList<SSSNode>();
	
	static Boolean isRunning = false;
		
	String contentsTitle = "Contents:";
	Label title  = new Label(contentsTitle,skin);
	
	
	//scroll contents
	final Table scrollTable;
	
	//scroll stuff
	final ScrollPane scroller;
	
	public ContentsScreen(LocationsHub parentLocationContainer, SSSNode securedBy, String contentsTitle) {
		
		
		
		super();

        scrollTable = new Table();
		

        scrollTable.setX(0);
        scrollTable.setY(0);
             
        scroller = new ScrollPane(scrollTable);
        scrollTable.setBackground(DefaultStyles.colors.newDrawable("white",new Color(0,0,1,0.5f)));
        
        
        
        
        scroller.setX(0);
        scroller.setY(0);
    	//scroller.setScrollingDisabled(true, true);
    	scroller.setCancelTouchFocus(false);
        scroller.setDebug(true);
        scroller.setFillParent(true);
        

        super.left();	       
        super.bottom();        
        super.setActor(scroller);
        
        
        
        
		this.contentsTitle = contentsTitle;

		objectDownloadTask = new Task(){

				@Override
				public void run() {
					isRunning=true;
					
					if (objectsBeingDownloaded.size()==0){
	   					objectDownloader.stop();
	   					isRunning=false;
	   					return;
	   				}
					
	   				int SPEEDSTEP = (DownloadSpeed / objectsBeingDownloaded.size())+1; 
	   										
	   				Iterator<ObjectFile> ObjectFilesToUpdate = objectsBeingDownloaded.iterator();
	   				
	   				while (ObjectFilesToUpdate.hasNext()) {
	   					
	   					ObjectFile currentObjectFile = ObjectFilesToUpdate.next();	   										
	   					currentObjectFile.stepForwardDownloadingAmount(SPEEDSTEP);
	   					
	   					if (currentObjectFile.currentMode!=ObjectFileState.Downloading){
	   						ObjectFilesToUpdate.remove();
	   						currentObjectFile.validate();
	   					}
	   					
	   				}
	   				
	   				
	   				if (objectsBeingDownloaded.size()==0){
	   					objectDownloader.stop();
	   					isRunning=false;
	   				}
					
				}
	   			
	   		};
	   		
	   	 
		
		Label title = new Label(contentsTitle,skin);
		//title.setX(150);
	//	title.setY(50);
		scrollTable.add(title).fillX().height(30).expandX();
		//super.addActor(title);
		scrollTable.row();
		
	}

	
	@Override
	public void layout(){
		Log.info("validate contents__");
		super.layout();
		
		//scroller.validate();
		scroller.setHeight(super.getParent().getHeight());
	
		scrollTable.validate();
	}
	
	public void startScanningObjectFile(final ObjectFile ObjectFile){

		ObjectFile.setScanningAmount(0);
	
		objectsBeingDownloaded.add(ObjectFile);
		

		if (!isRunning){
			
			Log.info("starting scanner");			
			if (!objectDownloadTask.isScheduled()){

				Log.info("scheduleTask");		
			  objectDownloader.scheduleTask(objectDownloadTask, 0.1f, 0.1f);
			}
			objectDownloader.start();
			
		}
		
		
	}
	public void addObjectFile(ObjectFile ObjectFile){
		
		//ensure its not already on here
		if (allContentsNodes.contains(ObjectFile.objectsnode)){			
			return;
		};
		
		allContentsNodes.add(ObjectFile.objectsnode);
		scrollTable.left();
		scrollTable.top();
		
		
		scrollTable.add(ObjectFile).fillX().expandX(); //height(30)
		ObjectFile.validate();
		
		scrollTable.row();

		invalidate();
	}
	
	public void addObjectFile(SSSNode sssNode) {
		Gdx.app.log("", "adding node:"+sssNode.PURI);
		
		//make a ObjectFile from this sssnode rk) 
		ObjectFile newObjectFile = new ObjectFile(sssNode,this);		
		addObjectFile(newObjectFile);
		
		invalidate();
	
	}

	public void removeAllContents() {
		
		scrollTable.clearChildren();
		allContentsNodes.clear();
		
		//read add title at top
		scrollTable.add(title).fillX().height(30).expandX();
		scrollTable.row();
		invalidate();
	}
	
	

}
