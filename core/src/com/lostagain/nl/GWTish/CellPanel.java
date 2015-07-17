package com.lostagain.nl.GWTish;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * A cell panel is the parent of both VerticalPanel and HorizontalPanel
 * it handles a collection of widgets inside itself, laid out in some fashion
 * 
 * @author Tom
 *
 */
public abstract class CellPanel extends Widget {

	Color DefaultColour = new Color(0.3f,0.3f,1f,0.5f);
	protected float spaceing = 0f;
	protected ArrayList<Widget> contents = new ArrayList<Widget>();

	Runnable updateContainerSize; 
	
	/**
	 * 
	 * the currently largest width of any stored element (doesnt yet update when elements removed)
	 */
	float currentLargestWidgetsWidth = 0f;
	/**
	 * the currently largest height of any stored element (doesnt update when elements removed)
	 */
	float currentLargestWidgetsHeight = 0f;
	
	
	public CellPanel(int x, int y) {
		super(x,y);
		
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
	 * Sets the spacing between elements vertically
	 * @param f
	 */
	public void setSpaceing(float f) {
		this.spaceing = f;
		
		repositionWidgets();
		
	}

	abstract void repositionWidgets();

	/**
	 * Adds a widget below the current ones.
	 * This class should be extended by subclasses in order to call setSizeAs(w,h) with the correct new total size afterwards)
	 * 
	 * @param widget
	 */
	public void add(Widget widget) {
		
		//add to the widget list
		contents.add(widget);
		
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
		
		if (width>currentLargestWidgetsWidth){
			currentLargestWidgetsWidth=width;
		}
		if (height>currentLargestWidgetsHeight){
			currentLargestWidgetsHeight=height;
		}
		
		//currently set to position on the right hand side
		Vector2 newLoc = getNextPosition(width,height,true);
		
		float newLocationX = newLoc.x;
		float newLocationY = newLoc.y; //under the last widget
		
		Gdx.app.log(logstag,"______________placing new widget at: "+newLocationY+" its height is:"+height);
	
		PosRotScale newLocation = new PosRotScale(newLocationX,-newLocationY,3); //hover above for now (3 is currently a bit arbitrary, guess we should make this a option in future)
		
		//set the scale of the newLocation to match the scale of the incoming object too (so its size is preserved
		newLocation.setToScaling(widget.transState.scale);
		
		this.attachThis(widget, newLocation);
	
		//set widget to inherit visibility
		widget.setInheritedVisibility(true);		
		
		//Now we need to register handlers so we can reform stuff if the size of anything inside changes
		widget.addOnSizeChangeHandler(updateContainerSize);
		
	}

	abstract Vector2 getNextPosition(float width, float height, boolean assumeNewWidgetWillBeAdded);

	
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


}
