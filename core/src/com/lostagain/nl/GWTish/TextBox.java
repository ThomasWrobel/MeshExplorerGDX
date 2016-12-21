package com.lostagain.nl.GWTish;

import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;


/**
 * A standard single-line input text box.
 ***/
public class TextBox extends Label implements InputProcessor {
	final static String logstag = "ME.TextBox";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag
	
	
	//static demand a input multiplexer
	static InputMultiplexer inputMultiplexer;
	/**
	 * Required to be set before any input boxs will work 
	 * @param inputMultiplexer
	 */
	static public void setInputMultiplexer(InputMultiplexer inputMultiplexer) {
		TextBox.inputMultiplexer = inputMultiplexer;
	}

	
	/**
	 * NOTE; input boxs wont receive key events till you set a input multiplexer
	 * TextBox.setInputMultiplexer()
	 * This only needs to be done once. All Input boxes will then add themselves too it automatically
	 * 
	 * TextBoxs have a default border and backstyle to show focus, you can either extend and override, or use Blur and Focus handlers to set your own.
	 * 
	 * @param defaulttext
	 */
	public TextBox(String defaulttext) {
		super(defaulttext);
		super.setAsHitable(true);
		
		if (inputMultiplexer==null){
			Log.severe("attempted to use TextBox but no inputMultiplexer has been set. Use TextBox.setInputMultiplexer(..)  to set one");
			return;
		}

		this.getStyle().setBackgroundColor(Color.DARK_GRAY);
        this.getStyle().setBorderColor(Color.LIGHT_GRAY);
        
        
		
	}

	//only accept keyboard when focused
	@Override
	protected void onFocus() {
		super.onFocus();
		inputMultiplexer.addProcessor(this);

		
		this.getStyle().setBackgroundColor(Color.GRAY);
		
	}

	@Override
	protected void onBlur() {
		super.onBlur();
		inputMultiplexer.removeProcessor(this);

		this.getStyle().setBackgroundColor(Color.DARK_GRAY);
	}
	
	public void dispose(){
		super.dispose();
		inputMultiplexer.removeProcessor(this); //ensure we are removed
	}
	
	

	boolean dontProcessNextTyped = false;
	boolean backspaceHeld = false;
	
	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Input.Keys.BACKSPACE){
			//backspace
			//recreate (crude)
			deleteCharacter();

			dontProcessNextTyped = true;
			backspaceHeld = true;
			return false;
		} 
		
		//other not allowed characters (if we make a text area widget, enter will be allowed
		if (	   keycode == Input.Keys.ENTER
				|| keycode == Input.Keys.DEL
				|| keycode == Input.Keys.ESCAPE){

			dontProcessNextTyped = true;
			return false;
		}
		
		

		//dontProcessNextTyped = false;
		return false;
	}


	private void deleteCharacter() {
		String currentText = super.getText();
		String text=currentText.substring(0, currentText.length()-1);
		Log.info("setting text too:"+text);			
		super.setText(text);
	}

	@Override
	public boolean keyUp(int keycode) {		
		backspaceHeld=false;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {

		if (dontProcessNextTyped){		
			dontProcessNextTyped=false;
			return false;
		}
		
		if (backspaceHeld){
			deleteCharacter();
			return false;
		}
		
		if (!dontProcessNextTyped){			
			//ensure visible character
			if (isPrintableChar(character)){
				super.addText(""+	character);
			}
		} 
		
		dontProcessNextTyped=false;
		return false;
	}
	
	private boolean isPrintableChar( char c ) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	
	

}
