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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.LocationGUI.Link.LinkMode;
import com.lostagain.nl.LocationGUI.ObjectFile.ObjectFileState;

public class LinksScreen extends Table implements LocationScreen {

	static Logger Log = Logger.getLogger("LinksScreen");
	
		

	Label title = new Label("Links:",DefaultStyles.linkstyle);

	
	/** scan updates every 0.1 seconds. Normally takes 100steps (so 0.1*100 = 10secs)
	 * but (ScanSpeed/ number of current scans) gets subtracted per step**/
	int scanSpeed = 10;	
	
	
	
	ArrayList<Link> linksBeingScanned= new ArrayList<Link>();
	
	final Timer	 linkDownloader = new Timer();
	final Task linkDownloadTask;



	LocationContainer parentLocationContainer;
	
	
	
	static Boolean isDownloading = false;

	public LinksScreen(LocationContainer parentLocationContainer, SSSNode securedBy) {
		super();
		super.setFillParent(true);
		this.parentLocationContainer=parentLocationContainer;
		super.setDebug(true, true);
		
		//Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		linkDownloadTask = new Task() {

				@Override
				public void run() {
					isDownloading=true;
					
	   				if (linksBeingScanned.size()==0){
	   					linkDownloadTask.cancel();
	   					isDownloading=false;
	   					return;
	   				}
	   				
					Log.info("updating bars");					
	   				int SPEEDSTEP = (scanSpeed / linksBeingScanned.size())+1; 
	   										
	   				Iterator<Link> ObjectFilesToUpdate = linksBeingScanned.iterator();
	   					   				
	   				while (ObjectFilesToUpdate.hasNext()) {
	   					
	   					Link currentObjectFile = ObjectFilesToUpdate.next();	   
	   					
	   					currentObjectFile.stepForwardDownloadingAmount(SPEEDSTEP);
	   					
	   					if (currentObjectFile.currentMode!=LinkMode.Scanning){
	   						ObjectFilesToUpdate.remove();
	   						currentObjectFile.validate();
	   					}
	   					
	   				}
	   				

	   				if (linksBeingScanned.size()==0){
	   					linkDownloadTask.cancel();
	   					isDownloading=false;
	   					return;
	   				}
	   				
					
				}
	   			
	   		};
	   	 
		
	//	super.add(title);
		
		
	}

	
	public void addLink(SSSNode sssNode) {
		
	//	Label linkLabel = new Label(sssNode.getPLabel(),DefaultStyles.linkstyle);	
	//	linkLabel.setHeight(20);
		
		
		Link newlink = new Link(sssNode,this);			
	//	newlink.setHeight(30);
		super.left();
		super.top();
		super.add(newlink).fillX().height(30).expandX();// expandX().
		newlink.validate();
		super.row();
		
	}

	public void startScanningLink(final Link ObjectFile){
		Log.info("startScanningLink");
		
		ObjectFile.setScanningAmount(0);
	
		boolean wasadded = linksBeingScanned.add(ObjectFile);
		
		
		
		if (!isDownloading && wasadded){
			
			Log.info("starting scanner");			
			if (!linkDownloadTask.isScheduled()){

				Log.info("scheduleTask");		
			  linkDownloader.scheduleTask(linkDownloadTask, 0.1f, 0.1f);
			}
			linkDownloader.start();
			
		}
		
	}


	public void removeAllContents() {
		
		super.clearChildren();
		
	}


	public void clearLinks() {
		 removeAllContents() ;
		
	}
	
	

}
