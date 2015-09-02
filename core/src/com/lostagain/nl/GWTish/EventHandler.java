package com.lostagain.nl.GWTish;

import com.lostagain.nl.GWTish.Element.EventType;

abstract class EventHandler {
	abstract EventType getType(); 
	protected abstract void fireHandler(); 
}