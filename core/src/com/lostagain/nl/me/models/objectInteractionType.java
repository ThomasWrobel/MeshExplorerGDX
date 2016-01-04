package com.lostagain.nl.me.models;

/**
 * Used so a object can specify how its interacted with 
 * @author Tom
 *
 */
public enum objectInteractionType {
	/** clicks and firings interact with this object **/
	Normal,
	/** interactions happen with this object and it blocks what's under it  **/
	Blocker,
	/** this object is interface only, and the gun wont fire at it **/
	Interface
	
}