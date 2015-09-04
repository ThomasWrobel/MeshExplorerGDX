package com.lostagain.nl.GWTish;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.GWTish.Widget.MODELALIGNMENT;
import com.lostagain.nl.me.newmovements.PosRotScale;


public abstract class ComplexPanel extends Widget {

	final static String logstag = "GWTish.ComplexPanel";
	Color DefaultColour = new Color(0.3f,0.3f,1f,0.5f);
	
	
	/**
	 * the currently largest width of any stored element (doesn't yet update when elements removed)
	 */
	protected float largestWidthOfStoredWidgets = 0f;
	/**
	 * the currently largest height of any stored element (doesn't update when elements removed)
	 */
	protected float largestHeightOfStoredWidgets = 0f;


	
	//Runnable updateContainerSize; 

	//--
		//The follow is used for widgets attached to it when we want to specify where they go
		//Note; These may be moved elsewhere as we introduce other classes for widgets-in-widgets
		public enum HorizontalAlignment {
			Left,Center,Right
		}
		
		public enum VerticalAlignment {
			Top,Middle,Bottom
		}
		
		/**
		 * A class that stores the widget with its horizontal and vertical alignments
		 * @author Tom
		 */
		class Alignment {		
			HorizontalAlignment horizontal = null;
			VerticalAlignment   vert = null;
						
			public Alignment(HorizontalAlignment horizontal,VerticalAlignment vert) {
				
				this.horizontal = horizontal;
				this.vert = vert;
				
			}			
						
		}
	
		/**
		 * the contents of this panel
		 */
		protected ArrayList<Widget> contents = new ArrayList<Widget>();
		
		
		/**
		 * if a widget has a specified alignment, it is stored here
		 */
		protected HashMap<Widget,Alignment> contentAlignments = new HashMap<Widget,Alignment>();
		
		
		
		
		
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

		/*
		//this will be given to child widgets to inform the parent of size changes
		updateContainerSize = new Runnable(){
			@Override
			public void run() {

				Gdx.app.log(logstag,"updating position due to size change");
				boolean changed = recalculateLargestWidgets();

				Gdx.app.log(logstag,"_________vis on panel:"+isVisible()+" ");
				repositionWidgets();
				Gdx.app.log(logstag,"_________vis on panel2:"+isVisible()+" ");
			}			
		};*/

	}
	
	/**
	 * when a child widget resizes, this is fired to ensure the parent widgets
	 * size changes to keep it contained
	 */
	@Override
	protected void onChildResize(){

		Gdx.app.log(logstag,"updating position due to child size change");
		boolean changed = recalculateLargestWidgets();

		Gdx.app.log(logstag,"_________vis on before reposition"+isVisible()+" ");
		repositionWidgets();
		Gdx.app.log(logstag,"_________vis on after reposition:"+isVisible()+" ");

		//update back size
		sizeToFitContents();
	}
	
	public ComplexPanel(float sizeX, float sizeY) {
		this(sizeX, sizeY,MODELALIGNMENT.TOPLEFT);
		
	}
	public ComplexPanel(Model object) {
		super(object);
		//this will be given to child widgets to inform the parent of size changes
		/*
				updateContainerSize = new Runnable(){
					@Override
					public void run() {

						Gdx.app.log(logstag,"updating position due to size change");
						boolean changed = recalculateLargestWidgets();
						
						repositionWidgets();
					}			
				};*/
		
	}
	public void clear() {

		for (Widget widget : contents) {
			
			//Widget widget = widgetdata.widget;
			
			widget.hide();
			//widget.removeOnSizeChangeHandler(updateContainerSize);	
			widget.setParent(null);
			
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

			//Widget widget = widgetdata.widget;
			
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

	


	
	
	@Override
	public void setOpacity(float opacity) {		
		super.setOpacity(opacity);
		//repeat for our attached widgets
		for (Widget widget : contents) {
			
		//	Widget widget = widgetdata.widget;
			
			widget.setOpacity(opacity);			
		}

	}

	/**
	 * This function should size the widget to fit its contents.
	 * This normally is called after a reposition,add or remove and relays upon recalculateLargestWidgets being up to date.
	 * A horizontal panel might implement this by calling;
	 *
	 * this.setSizeAs(leftPadding+currentTotalWidgetWidth+rightPadding,
	 *	     bottomPadding+largestHeightOfStoredWidgets+topPadding);
	 *
	 * as that information will determine its size. A vertical panel will be similar but use the total height instead of width, and the largest widget instead of height
	 *		     
	 */
	abstract void sizeToFitContents();
	
	/**
	 * All subtypes must implement the ability to reposition all the widgets correctly.
	 * This will be called if a size of one of the widgets changes (for example, a new max size might
	 * expanded the overall widget size, and thus mean anything centrally aligned needs to be updated)
	 * internally it should remove all widgets and call internalAdd to ensure the methods stay sinked
	 */
	abstract void repositionWidgets();
	
	
	abstract Vector3 getNextPosition(float width, float height, boolean b,Widget widget);
	
	
	
	public boolean add(Widget widget) {
		
		return insert(widget,contents.size()+1);
		
	}
	
	/**
	 * Adds a widget below the current ones.
	 * This class should be extended by subclasses in order to call setSizeAs(w,h) with the correct new total size afterwards)
	 * 
	 * @param widget
	 * @return true on success, false if not added (ie, was already there)
	 */
	public boolean insert(Widget widget,int beforeIndex) {
		int atIndex = beforeIndex-1;
		
		if (contents.contains(widget)){
			//do nothing as its already contained
			return false;			
		}
		
		//add to the widget list
		contents.add(atIndex,widget);
		
		//recalculate biggest widgets (used for centralization vertical or horizontal depending on panel)
		boolean changed = recalculateLargestWidgets();
		
		if (changed || atIndex!=contents.size()){
			repositionWidgets(); //reposition all widgets with the new one	
			sizeToFitContents();
			return true;
		}
		
		//else we just add the new one
		internalAdd(widget);
		
		//resize
		sizeToFitContents();
		
		return true;
	}
	
	/**
	 * removes a widget from this panel and hides it.
	 * Note; The widget will still exist if you wish to unhide it, it just wont be attached to this panel anymore
	 * 
	 * @param widget
	 * @return false is widget was not found
	 */
	public boolean remove(Widget widget) {
		
		boolean removedSuccessfully = contents.remove(widget);
		
		if (!removedSuccessfully){
			return false;
		}
		contentAlignments.remove(widget);
		
		widget.hide();
		
		widget.setParent(null);
		
		this.removeAttachment(widget);
		boolean changed = recalculateLargestWidgets();
		
		//regenerate list
		repositionWidgets(); //we can optimize if we only reposition after the last one removed
		
		sizeToFitContents();
		
		return true;
	}

	
	//protected void internalAdd(Widget widget) {
//		internalAdd(widget,true);
//	}
	
	/**
	 * Attaches the widget at the end of the current ones without resizing or adding to lists
	 * @param widget
	 */
	protected void internalAdd(Widget widget) {
		
		//get size of widget (unscaled)

		BoundingBox size = widget.getLocalBoundingBox();
		
		
		boolean isAttachedAlready = this.hasAttachment(widget);
		
		if (!isAttachedAlready) {
			
	
			float scaleY = widget.transState.scale.y;
			float scaleX = widget.transState.scale.x;
					
			float height = size.getHeight() * scaleY;
			float width  = size.getWidth()  * scaleX;
			
					
			Vector3 newLoc = getNextPosition(width,height,true,widget); //contents.indexOf(widget)
			
			
			float newLocationX = newLoc.x;
			float newLocationY = newLoc.y; //under the last widget
			float newLocationZ = newLoc.z;
					
			
			PosRotScale newLocation = new PosRotScale(newLocationX,-newLocationY,newLocationZ); 
			
			
			//set the scale of the newLocation to match the scale of the incoming object too (so its size is preserved
			newLocation.setToScaling(widget.transState.scale);
			
			Gdx.app.log(logstag,"______________placing new "+widget.getClass()+" widget at: "+newLocationY+" its scaled size is:"+width+","+height);
		
			attachThis(widget, newLocation);
		
			//set widget to inherit visibility
			widget.setInheritedVisibility(true);		
			
			//tell the widget we are its parent
			widget.setParent(this);
			
		} else {
			//we take the existing displacement and only change the position.
			//This is in order to preserve the scale :)
			PosRotScale currentDisplacement  = this.getAttachmentsPoint(widget);
			
			//get new location (note the widget and height now use the scaling from their existing attachment
			float scaleY = currentDisplacement.scale.x;
			float scaleX = currentDisplacement.scale.y;
					
			float height = size.getHeight() * scaleY;
			float width  = size.getWidth()  * scaleX;
						
			Vector3 newLoc = getNextPosition(width,height,true,widget);
					
			
			float newLocationX = newLoc.x;
			float newLocationY = newLoc.y; //under the last widget
			float newLocationZ = newLoc.z;
					
			
			PosRotScale newLocation = new PosRotScale(newLocationX,-newLocationY,newLocationZ); 
			
			currentDisplacement.setToPosition(newLocation.position);
			
			//just update position without the other gunk
			updateAtachment(widget, currentDisplacement);
			
			
		}
		
		
		
		
	}
	
	
	
	/**
	   * Sets the horizontal alignment of the given widget within its cell and triggers the widgets to be repositioned
	   * 
	   * @param w the widget whose horizontal alignment is to be set
	   * @param align the widget's horizontal alignment
	   */
	  public void setCellHorizontalAlignment(Widget widget, HorizontalAlignment align) {
		  
		  Alignment currentAlignment =contentAlignments.get(widget);
		  
		  if (currentAlignment==null){
			  currentAlignment = new Alignment(align, VerticalAlignment.Middle);
			  contentAlignments.put(widget, currentAlignment);	
			  return;
		  } else {
			  currentAlignment.horizontal = align;
		  }
		  
		  this.repositionWidgets();
		  	
	  }

	  /**
	   * Sets the vertical alignment of the given widget within its cell  and triggers the widgets to be repositioned
	   * 
	   * @param w the widget whose vertical alignment is to be set
	   * @param align the widget's vertical alignment
	   */
	  public void setCellVerticalAlignment(Widget widget, VerticalAlignment align) {
		  
		  Alignment currentAlignment = contentAlignments.get(widget);		  
		  if (currentAlignment==null){
			  currentAlignment = new Alignment(HorizontalAlignment.Center,align);
			  contentAlignments.put(widget, currentAlignment);	
			  return;
		  } else {
			  currentAlignment.vert = align;
		  }
		  
		  this.repositionWidgets();
	  }

	  
	public void setContentAlignments(Widget widget, Alignment contentAlignment) {		
		contentAlignments.put(widget, contentAlignment);		
	}

	
	
	public ArrayList<Widget> getChildren() {
		return new ArrayList<Widget>(contents);
	}
	
	@Override
	/**
	 * sets the index on this and all child objects.
	 * child objects get index+1
	 * @param index  - higher valued objects go infront of lower objects
	 * @param group  - things in the same group get ordered next to eachother according to index value 
	 */
	public void setZIndex(int index, String group) {
		Gdx.app.log(logstag,"_-(setZIndex "+group+" )-_");
		
		//set zindex of back material
		super.setZIndex(index,group);
	
		//but we also need to apply it to all subobjects (only a little higher!)
		for (Widget childwidget : contents) {
			childwidget.setZIndex(index+1,group); 
		}
		
		
	}

}
