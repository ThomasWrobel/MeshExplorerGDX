package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.shaders.MyShaderProvider;

/** A Libgdx label that will eventually emulate most of the features of a GWT label (ish)
 * The most significant thing here though is we enable it to use distance mapped fonts in a 3d view **/
public class Label {

	String contents = "TEST";

	final static String logstag = "ME.Label";
	
    static int TITLE_WIDTH=512;
    static int TITLE_HEIGHT=512;
	Model labelModel = null;
	ModelInstance labelInstance = null;
	//image
	Image testImage;
	
	//setup
	Boolean setup = false;
	
	//defaults
	BitmapFont defaultFont;
	
	
	public Label (String contents){
		this.contents=contents;
		
		if (!setup){
			firstTimeSetUp();
			setup=true;
		}
		
		createModel();
		
		
		
	}
	
	
	private Texture generateTexture(String text) {
		
		  String Letters=text;
		  Pixmap textPixmap = new Pixmap(TITLE_WIDTH, TITLE_HEIGHT, Format.RGBA8888);
		//	textPixmap.setColor(1, 0, 0, 1);					
		//	textPixmap.drawRectangle(3, 3, TITLE_WIDTH-3, TITLE_HEIGHT-3);

		    BitmapFontData data = DefaultStyles.standdardFont.getData(); //new BitmapFontData(Gdx.files.internal(data.imagePaths[0]), true);
		    
		    Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0]));
		    
			// draw the character onto our base pixmap
		  
		  int totalwidth=0;
		  int current_testedwidth=0;

		    int currentX=0;
	
		  float scaledown = 1f;
		 			  
		  Glyph defaultglyph = data.getGlyph(Letters.charAt(0));
		  
		  int totalheight=defaultglyph.height+9;
		  
			Gdx.app.log(logstag,"scaledown="+scaledown);
			double lastremainder =0;
			int yp=0;
			for (int i = 0; i < Letters.length(); i++) {
				
				Glyph glyph = data.getGlyph(Letters.charAt(i));
				
				if (glyph==null){
					glyph=defaultglyph; //temp
				
					
				}
				


				//Gdx.app.log(logstag,"Letters.charAt(i)="+Letters.charAt(i));
				
				if (Letters.charAt(i) == '\n'){

					Gdx.app.log(logstag,"______________adding line=");
					
					//new line
					yp=(int) (yp+(defaultglyph.height* scaledown)+5);
					currentX=0;
				}
				
				
				int cwidth =  (int)(glyph.width  * scaledown);
				int cheight = (int)(glyph.height * scaledown);

				int yglyphoffset = (int) (glyph.yoffset * scaledown);
				
			//	Gdx.app.log(logstag,"cwidth="+cwidth);
				
				textPixmap.drawPixmap(
						fontPixmap,
						glyph.srcX,
						glyph.srcY, 
						glyph.width, 
						glyph.height+1,
						0+currentX+glyph.xoffset,
						0+(yp+(yglyphoffset )),//+(TILE_HEIGHT - (cheight)) / 2,						
						cwidth, 
						cheight);
				
				
				/*
				textPixmap.drawPixmap(
						fontPixmap,
						xpad+currentX,
						(TILE_HEIGHT - glyph.height) / 2, 
						glyph.srcX,
						glyph.srcY, glyph.width, glyph.height);
				*/
				double newprecisepos =  ((glyph.xadvance+2)  * scaledown)+lastremainder;//glyph.width+3
				lastremainder = newprecisepos - Math.floor(newprecisepos);
				int newpos = (int) (Math.floor(newprecisepos));
			//	Gdx.app.log(logstag,"newpos="+newpos);
			//	Gdx.app.log(logstag,"lastremainder="+lastremainder);
				currentX=currentX + newpos;
			}
			
		    
		    return new Texture(textPixmap,true);
		  
	}
	
	
	private void createModel() {
		
		//Material mat = new Material(ColorAttribute.createDiffuse(Color.MAROON));
	
		//mat.set(TextureAttribute.createDiffuse(idealAnimation.getKeyFrame(0)));

    	Texture texture = generateTexture(contents); //new Texture(Gdx.files.internal("data/dfield.png"), true);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);//MipMapLinearNearest
		
		Material mat = 	new Material(TextureAttribute.createDiffuse(texture),
									new BlendingAttribute(1f),
									ColorAttribute.createDiffuse(Color.CYAN));
		
		labelModel = ModelMaker.createRectangle(0, 0, 400,400, 0, mat);

		labelInstance = new ModelInstance(labelModel); 

		//Matrix4 newmatrix = new Matrix4();
		//newmatrix.setToRotation(0, 0, 1, -90);
		//labelInstance.transform.mul(newmatrix);
		
		labelInstance.userData = MyShaderProvider.shadertypes.distancefield;
		
		
	}

	public void firstTimeSetUp(){

    //	Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), true);
	//	texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		//testImage  = new Image(texture);
		//defaultFont = new BitmapFont(Gdx.files.internal("data/dfield.fnt"), new TextureRegion(texture), false);
		
	}

	public ModelInstance getModel() {
		
		return labelInstance;
	}
	
	
}
