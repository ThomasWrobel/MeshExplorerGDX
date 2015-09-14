package com.lostagain.nl.GWTish.Management;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class ZIndexGroup extends Array<ZIndexAttribute> {
	
	public String group_id = ""; //the identifier for the group, currently a string
	public int drawOrderPosition  = -1; //the position of the earliest element in the draw order. -1 for unordered.
	
	public static HashMap<String,ZIndexGroup> AllZIndexGroups = new HashMap<String,ZIndexGroup>();

	/**
	 * Returns a existing zIndexGroup is one exists with this name, else makes a new one.
	 */
	static ZIndexGroup getZIndexGroup(String groupname){
		
		ZIndexGroup group = AllZIndexGroups.get(groupname);
		
		if (group!=null){
			return group;
		} else {
			return new ZIndexGroup(groupname);
		}
		
	}
	
	private ZIndexGroup(String groupname) {
		super(true,5); //capacity of 5 by default
		group_id = groupname;
		 AllZIndexGroups.put(groupname, this);
		 
	}

	@Override
	public void add(ZIndexAttribute newZindex) {
		//validate it should be part of this group
		if (newZindex.group!=this){
			Gdx.app.log("ZIndexGroup", "Attempted to add zindex to a different group then it belongs");
			return;
		}
		
		
		 for (int i = 0; i < this.size; i++) {
			 ZIndexAttribute zin = this.get(i);
			 
			 if (newZindex.zIndex<zin.zIndex){
				 super.insert(i, newZindex);
				 return;
			 }
			 
			 
		 }
		
		//if we arnt less then anything we add at the end
		 super.add(newZindex);
		 
	}

	@Override
	public boolean removeValue(ZIndexAttribute value, boolean identity) {
		return super.removeValue(value, identity);
	}
	
	public static void clearAllDrawOrderPositions(){
		for (ZIndexGroup group : AllZIndexGroups.values()) {
			group.drawOrderPosition = -1;
			
		}
	}

	
	
}