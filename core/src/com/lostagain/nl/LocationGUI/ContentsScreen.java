package com.lostagain.nl.LocationGUI;

import java.util.ArrayList;


import java.util.Iterator;



import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.LocationGUI.ObjectFile.ObjectFileState;

public class ContentsScreen extends Table implements LocationScreen {


	static Logger Log = Logger.getLogger("LinksScreen");
	int DownloadSpeed = 10;	
	ArrayList<ObjectFile> objectsBeingDownloaded = new ArrayList<ObjectFile>();
	
	final Timer	 objectDownloader = new Timer();

	final Task objectDownloadTask;
	
	static Boolean isRunning = false;
		
	public ContentsScreen(LocationContainer parentLocationContainer, SSSNode securedBy) {
		super();
		super.setFillParent(true);
		

		objectDownloadTask = new Task(){

				@Override
				public void run() {
					isRunning=true;
					
					if (objectsBeingDownloaded.size()==0){
	   					objectDownloader.stop();
	   					isRunning=false;
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
	   		
	   	 
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		Label title = new Label("Contents:",skin);
		title.setX(150);
		title.setY(50);
		
		super.addActor(title);
		
		
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
		
		super.left();
		super.top();
		super.add(ObjectFile).fillX().height(30).expandX();
		ObjectFile.validate();
		
		super.row();
		
	}
	
	public void addObjectFile(SSSNode sssNode) {
		Gdx.app.log("", "adding node:"+sssNode.PURI);
		
		//make a ObjectFile from this sssnode (which should identify a pc on the games network) 
		ObjectFile newObjectFile = new ObjectFile(sssNode,this);
		addObjectFile(newObjectFile);
	}

	public void removeAllContents() {
		
		super.clearChildren();
		
	}
	
	

}
