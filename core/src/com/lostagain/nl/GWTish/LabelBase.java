package com.lostagain.nl.GWTish;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;



/**
 * 
 * The base of all GWTish label class's
 * 
 * Specifically used at the moment as a way to remember the current cursor position
 * 
 * @author Tom
 *
 */
public class LabelBase extends Widget {
	
	//Model labelInstance = null; //temp instance (ventually we will extend it directly)
	

	//First we create a few static class's
	//All these do is help package data together so it can be returned from static functions
	
	//needed so we can create and pass both with one augment
	static class backgroundAndCursorObject {
		
		Model object = null; 
		
		Vector2 Cursor = new Vector2();

		Vector2 textureSize = null;

		public backgroundAndCursorObject(Model labelInstance,
				int i, int j, Vector2 textureSize) {		
			
			object = labelInstance;
			
			Cursor.x = i;
			Cursor.y = j;		
						
			this.textureSize = textureSize;
			
		}
	}
	
	//needed so we can create and pass both with one augment
		public static class TextureAndCursorObject {
			public Texture textureItself = null; 
			
			Vector2 Cursor = new Vector2();
			public TextureAndCursorObject(Texture texture,
					float x, float y) {			
				textureItself = texture;
				Cursor.x = x;
				Cursor.y = y;			
			}
		}
		
		
		
	
		//THIS ONE IS ONLY NEEDED TILL A NEW DATAOBJECT widget is made, 
		//as thats the only method that requires the pixmap data to be accessible from outside label
		//needed so we can create and pass both with one augment
		public static class PixmapAndCursorObject {
			public Pixmap textureItself = null; 
			
			Vector2 Cursor = new Vector2();
			public PixmapAndCursorObject(Pixmap texture,
					int i, int j) {			
				textureItself = texture;
				Cursor.x = i;
				Cursor.y = j;			
			}
		}
		
	//Current writing statistics
	Vector2 Cursor = null;

	Vector2 textureSize = null;
	
	public LabelBase(backgroundAndCursorObject setupData){
		super(setupData.object);
		
	//	labelInstance = setupData.object;
		this.Cursor = setupData.Cursor;
		this.textureSize = setupData.textureSize;		
	}
	
	
}

