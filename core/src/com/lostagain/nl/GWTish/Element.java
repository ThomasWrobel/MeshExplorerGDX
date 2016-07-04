package com.lostagain.nl.GWTish;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.shaders.GwtishWidgetDistanceFieldAttribute;

/**
 * This will approximate a similar function as GWTs Element class does.
 * 
 * It also implements handlers if any are added. This is because the AnimatableModelInstance class its based on does all the heavy lifting
 * making it pointless to really have a FocusPanel class. We might as well let ALL widgets have various handlers as a option.
 * 
 * @author Tom
 *
 */
public class Element extends AnimatableModelInstance {
	
	final static String logstag = "GWTish.Element";
	Style objectsStyle;
	
	public Element(Model model) {
		super(model);
	}
	
	
	/**
	 *  returns the style object which will control a small fraction of
	 *  the functionality that true GWT styles do.
	 *  Specifically this is currently for a few style options on text labels.
	 * @return 
	 */
	public Style getStyle() {
		return objectsStyle;
		
	}


	public void setStyle(Material material) {
		
		objectsStyle=new Style(this,material);
			
		//umm the associated material probablty needs updating to match the material supplied?
		
	}
	
	public void setStyle(Style material) {
		
		objectsStyle=material;
		//umm the associated material probablty needs updating to match the material supplied in style?
		//objectsStyle.objectsMaterial
		
		
	}
	//============
	//------------
	//FocusPanel like functions;
	//------------(might refractor elsewhere at some point)---
	ArrayList<EventHandler> handlers = new ArrayList<EventHandler>();
	
	/**
	 * was hitable manually set? in which case we should not remove it here when theres no handlers
	 * Note; we might have to override setAsHitable to keep track of this
	 */
	boolean hitableManuallySet = false;
	
	public HandlerRegistration addClickHandler(ClickHandler handler) 
	 {
		//if no handlers yet exist ensure we are hitable
		ensureHandlers();
		
		//add new handler
		handlers.add(handler);
				
		//create and return a HandlerRegistration
		return new HandlerRegistration(this,handler);
     }
	
	
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) 
	{
		//if no handlers yet exist ensure we are hitable
		ensureHandlers();
		
		//add new handler
		handlers.add(handler);
				
		//create and return a HandlerRegistration
		return new HandlerRegistration(this,handler);
    }
	
	
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) 
	{
		//if no handlers yet exist ensure we are hitable
		ensureHandlers();		
		//add new handler
		handlers.add(handler);		
		//create and return a HandlerRegistration
		return new HandlerRegistration(this,handler);
   }
	
	//

	private void ensureHandlers() {
		if (handlers.isEmpty()){
			//first remember if hitable was manually set
			//this means we dont remove it if handlers become empty
			hitableManuallySet = this.isHitable();
					
			if (!isHitable()){
				this.setAsHitable(true);				
			}
			
		}
	}
	
	//private atm for consistency with GWT. I am not sure yet if theres any point
	//to their whole handler system. But for the moment we will stay consistent
	//Use the handlerrigstration and "remove" to remove a handler
	void removeHandler(EventHandler handler){
		handlers.remove(handler);
		//recheck if any left
		if (handlers.size()==0 && !hitableManuallySet){ //only remove if hitable wasn't manually set elsewhere
			setAsHitable(false);			
		}
	}
	
	
	protected void fireHandlersForType(Event.EventType eventType) {
		
		if (handlers.isEmpty()){
			Gdx.app.log(logstag, "no handlers set for element");
		}
		
		for (EventHandler handler : handlers) {
			
			//if the type matches fire the event
			if (handler.getType()==eventType){
				 handler.fireHandler();
			}
			
		}
	}
	
	
	//Actual events here;
	//This is where the clicks are received
	private boolean mouseDownOn = false;

	@Override
	public void fireTouchDown() {
		super.fireTouchDown();
		mouseDownOn = true;
		
	}


	@Override
	public void fireTouchUp() {
		super.fireTouchUp();
		
		//if a touchup event fires while it was touched down
		//then this indicates a "click" action happened
		if (mouseDownOn == true){			
			mouseDownOn = false;
			
			fireHandlersForType(Event.EventType.ClickEvent);
			
		}
	
		
	}




	@Override
	public void fireDragStart() {
		super.fireDragStart();
	}


	/**
	 * fired when a layout related style is changed in this objects style object (objectsStyle)
	 * Override this and implement the correct updating if, for example, this widget
	 * should respond to text align changes
	 */
	public void layoutStyleChanged() {
		// Override by sub classes if needed
		
	}
	
	
}
