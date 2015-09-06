package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class VerticalPanel extends CellPanel {

	// current stats
	float currentTotalWidgetHeight = 0f;

	HorizontalAlignment DefaultAlignmentinCell = HorizontalAlignment.Center;

	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public VerticalPanel() {

		super(10, 10); // default size and background

	}

	Vector3 getNextPosition(float incomingWidth, float incomingHeight,
			boolean updateHeight, Widget widget) {

		int index = contents.indexOf(widget);
		
		Alignment align = contentAlignments.get(widget);
		if (align == null) {
			align = new Alignment(defaultHorizontalAlignment,
					VerticalAlignment.Middle);
			contentAlignments.put(widget, align);
		}

		float newLocationX = 0;

		// note; maxW could be worked out after the largestWidthOfStoredWidgets is.
		// its wasteful to work it out each time here
		float maxW = (largestWidthOfStoredWidgets);
		// or minimum size if smaller
		if (maxW < this.MinSizX) {
			maxW = this.MinSizX;
		}
		// -----------------------

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

		float newLocationY = currentTotalWidgetHeight; // under the last widget

		if (updateHeight) {
			currentTotalWidgetHeight = currentTotalWidgetHeight
					+ incomingHeight + spaceing; // should spacing be scaled?
		}

		Gdx.app.log(logstag, index + " adding incomingHeight: "
				+ incomingHeight + " total=" + currentTotalWidgetHeight);

		return new Vector3(getLeftPadding() + newLocationX, getTopPadding()
				+ newLocationY, 3f);

	}

	/**
	 * Refreshes the position of all widgets recalculateLargestWidgets(); should
	 * be run first
	 */
	void repositionWidgets() {
		Gdx.app.log(logstag, "Reposition " + contents.size() + " widgets in ");
		// simply clear and re-add them all

		// reset stats
		currentTotalWidgetHeight = 0f;
		// largestWidthOfStoredWidgets = 0f;
		// largestHeightOfStoredWidgets = 0f;

		for (Widget widget : contents) {
			// super.removeAttachment(widget); //remove
			internalAdd(widget); // re add only when needed

		}

		Gdx.app.log(logstag, "new size:" + largestWidthOfStoredWidgets + ","
				+ currentTotalWidgetHeight);

	}
	
	 private HorizontalAlignment defaultHorizontalAlignment = HorizontalAlignment.Center;
	 
	  /**
	   * Sets the default horizontal alignment to be used for widgets added to this
	   * panel. It only applies to widgets added after this property is set.
	   * 
	   **/
	  public void setHorizontalAlignment(HorizontalAlignment align) {
		  defaultHorizontalAlignment = align;
	  }


	@Override
	void sizeToFitContents() {
		setSizeAs(getLeftPadding() + largestWidthOfStoredWidgets + getRightPadding(),
				getBottomPadding() + currentTotalWidgetHeight + getTopPadding());
	}

}
