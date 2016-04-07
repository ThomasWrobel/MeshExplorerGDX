package com.lostagain.nl;

import java.util.ArrayList;
import java.util.HashSet;
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
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.uti.HSLColor;

public class DefaultStyles {

	final static String logstag = "ME.DefaultStyles";
		

	public static final Color lockedLabel = new Color(1f, 0f, 0f, 0.5f);
	public static final Color unlockedLabel = new Color(0f, 1f, 0f, 0.5f);

	public static final Color SpecialDownloadLabel = new Color(0.9f,0.7f, 0.3f, 1f);

	public static final Color lighterAmount = new Color(0.5f,0.5f, 0.5f, 0.5f);
	public static final Color labelpressed = new Color(unlockedLabel).add(lighterAmount);
	//Color.rgba8888(200f, 0f, 0f, 0.5f);


	public static final Skin defaultStyles = new Skin(Gdx.files.internal("data/uiskin.json"));


	public static Skin linkstyle = new Skin(Gdx.files.internal("data/uiskin.json"));
	public	static Skin buttonstyle = new Skin(Gdx.files.internal("data/uiskin.json"));

	public static Skin colors = new Skin();



	public static Texture texturespaced = new Texture(Gdx.files.internal("data/fonttest_spaced.png"), true);
	public static BitmapFont standdardFont = new BitmapFont(Gdx.files.internal("data/fonttest_spaced.fnt"), new TextureRegion(texturespaced), true);
		
	
	public static Texture texture = new Texture(Gdx.files.internal("data/standardfont.png"), true);
	
	public static BitmapFont scramabledFont = new BitmapFont(Gdx.files.internal("data/dfieldscrambled.fnt"), new TextureRegion(texture), true);	
	public static BitmapFont standdardFont_interface = new BitmapFont(Gdx.files.internal("data/standardfont.fnt"), new TextureRegion(texture), false);


	public static void setupStyles(){		


		Gdx.app.log(logstag,"___________setupStyles___");
		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		colors.add("white", new Texture(pixmap));

	}


	/** if a node has colors, this will return them 
	 * 
	 * returns null if none found **/
	static public ArrayList<Color> getColorsFromNode(SSSNode node){
		//http://dbpedia.org/ontology/hsvCoordinateHue
		Gdx.app.log(logstag, " getting nodes colours ");
		
		ArrayList<Color> colours = new ArrayList<Color>();

		HashSet<SSSNodesWithCommonProperty> Nodeproperties = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(node.PURI);
		
		String newcolorstring = "Red";

		for (SSSNodesWithCommonProperty property : Nodeproperties) {

			SSSNode currentPred  = property.getCommonPrec();
			SSSNode currentValue = property.getCommonValue();

			Color newcolor=null;;
			
			//only color supported atm later we might search for other style related attributes?
			if (currentPred == StaticSSSNodes.DBPediaColour){
				newcolorstring =currentValue.getPLabel();

				newcolor = getColorFromString(newcolorstring);
			}	else if (currentPred == StaticSSSNodes.DBPediaHueCoOrd){
				
				newcolorstring = currentValue.getPLabel();
				float huefloat = Integer.parseInt(newcolorstring)/360.0f;
				//default color extraction from hue (might want customizable defaults in future)				
				newcolor= new HSLColor(huefloat,1.0f,0.1f,0.8f).toRGB();			
				
			}	else {
				continue;
			}
			
			
			if (newcolor!=null){

				Gdx.app.error (logstag, " adding color:	 "+newcolor.toString());
				colours.add(newcolor);
			} else {
				Gdx.app.error (logstag, " color not recognised:	 "+newcolorstring);
			}
			
			
		}	



		//return null if none found
		if (colours.size()==0){
			return null;
		}
		

		//set color (crude)
		/*
	if (newcolorstring.equalsIgnoreCase("Green")){
		newcolor = Color.GREEN;
	} else if (newcolorstring.equalsIgnoreCase("Blue")){
		newcolor = Color.BLUE;
	}	else {
		newcolor = Color.RED;
	}

		 */
		return colours;

	}


	/**
	 * 
	 * @param newcolorstring - color as string 
	 * @return color or null if no match found
	 */
	public static Color getColorFromString(String newcolorstring) {
		
		Color newcolor = null; //default color 
		
		newcolorstring=newcolorstring.trim();
		
		//if it starts with a hash
		if (newcolorstring.startsWith("#")){
									
			String colordata = newcolorstring.substring(1);
			newcolor = Color.valueOf(colordata);
					
			
			
			return newcolor;
		}
		
		//Normalize the string by going to lower case and replacing spaces with _
		newcolorstring = newcolorstring.toLowerCase();
		newcolorstring = newcolorstring.replace(" ", "_");

		switch(newcolorstring.toLowerCase()) {

		case "black":
			newcolor = Color.BLACK;
			break;
		case "blue":
			newcolor = Color.BLUE;
			break;
		case "cyan":
			newcolor = Color.CYAN;
			break;
		case "dark_gray":
			newcolor = Color.DARK_GRAY;
			break;    
		case "gray":
			newcolor = Color.GRAY;
			break;
		case "green":
			newcolor = Color.GREEN;
			break;
		case "light grey":
			newcolor = Color.LIGHT_GRAY;
			break;
		case "magenta":
			newcolor = Color.MAGENTA;	
			break; 
		case "maroon":
			newcolor = Color.MAROON;
			break;
		case "navy":
			newcolor = Color.NAVY;
			break;
		case "olive":
			newcolor = Color.OLIVE;
			break;
		case "orange":
			newcolor = Color.ORANGE;
			break;    
		case "pink":
			newcolor = Color.PINK;
			break;
		case "purple":
			newcolor = Color.PURPLE;
			break;
		case "red":
			newcolor = Color.RED;
			break; 
		case "teal":
			newcolor = Color.TEAL;
			break; 
		case "white":
			newcolor = Color.WHITE;
			break;
		case "yellow":
			newcolor = Color.YELLOW;
			break; 
		default:
			newcolor = null;
			break;
		}
		return newcolor;
	}
}