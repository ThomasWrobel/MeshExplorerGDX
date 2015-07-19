package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.darkflame.client.interfaces.GenericProgressMonitor;
import com.lostagain.nl.GWTish.Widget;


/**
 * A progress bar is a widget which grows/shrinks between a min/max size based on a percentage
 * 
 * @author Tom
 *
 */
public class ProgressBar extends Widget implements GenericProgressMonitor {

	final static String logstag = "ME.ProgressBar";

	
	float widgetsHeight = 0f;
	float widgetsCurrentWidth  = 0f; 		

	float valueMin = 0f;
	float valueMax  = 0f; 	
	
	float CurrentValue  = 0f;
	
	float widgetsMinWidth = 0f;		
	float widgetsMaxWidth = 0f; 	
	
	public ProgressBar(float Height,float MinWidth,float MaxWidth){
		super(MinWidth, Height, MODELALIGNMENT.TOPLEFT);
		CurrentValue  = 0; 	

		widgetsMinWidth     = MinWidth;
		widgetsMaxWidth     = MaxWidth;
		widgetsCurrentWidth = MinWidth;
		widgetsHeight       = Height;
		
		valueMin = 0;
		valueMax = 100; 	
		
		
		super.getStyle().setBackgroundColor(Color.RED);
		super.getStyle().setBorderColor(Color.WHITE);
		
	}
	/**
	 * 
	 * @param Height - the height of this widget 
	 * @param MinWidth - the width when value is minimum
	 * @param MaxWidth - the width when value is max
	 * @param MinVal
	 * @param MaxVal
	 * @Param CurrentVal 
	 */
	public ProgressBar(float Height,float MinWidth,float MaxWidth, float MinNumber, float MaxNumber,float CurrentVal){
		super(MaxWidth, Height, MODELALIGNMENT.TOPLEFT);

		widgetsHeight = Height;
		widgetsCurrentWidth  = MaxWidth;
		
		widgetsMinWidth  = MinWidth;
		widgetsMaxWidth  = MaxWidth;
				
		valueMin = MinNumber;
		valueMax = MaxNumber; 	
		
		CurrentValue  = CurrentVal; 	
		
		setCurrentWidth(getCurrentValueAsWidth());
		
		 
		
	}
	
	public void setValue(float CurrentVal){

		CurrentValue  = CurrentVal; 	
		widgetsCurrentWidth = getCurrentValueAsWidth();

		Gdx.app.log(logstag,"widgetsCurrentWidth:"+widgetsCurrentWidth);
		
		
		setCurrentWidth(widgetsCurrentWidth);
	}
	
	
	private void setCurrentWidth(float newWidth) {
		
		this.setSizeAs(newWidth, widgetsHeight);

		Gdx.app.log(logstag,"newWidth:"+newWidth+" widgetsHeight:"+widgetsHeight);
		
		
	}

	private float getCurrentValueAsWidth(){
		
		float valueAsRatio = getValueAsRatio();

		Gdx.app.log(logstag,"valueAsRatio:"+valueAsRatio);
		
		float widthRange = widgetsMaxWidth - widgetsMinWidth;

		Gdx.app.log(logstag,"widthRange:"+widthRange);
		
		float newWidth = widgetsMinWidth + (widthRange * valueAsRatio);
				
		return newWidth;
		
	}

	
	/**
	 * gets the current value as a position between 0 and 1 with 0 representing valueMin and 1 represent valueMax;
	 * @return
	 */
	private float getValueAsRatio(){
		
		float range = valueMax - valueMin;
		float ratio = (CurrentValue-valueMin) / range;
		
		return ratio;
		
	}
	
	/**
	 * Sets the total units this bar represents
	 */
	@Override
	public void setTotalProgressUnits(int i) {
		valueMin=0;
		valueMax=i;
		
	}
	@Override
	public void addToTotalProgressUnits(int i) {
		valueMax=valueMax+i;
		
	}
	@Override
	public void setCurrentProcess(String message) {
		
		
	}
	@Override
	public void stepProgressForward() {
		CurrentValue = CurrentValue + 1;
		
	}
	@Override
	public void setCurrentProgress(int i) {
		CurrentValue =  i;
		
	}
	
}
