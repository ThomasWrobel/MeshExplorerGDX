package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.ComplexPanel.Alignment;
import com.lostagain.nl.GWTish.ComplexPanel.HorizontalAlignment;
import com.lostagain.nl.GWTish.ComplexPanel.VerticalAlignment;
import com.lostagain.nl.GWTish.Widget.MODELALIGNMENT;

/**
 * A deck panel is a stack of other panels ontop of eachother
 * You can choose to either display one at a time (like GWTs) or multiple at once.
 * 
 * 
 * @author Tom
 *
 */
public class DeckPanel extends ComplexPanel {

	public DeckPanel(float sizeX, float sizeY) {
		super(sizeX, sizeY);
		// TODO Auto-generated constructor stub
	}

	//@Override
	//public void add(Widget widget) {
	//	super.add(widget);
	//resize
	//	sizeToFitContents();
	//}

	public DeckPanel(float sizeX, float sizeY, MODELALIGNMENT align) {
		super(sizeX, sizeY, align);
		
	}

	/**
	 * even though we have all the widgets ontop of eachother we need to reposition them if our size changes in order
	 * to keep them centralized
	 */
	@Override	
	void repositionWidgets() {


		//simply clear and re-add them all

		//reset  stats

		//currentTotalWidgetWidth = 0f;
		//largestWidthOfStoredWidgets = 0f;
		//largestHeightOfStoredWidgets = 0f;


		for (Widget widget : contents) {	

			//super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add

		}

		sizeToFitContents();
		/*
		float cy  = largestHeightOfStoredWidgets/2;

		if (largestHeightOfStoredWidgets < this.MinSizY){
			cy  = MinSizY/2;
		}

		float cx  = largestWidthOfStoredWidgets/2;

		if (largestWidthOfStoredWidgets < this.MinSizX){
			cx  = MinSizX/2;
		}


		for (Widget widget : contents) {



			this.updateAtachment(widget,new PosRotScale(cx ,cy,3f));
		}*/

	}

	public void showWidget(int index) {
		showWidget(index,true);
	}
	public void showWidget(Widget selected) {
		showWidget(selected,true);
	}

	/** 
	 * @param index
	 * @param hideOthers - hide other widgets
	 */
	public void showWidget(int index,boolean hideOthers) {

		Widget selected = contents.get(index);
		showWidget(selected,hideOthers);
	}

	public void showWidget(Widget selected,boolean hideOthers) {
		if (selected==null || !contents.contains(selected)){
			Gdx.app.log(logstag,"_widget not on panel, cant show/hide");			
			return;			
		}

		selected.show();

		if (hideOthers){
			for (Widget widget : contents) {
				if (widget!=selected){
					widget.hide();
				}

			}
		}
	}


	@Override
	Vector3 getNextPosition(float incomingWidth, float incomingHeight, boolean b,Widget widget) {
		int index = contents.indexOf(widget);

		/*float cy  = largestHeightOfStoredWidgets/2;

		if (largestHeightOfStoredWidgets < this.MinSizY){
			cy  = MinSizY/2;
		}

		float cx  = largestWidthOfStoredWidgets/2;

		if (largestWidthOfStoredWidgets < this.MinSizX){
			cx  = MinSizX/2;
		}*/

		//get max height
		float maxH = (largestHeightOfStoredWidgets);
		//ensure its at least min height
		if (maxH<MinSizY){
			maxH=MinSizY;
		}

		//get max width
		// note; maxW/h could be worked out after the largestWidthOfStoredWidgets is.
		// its wasteful to work it out each time here
		float maxW = (largestWidthOfStoredWidgets);
		// or minimum size if smaller
		if (maxW < MinSizX) {
			maxW = MinSizX;
		}
		// -----------------------


		//get alignment of widget
		Alignment align = contentAlignments.get(widget);
		if (align == null) {
			align = new Alignment(HorizontalAlignment.Left,
					defaultVerticalAlignment);
			contentAlignments.put(widget, align);
		}


		float newLocationX = 0;
		float newLocationY = 0;

		//get Y location based on alignment
		switch (align.vert) {
		case Bottom:
			newLocationY = (maxH - incomingHeight);
			break;
		case Middle:
			newLocationY =  (maxH - incomingHeight)/2; //center in panel
			break;
		case Top:
			newLocationY = 0;
			break;
		default:
			newLocationY =  (maxH - incomingHeight)/2; //center in panel
			break;

		}
		//get x location based on alignment
		switch (align.horizontal) {
		case Left:
			newLocationX = 0;
			break;
		case Right:
			newLocationX = (maxW - incomingWidth);
			break;
			// default and center are the same
		case Center:
		default:
			newLocationX = (maxW - incomingWidth) / 2;

		}

		return new Vector3(getLeftPadding()+newLocationX,getTopPadding()+newLocationY,(1f+index));//widgets used to be  stacked 5 apart vertically  (5f+10f*index)

		//return new Vector3(cx-(incomingWidth/2),cy-(incomingHeight/2),(5f+10f*index)); //widgets are stacked 5 apart vertically
	}


	//Override to give alternative to normal zindex setting
	@Override
	/**
	 * sets the index on this and all child objects.
	 * child objects get index+1, unless we are in a DeckPanel subclass, in which case
	 * they should get index+n where n increases based on their position in the deck.
	 * 
	 * @param index  - higher valued objects go infront of lower objects
	 * @param group  - things in the same group get ordered next to eachother according to index value 
	 */
	public void setZIndex(int index, String group) {
		Gdx.app.log(logstag,"_-(setZIndex "+group+","+index+" )-_");

		//set zindex of back material
		getStyle().setZIndex(index,group);

		//but we also need to apply it to all subobjects (only a little higher!)
		int n=1;
		for (Widget childwidget : contents) {

			Gdx.app.log(logstag,"_-( child now; "+group+","+(index+n)+" )-_");			
			childwidget.setZIndex(index+n,group); 
			n++;
		}				
	}


	private VerticalAlignment defaultVerticalAlignment = VerticalAlignment.Middle;

	/**
	 * Sets the default horizontal alignment to be used for widgets added to this
	 * panel. It only applies to widgets added after this property is set.
	 * 
	 */
	public void setHorizontalAlignment(VerticalAlignment align) {
		defaultVerticalAlignment = align;
	}


	@Override
	void sizeToFitContents() {
		this.setSizeAs(getLeftPadding()+largestWidthOfStoredWidgets+getRightPadding(),getBottomPadding()+this.largestHeightOfStoredWidgets+getTopPadding());

	}

}
