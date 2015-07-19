package com.lostagain.nl.GWTish;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.GWTish.Widget.MODELALIGNMENT;
import com.lostagain.nl.me.newmovements.PosRotScale;

public abstract class ComplexPanel extends Widget {


	Color DefaultColour = new Color(0.3f,0.3f,1f,0.5f);
	protected ArrayList<Widget> contents = new ArrayList<Widget>();
	/**
	 * 
	 * the currently largest width of any stored element (doesnt yet update when elements removed)
	 */
	protected float largestWidthOfStoredWidgets = 0f;
	/**
	 * the currently largest height of any stored element (doesn't update when elements removed)
	 */
	protected float largestHeightOfStoredWidgets = 0f;


	Runnable updateContainerSize; 

	//--
		//The follow is used for widgets attached to it when we want to specify where they go
		//Note; These may be moved elsewhere as we introduce other classes for widgets-in-widgets
		enum HorizontalAlignment {
			Left,Center,Right
		}
		
		enum VerticalAlignment {
			Top,Middle,Bottom
		}
		
		
		float topPadding    = 0f;
		float bottomPadding = 0f;
		float leftPadding   = 0f;
		float rightPadding  = 0f;
		public void setPadding (float padding){
			topPadding = padding;
			bottomPadding = padding;
			leftPadding = padding;
			rightPadding = padding;
		}
		//--------------------------------------------------------------------
	public ComplexPanel(float sizeX, float sizeY, MODELALIGNMENT align) {
		super(sizeX, sizeY, align);

		//this will be given to child widgets to inform the parent of size changes
		updateContainerSize = new Runnable(){
			@Override
			public void run() {
				//reposition this panels widgets
				repositionWidgets();
			}			
		};

	}
	public ComplexPanel(float sizeX, float sizeY) {
		this(sizeX, sizeY,MODELALIGNMENT.TOPLEFT);
		
	}
	public ComplexPanel(Model object) {
		super(object);
		//this will be given to child widgets to inform the parent of size changes
				updateContainerSize = new Runnable(){
					@Override
					public void run() {
						//reposition this panels widgets
						repositionWidgets();
					}			
				};
		
	}
	public void clear() {

		for (Widget widget : contents) {
			widget.hide();
			widget.removeOnSizeChangeHandler(updateContainerSize);			
			this.removeAttachment(widget);
		}
		contents.clear();
	}
	
	
	/**
	 * 
	 * @return true if there was a change AND its bigger then the minimum size of this widget
	 */
	protected boolean recalculateLargestWidgets() {

		boolean changed=false;

		Gdx.app.log(logstag,"recalculateLargestWidgets");

		for (Widget widget : contents) {

			//get size of widget
			BoundingBox size = widget.getLocalBoundingBox();

			float scaleY = widget.transState.scale.y;
			float scaleX = widget.transState.scale.x;

			float height = size.getHeight() * scaleY;
			float width  = size.getWidth()  * scaleX;

			if (width>largestWidthOfStoredWidgets){
				
				if (width>this.MinSizX){
					//note we only flag as changed if we are exceeding the minimum size
					changed=true;
				}
				
				//update largest
				largestWidthOfStoredWidgets=width;
								
			}
			if (height>largestHeightOfStoredWidgets){
				if (height>this.MinSizY){
					changed=true;
				}
				largestHeightOfStoredWidgets=height;
				
			}

		}



		Gdx.app.log(logstag,"largestWidthOfStoredWidgets:"+largestWidthOfStoredWidgets);
		Gdx.app.log(logstag,"largestHeightOfStoredWidgets:"+largestHeightOfStoredWidgets);

		return changed;

	}
	/**
	 * removes a widget from this panel and hides it.
	 * Note; The widget will still exist if you wish to unhide it, it just wont be attached to this panel anymore
	 * @param widget
	 */
	public void remove(Widget widget) {
		contents.remove(widget);
		widget.hide();
		widget.removeOnSizeChangeHandler(updateContainerSize);
		this.removeAttachment(widget);
		boolean changed = recalculateLargestWidgets();
		//regenerate list
		repositionWidgets(); //we can optimize if we only reposition after the one removed
	}
	@Override
	public void setOpacity(float opacity) {		
		super.setOpacity(opacity);
		//repeat for our attached widgets
		for (Widget widget : contents) {
			widget.setOpacity(opacity);			
		}

	}

	/**
	 * All subtypes must implement the ability to reposition all the widgets correctly.
	 * This will be called if a size of one of the wdigets changes (for example, a new max size might
	 * expanded the overall widget size, and thus mean anything centraly aligned needs to be updated)
	 * internally it should remove all widgets and call internalAdd to ensure the methods stay sinked
	 */
	abstract void repositionWidgets();
	
	
	abstract Vector3 getNextPosition(float width, float height, boolean b,int widgetIndex);
	
	/**
	 * Adds a widget below the current ones.
	 * This class should be extended by subclasses in order to call setSizeAs(w,h) with the correct new total size afterwards)
	 * 
	 * @param widget
	 */
	public void add(Widget widget) {
		
		
		//add to the widget list
		contents.add(widget);
	
		//recalculate biggest widgets (used for centralization vertical or horizontal depending on panel)
		boolean changed = recalculateLargestWidgets();
				
		if (changed){
			repositionWidgets(); //reposition all widgets with the new one			
			return;
		}
		
		//else we just add the new one
		internalAdd(widget);
		
	
	}
	/**
	 * Attaches the widget at the end of the current ones without resizing or adding to lists
	 * @param widget
	 */
	protected void internalAdd(Widget widget) {
		
		//get size of widget
		
		BoundingBox size = widget.getLocalBoundingBox();
		
		float scaleY = widget.transState.scale.y;
		float scaleX = widget.transState.scale.x;
				
		float height = size.getHeight() * scaleY;
		float width  = size.getWidth()  * scaleX;
		
		/*
		if (width>largestWidthOfStoredWidgets){
			largestWidthOfStoredWidgets=width;
		}
		if (height>largestHeightOfStoredWidgets){
			largestHeightOfStoredWidgets=height;
		}*/
		
		//currently set to position on the right hand side
		Vector3 newLoc = getNextPosition(width,height,true,contents.indexOf(widget));
		
		
		float newLocationX = newLoc.x;
		float newLocationY = newLoc.y; //under the last widget
		float newLocationZ = newLoc.z;
				
		Gdx.app.log(logstag,"______________placing new widget at: "+newLocationY+" its height is:"+height);
	
		PosRotScale newLocation = new PosRotScale(newLocationX,-newLocationY,newLocationZ); //hover above for now (3 is currently a bit arbitrary, guess we should make this a option in future)
		
		//set the scale of the newLocation to match the scale of the incoming object too (so its size is preserved
		newLocation.setToScaling(widget.transState.scale);
		
		this.attachThis(widget, newLocation);
	
		//set widget to inherit visibility
		widget.setInheritedVisibility(true);		
		
		//Now we need to register handlers so we can reform stuff if the size of anything inside changes
		widget.addOnSizeChangeHandler(updateContainerSize);
		
	}


}
