package com.lostagain.nl.GWTish;

import com.lostagain.nl.GWTish.Element.EventType;

public abstract class MouseUpHandler extends EventHandler {
	/** called when the element with this handler added is pressed **/
	public abstract void onMouseUp();
	
	EventType getType(){
		return EventType.MouseUpEvent;
	}
	protected void fireHandler(){
		onMouseUp();
	}
}