package com.lostagain.nl.me.LocationGUI;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;

public class DefaultStyles {

	static Logger Log = Logger.getLogger("ME.DefaultStyles");

	
	public static final Color lockedLabel = new Color(1f, 0f, 0f, 0.5f);
	public static final Color unlockedLabel = new Color(0f, 1f, 0f, 0.5f);

	public static final Color SpecialDownloadLabel = new Color(0.9f,0.7f, 0.3f, 1f);

	public static final Color lighterAmount = new Color(0.5f,0.5f, 0.5f, 0.5f);
	public static final Color labelpressed = new Color(unlockedLabel).add(lighterAmount);
			//Color.rgba8888(200f, 0f, 0f, 0.5f);


	public static final Skin defaultStyles = new Skin(Gdx.files.internal("data/uiskin.json"));


	
	
	
	
	
	
	
	

	public static Skin linkstyle = new Skin(Gdx.files.internal("data/uiskin.json"));
	static Skin buttonstyle = new Skin(Gdx.files.internal("data/uiskin.json"));
	
	static Skin colors = new Skin();
	
	//static ProgressBarStyle barStyle = new ProgressBarStyle(Skin.newDrawable("white", Color.DARK_GRAY), textureBar);
	static Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), true);
	static BitmapFont scramabledFont = new BitmapFont(Gdx.files.internal("data/dfieldscrambled.fnt"), new TextureRegion(texture), false);
	static BitmapFont standdardFont = new BitmapFont(Gdx.files.internal("data/dfield.fnt"), new TextureRegion(texture), false);

	
	public static void setupStyles(){
		
		

    	Log.info("___________setupStyles___");

		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		colors.add("white", new Texture(pixmap));
		

    	
	}
	
}
