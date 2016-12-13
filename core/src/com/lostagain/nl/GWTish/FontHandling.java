package com.lostagain.nl.GWTish;

import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FontHandling {

	public static Texture texturespaced = new Texture(Gdx.files.internal("data/fonttest_spaced.png"), true);
	public static BitmapFont standdardFont = new BitmapFont(Gdx.files.internal("data/fonttest_spaced.fnt"), new TextureRegion(texturespaced), true); //font file says size is 32
	public static Texture texture = new Texture(Gdx.files.internal("data/standardfont.png"), true);
	public static BitmapFont scramabledFont = new BitmapFont(Gdx.files.internal("data/dfieldscrambled.fnt"), new TextureRegion(texture), true);
	public static BitmapFont standdardFont_interface = new BitmapFont(Gdx.files.internal("data/standardfont.fnt"), new TextureRegion(texture), false);

	
	
	static HashMap<BitmapFont,Float> sizecache = new HashMap<BitmapFont,Float>();
	
	public static void cacheFontSizes(){
		
		sizecache.put(standdardFont,standdardFont.getLineHeight());
		sizecache.put(standdardFont_interface,standdardFont_interface.getLineHeight());
		sizecache.put(scramabledFont,scramabledFont.getLineHeight());
		
		
	}
	
	
	
	/**
	 * gets the native font size.
	 * This is currently a bit of a hack job, using a cache of line-heights when the font first loads.
	 * (as lineheights might be changed later)
	 * @param font
	 * @return
	 */
	public static float getNativeFontSize(BitmapFont font){
		Float height = sizecache.get(font);
		
		if (height==null){
			height=font.getLineHeight();
			sizecache.put(font,height);
			
		}
		
		return height;
	}
	
}
