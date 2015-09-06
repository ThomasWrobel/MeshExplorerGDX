package com.lostagain.nl.GWTish;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.ComplexPanel.Alignment;
import com.lostagain.nl.GWTish.ComplexPanel.HorizontalAlignment;
import com.lostagain.nl.GWTish.ComplexPanel.VerticalAlignment;
import com.lostagain.nl.me.newmovements.PosRotScale;

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
	/**
	 * 
	 * @param index
	 * @param hideOthers - hide other widgets
	 */
	public void showWidget(int index,boolean hideOthers) {

		Widget selected= contents.get(index);
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

		return new Vector3(getLeftPadding()+newLocationX,getTopPadding()+newLocationY,(5f+10f*index));//widgets are stacked 5 apart vertically
		
		//return new Vector3(cx-(incomingWidth/2),cy-(incomingHeight/2),(5f+10f*index)); //widgets are stacked 5 apart vertically
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
