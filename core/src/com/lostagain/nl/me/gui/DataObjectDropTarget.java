package com.lostagain.nl.me.gui;

import com.lostagain.nl.me.objects.DataObject;

public interface DataObjectDropTarget  {

	public static final String DROPSPOTTYPENAME = "DataObjectDropSpot";
	
	public boolean onDrop(DataObject drop);
	public void onDrag(DataObject dataObject);
	
	
	
}
