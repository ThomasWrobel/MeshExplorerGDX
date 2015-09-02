package com.lostagain.nl.GWTish;

import com.lostagain.nl.GWTish.Element.EventType;

public abstract class MouseDownHandler extends EventHandler {
	/** called when the element with this handler added is pressed **/
	public abstract void onMouseDown();
	
	EventType getType(){
		return EventType.MouseDownEvent;
	}
	
	protected void fireHandler(){
		onMouseDown();
	}
}