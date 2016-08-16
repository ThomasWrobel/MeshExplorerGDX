package com.lostagain.nl;

/** GameMode control's mode and params for each.
 * Some global tweaks to the gameplay can be made here.
 * For now its just speed of scans, which varies based on the currentgame mode**/
public enum GameMode {		
	/** production mode turns debug logs off **/
	Production(20),
	/** logs on, normal scan speed **/
	Normal(20),
	/** logs on, speeds up scans (speed controlled in ScanManager)**/
	Developer(60);
	
	int ScanSpeed;
	public static final GameMode currentGameMode = Normal;		
	GameMode(int ScanSpeed){
		this.ScanSpeed=ScanSpeed;
	}
	
	public int getScanSpeed() {
		return ScanSpeed;
	}
	
}