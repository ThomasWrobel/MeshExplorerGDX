package com.lostagain.nl.me.gui;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.darkflame.client.interfaces.GenericProgressMonitor;

/**
 * 
 * Scan manager managers timings for all current link or object scans
 * 
 * @author Tom
 *
 */
public class ScanManager {
	final static String logstag = "ME.ScanManager";
	
	/**  how many scans are allowed
	 *  This value might change as the player unlocks ability's ***/ 
	static int maxSimultaniousScans = 3;
	
	/** speed total (divided by number of active scans to determine speed) 
	 * This value might change as the player unlocks ability's ***/
	static float maxSpeed = 20f; //should be tweaked for finnal game. Developers probably need this faster for testing!
		
	/** the current active scans **/
	static ArrayList<Scan> activeScans = new ArrayList<Scan>();
	
	/**class that defines a single scan currently on going
	*in knows what widget it should update and how far along it is**/
	static class Scan {
	
		//How far into the scan
		private float scanPercentage = 0;
		
		//The widget we are updating. 
		private GenericProgressMonitor progressWidget;
		
		//scan finished (public so we can check when to remove this scan)
		public boolean scanComplete = false;
		
		public Scan(GenericProgressMonitor progressWidget){
			this.progressWidget = progressWidget;
		}
		
		/**
		 *  We add this value to the current scan percentage.
		 *  As all scans have the same speed at the same time, this change is worked out in scan manager
		 *  not on a individual scan base
		 *  
		 * @param changeInPosition
		 */
		public void update(float changeInPosition){
			//we get the new position
			scanPercentage = scanPercentage + changeInPosition;			
			
			progressWidget.setCurrentProgress((int)Math.floor(scanPercentage));
			
			
			if (scanPercentage>=100){
				scanComplete = true;
			}
		}
		
	}
	
	/**
	 * Adds and starts a new scan
	 * scan speed is proportional to the number being scanned
	 * 
	 * @param widgetBeingScanned
	 * 
	 * @return - boolean if successfully added.
	 */
	static public boolean addNewScan(GenericProgressMonitor widgetBeingScanned){
		
		//check the player is allowed to add a new scan	
		if (activeScans.size()>=maxSimultaniousScans){
			fireToManyActiveScansWarning();			
			return false;
		}
		
		//create a new scan object
		Scan newScan = new Scan(widgetBeingScanned);
		
		//add scan to list
		activeScans.add(newScan);
		
		//fire a GUI indication of new scan started
		fireNewScanStartedMessage();
		
		return true;
	}
	
	
	/**
	 * Updates progress on all active scans, which in turn triggers their visual appearance to change.
	 * This should be fired in the games render cycle
	 * ie. 	public void render(float delta) {  
	 * 
	 * @param delta - time in seconds since last render
	 */
	public static void update(float delta){
		//exit if no scans
		if (activeScans.size()==0){
			return;
		}
		
		//get current speed. This is total speed over how many things are being scanned at once
		float speed = maxSpeed / activeScans.size();
		
		//We multiply by delta to work out the change since last update
		//delta is the time in seconds since last frame. This lets progress seem smooth despite framerate varying
		float change = speed * delta;
		
		//Gdx.app.log(logstag, " change in scan =  "+change);
		
		//loop over all active scans updating them
		Iterator<Scan> scanIterator = activeScans.iterator();
		
		while(scanIterator.hasNext()) {
			
			Scan scanToUpdate = scanIterator.next();					
			scanToUpdate.update(change);
			
			if (scanToUpdate.scanComplete){
				
				fireScanCompleteMessage();				
				scanIterator.remove(); //removes the last Scan the Iterator got safely. (we shouldnt remove directly from the list while we are looping over it as it would get its place mixed up!)
			
			}
			
		}
		
		if (activeScans.size()==0){
			fireAllScansCompleteMessage();
		}
		
		
	}
	
	/**
	 * In future this should trigger a GUI message that all scans have finished
	 */
	private static void fireAllScansCompleteMessage() {
		Gdx.app.log(logstag, " All Scans Complete  ");
	}

	/**
	 * In future this should trigger a GUI message that a scan has finished
	 */
	private static void fireScanCompleteMessage() {
		
		Gdx.app.log(logstag, " Scans Complete  ");
		
	}

	/**
	 * In future this should trigger a GUI message that a new scan has started
	 */
	private static void fireNewScanStartedMessage() {

		Gdx.app.log(logstag, " New Scan Started total scans =  "+activeScans.size());
	}

	/**
	 * In future this should trigger a GUI warning message that you have exceeded your max simultaneous scans
	 */
	private static void fireToManyActiveScansWarning() {
		Gdx.app.log(logstag, " To Many Simultanious Scans To Start Another ("+activeScans.size()+" of "+maxSimultaniousScans+")");
				
	}
	
	

}
