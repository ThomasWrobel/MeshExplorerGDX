package com.lostagain.nl.me.gui;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.TimeUtils;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;


/**
 * Will become a class for displaying small information notices to the player.
 * Pops up things for a set time, then fades away.
 * If a new thing gets added, will display it under it and reset the timer till it fades.
 *  * 
 * **/
public class InfoPopUp {

	final static String logstag = "ME.InfoPopUp";

	final static ArrayList<MessageObject> currentMessagess = new ArrayList<MessageObject>();
	final static float fadeInTime = 0.5f;
	final static float PopupDuration = 3; 
	final static float fadeOutTime = 1.5f;

	
	/** messages that are scheduled to be displayed at a certain time **/
	final static ArrayList<MessageObject> futureMessagess = new ArrayList<MessageObject>();
	

	/** how long has the messages been displayed for **/
	static float DisplayedFor = 0; 


	static float opacity = 0;

	enum CurrentState {
		hidden, appearing, displayed, fading

	}

	CurrentState state = CurrentState.hidden;
	

	/**manages the texts layout**/
	GlyphLayout textLayout = new GlyphLayout();
	
	/** A message object determines the look of a single message in the stack **/
	class MessageObject {
		
		String messageText = "";
		Color messageColor = Color.WHITE;
		long sheduledFor = -1;
		
		
		public MessageObject(String messageText, Color messageColor) {
			super();
			this.messageText = messageText;
			this.messageColor = messageColor;
		}
	}
	
	/**
	 * Adds a message to be displayed in the future
	 * @param message
	 */
	public void sheduleMessage(String message, Color color,int delay)
	{
		MessageObject newmessage = new MessageObject(message,color);	
		newmessage.sheduledFor = TimeUtils.millis()+delay;
				
		futureMessagess.add(newmessage);
		
	}

	
	
	
	/**
	 * Adds a new white message to appear in the corner for a short period
	 * 
	 * @param message
	 */
	public void displayMessage(String message)
	{
		displayMessage(message, Color.WHITE);		
	}

	/**
	 * Adds a new message to appear in the corner for a short period
	 * 
	 * @param message
	 * @param color
	 */
	public void displayMessage(String message, Color color)
	{
		MessageObject newmessage = new MessageObject(message,color);	
		displayMessage(newmessage );
		
		//currentMessagess.add(newmessage);
		//DisplayedFor = 0;		
		//state = CurrentState.appearing;
	}
	
	/**
	 * Adds a new message to appear in the corner for a short period
	 * 
	 * @param message
	 * @param color
	 */
	public void displayMessage(MessageObject newmessage )
	{	
		currentMessagess.add(newmessage);
		DisplayedFor = 0;		
		
		//if we arnt already visible we set it to appearing
		if (state != CurrentState.displayed){
			state = CurrentState.appearing;	
		}
	}
	
	/**
	 * 
	 * @param delta - time since last frame;
	 */
	public void update(float delta)
	{

		//check for any timed updated to be added
		Iterator<MessageObject> fmit = futureMessagess.iterator();
		while (fmit.hasNext()) {
			InfoPopUp.MessageObject message = (InfoPopUp.MessageObject) fmit.next();
			//if the current time is beyond the time its sheduled for we add it to be displayed and remove it from this list
			if (TimeUtils.millis()>message.sheduledFor){				
				displayMessage(message);
				fmit.remove();
			}
		}	
		
		
		//advance internal timer
		if (state != CurrentState.hidden){
			DisplayedFor=DisplayedFor+delta;
		}
		
		//do action based on current status
		switch (state) {
		case appearing:

			//if past appearing time we move to the next state
			if (DisplayedFor>fadeInTime){

				state = CurrentState.displayed;				
				opacity = 1;
			} else {

				//if appearing we work out how far we are into it as a ratio of current time to total fadein time
				//this becomes the alpha (1= fully visible, 0 = not at all visible)
				opacity = (DisplayedFor / fadeInTime);


			}

			break;
		case displayed:

			//do nothing till duration is passed
			if (DisplayedFor>(fadeInTime+PopupDuration)){

				state = CurrentState.fading;				

			} 

			break;
		case fading:

			//if past fading time we move to the next state
			if (DisplayedFor>(fadeInTime+PopupDuration+fadeOutTime)){

				state = CurrentState.hidden;				
				//we also clear everything ready for next time
				clearAndReset();

			} else {

				//how long have we been fading out?
				float FadingOutFor = DisplayedFor - (fadeInTime+PopupDuration);

				//if fading we work out how far we are into it as a ratio of current time to total fadeout time
				//we then invert this by subtracting it from 1;
				//this becomes the alpha (1= fully visible, 0 = not at all visible)
				opacity = 1-(FadingOutFor / fadeOutTime);

			}

			break;
			
		case hidden:
			//do nothing
			break;		
		}

		
		
		
		
		//if we are not hidden, we draw the text
		if (state != CurrentState.hidden){
			drawCurrentText();
		}

	}

	//currently just uses sprite batch rather then anything fancy.
	//in future we might want to make this more pretty!
	/** draws the current text on the screen in the corner at the correct opacity **/
	private void drawCurrentText() {

		ME.font.setColor(1, 1, 1, opacity);
		
		int totalmessages = currentMessagess.size();
		float spacing = (ME.font.getCapHeight())+3;
		float startHeight = totalmessages * spacing;
		float stageWidth = MainExplorationView.guiStage.getWidth();
				
		int i =0;
		for (MessageObject messageObj : currentMessagess) {

			i++;
			
			String message = messageObj.messageText;
			Color c = messageObj.messageColor;
			
			ME.font.setColor(c.r, c.g, c.b, c.a* opacity);
			
			textLayout.setText(ME.font,message);
			
			float stringWidth = textLayout.width;//ME.font.getBounds(message).width;
			
			ME.font.draw( ME.interfaceSpriteBatch,textLayout,stageWidth-stringWidth-10, 25+(startHeight-(i*spacing)));

		}		

		ME.font.setColor(1, 1, 1, 1);
	}

	private void clearAndReset(){
		currentMessagess.clear();
		DisplayedFor = 0;
		state = CurrentState.hidden;
		opacity = 0;
	}

}
