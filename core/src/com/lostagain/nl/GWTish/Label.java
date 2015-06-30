package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.shaders.DistanceFieldShader;
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute;

/**
 * A Libgdx label that will eventually emulate most of the features of a GWT label (ish. VERY ish.)
 * 
 * The most significant thing here though is we enable it to use distance mapped fonts in a 3d view. 
 * This lets things look sharp at all distances.
 * With the DistanceFieldShader we can also emulate shadows and outlines - sort of letting the label have "css styles" 
 * 
 *  TODO: make this extend AnimatableModelInstance.
 *  We need to make a lot of changes to make the label a true model though.
 *  Specifically making the create model function static, and making the model directly change itself when
 *  setting text or attributes, rather then recreating itself. **/
public class Label {

	String contents = "TextNotSetError";

	final static String logstag = "ME.Label";

	static int LabelNativeWidth=512;
	static int LabelNativeHeight=512;

	Model labelModel = null;
	AnimatableModelInstance labelInstance = null;
	
	

	//setup
	Boolean setup = false;

	//defaults
	BitmapFont defaultFont;
	
	/** default scale factor of the text **/
	float ModelScale = 1.0f;

	enum SizeMode {
		/** label is a fixed, specified size and text is scaled to fit **/
		Fixed,
		/** label expands till it contains the text **/
		ExpandToFitText	
	}

	SizeMode labelsSizeMode = SizeMode.ExpandToFitText;

	Texture currentTexture = null;
	boolean modelNeedsUpdate = true;

	//Style data (mostly controlled by shader)
	private Color LabelBackColor = Color.WHITE;

	private DistanceFieldAttribute textStyle;
	
	
	/**
	 * Generates a label with the specified contents.
	 * If no size is specified it will size both the model and the internal texture resolution
	 * based on the default font size to ensure the full word is fit
	 * 
	 * @param contents
	 */
	public Label (String contents){

		 
		this.contents=contents;

		if (!setup){
			firstTimeSetUp();
			setup=true;
		}
		
		currentTexture  =null; //null tells it to regenerate
		modelNeedsUpdate=true;

	}


	static public Texture generatePixmapExpandedToFit(String text, float sizeratio) {

		

		
	  //  BitmapFontData data = DefaultStyles.standdardFont.getData();

	    GlyphLayout layout = new GlyphLayout();	    

	    layout.setText(DefaultStyles.standdardFont, text);
	    
	    float currentWidth  = layout.width;
	    float currentHeight = layout.height;
	    

		Gdx.app.log(logstag,"______________predicted size = "+currentWidth+","+currentHeight);
		
		Pixmap textPixmap = generatePixmap( text, 0, 0,  sizeratio,true); //note zeros as size isnt used

		return new Texture(textPixmap,true);

	}









	static public Texture generateTexture(String text,int TITLE_WIDTH,int TITLE_HEIGHT, float sizeratio) {

		Pixmap textPixmap = generatePixmap( text, TITLE_WIDTH, TITLE_HEIGHT,  sizeratio,false);


		return new Texture(textPixmap,true);
	}

	static public Pixmap generatePixmap(String text,int DefaultWidth,int DefaultHeight, float sizeratio, boolean expandSizeToFit) {


		String Letters=text;
		Pixmap textPixmap = new Pixmap(DefaultWidth, DefaultHeight, Format.RGBA8888);

		if (!expandSizeToFit){
			textPixmap = new Pixmap(DefaultWidth, DefaultHeight, Format.RGBA8888);

		} else {
			//start arbitrarily big (this will be fixed later
			textPixmap = new Pixmap(500, 500, Format.RGBA8888);

		}

		//	textPixmap.setColor(1, 0, 0, 1);					
		//	textPixmap.drawRectangle(3, 3, TITLE_WIDTH-3, TITLE_HEIGHT-3);

		BitmapFontData data = DefaultStyles.standdardFont.getData(); //new BitmapFontData(Gdx.files.internal(data.imagePaths[0]), true);

		Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0]));

		// draw the character onto our base pixmap

		int totalwidth=0;
		int current_testedwidth=0;

		int currentX=0;

		float scaledown = sizeratio;

		Glyph defaultglyph = data.getGlyph(Letters.charAt(0));


		int totalheight=defaultglyph.height+9;

		Gdx.app.log(logstag,"scaledown="+scaledown);
		double lastremainder =0;
		int yp=0;

		int destX = 0;
		int destY = 0;
		int cheight = 0;

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
			cheight = (int)(glyph.height * scaledown);

			int yglyphoffset = (int) (glyph.yoffset * scaledown);

			destX = 0+currentX+glyph.xoffset;
			destY = 0+(yp+(yglyphoffset ));
			//note if we are going to go of the edge, and we are on expand mode, we have to quickly get a bigger map to work in
			boolean hadToEnlarge = false;
			int biggestX = destX+cwidth;
			int biggestY = destY+cheight;

			if (expandSizeToFit && (biggestX>textPixmap.getWidth())){
				Gdx.app.log(logstag,"______________x ("+biggestX+") out of range, having to make canvas bigger");
				//we just double the X size, as we are cropping later anyway
				biggestX=biggestX*2;
				hadToEnlarge =  true;					
			} else {
				biggestX = textPixmap.getWidth();
			}
			
			if (expandSizeToFit && (biggestY>textPixmap.getHeight())){
				Gdx.app.log(logstag,"______________y ("+biggestY+") out of range, having to make canvas bigger");
				//we just double the Y size, as we are cropping later anyway
				biggestY=biggestY*2;
				hadToEnlarge =  true;					
			} else {
				biggestY = textPixmap.getHeight();
			}
			
			if (hadToEnlarge){
				textPixmap = sizeTo(textPixmap, biggestX, biggestY);
			}
			//--------------------
			
			
			textPixmap.drawPixmap(
					fontPixmap,
					glyph.srcX,
					glyph.srcY, 
					glyph.width, 
					glyph.height+1,
					destX,
					destY,//+(TILE_HEIGHT - (cheight)) / 2,						
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

		if (expandSizeToFit){
			//crop down to final size
			int biggestX = currentX;
			int biggestY = destY+cheight;

			Gdx.app.log(logstag,"______________final cropped size="+biggestX+","+biggestY);

			textPixmap = sizeTo(textPixmap, biggestX, biggestY);

			LabelNativeWidth  = biggestX;
			LabelNativeHeight = biggestY;

		}

		return textPixmap;

	}


	private static Pixmap sizeTo(Pixmap textPixmap, int biggestX, int biggestY) {
		Pixmap croppedPixMap = new Pixmap(biggestX, biggestY, Format.RGBA8888);
		croppedPixMap.drawPixmap(textPixmap, 0, 0);

		textPixmap.dispose();
		textPixmap = croppedPixMap;
		return textPixmap;
	}

	/**
	 * Use this to set the style of the text
	 * @param style
	 * @return 
	 */
	public void setDistanceFieldAttribute(DistanceFieldAttribute style){
		textStyle = style;
	}
	

	private Model createModel() {

		if (currentTexture==null){
			regenerateTexture();
		}


		currentTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);//MipMapLinearNearest does not work with DistanceField shaders
		
		if (textStyle==null){
			textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);
		}
				
		
		
		Material mat = 	new Material("LabelMaterial",TextureAttribute.createDiffuse(currentTexture),			
									 ColorAttribute.createDiffuse(LabelBackColor),
									 textStyle);

		
		
		//Gdx.app.log(logstag,"______________text glow col is: "+teststyle.glowColour);
		//Gdx.app.log(logstag,"______________generating rect of "+LabelWidth+","+LabelHeight);
		//
		labelModel = ModelMaker.createRectangle(0, 0, LabelNativeWidth*this.ModelScale,LabelNativeHeight*this.ModelScale, 0, mat); 

		labelInstance = new AnimatableModelInstance(labelModel);
		
	//	DistanceFieldAttribute textStyleData = (DistanceFieldAttribute)mat.get(DistanceFieldAttribute.ID);
	//	Gdx.app.log(logstag,"______________text glow col is2: "+textStyleData.glowColour);
		//Matrix4 newmatrix = new Matrix4();
		//newmatrix.setToRotation(0, 0, 1, -90);
		//labelInstance.transform.mul(newmatrix);

		//labelInstance.userData = MyShaderProvider.shadertypes.distancefield;
		
		modelNeedsUpdate = false;
		
		return labelModel;

	}
	
	/**
	 * Sets the text and regenerates the texture (does not yet auto-update any generated models from this label!)
	 **/
	public void setText(String text){
		this.contents=text;
		
		//regenerateTexture();
		
		currentTexture  =null; //null tells it to regenerate
		modelNeedsUpdate=true;
		
		
	}
	/**
	 * A scaleing factor that will enlarge of shrink the text relative to the standard font size.
	 * NOTE: this does not scale the internal texture size. As we are using a distance field font, it should look sharp at all distances anyway.
	 * Scaleing would not help.
	 * @param text
	 */
	public void setTextScale(float scale){
		ModelScale = scale;
		
		currentTexture  =null; //null tells it to regenerate
		modelNeedsUpdate=true;
		
	}

	private void regenerateTexture() {
		if (labelsSizeMode == SizeMode.ExpandToFitText){

			Gdx.app.log(logstag,"______________generating expand to fit text ");

			currentTexture = generatePixmapExpandedToFit(contents,1f); //new Texture(Gdx.files.internal("data/dfield.png"), true);


		} else {
			currentTexture = generateTexture(contents,LabelNativeWidth, LabelNativeHeight,1f); //new Texture(Gdx.files.internal("data/dfield.png"), true);

		}
		modelNeedsUpdate=true;
	}

	public void firstTimeSetUp(){

		//	Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), true);
		//	texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		//testImage  = new Image(texture);
		//defaultFont = new BitmapFont(Gdx.files.internal("data/dfield.fnt"), new TextureRegion(texture), false);

	}

	/**
	 * gets the current model instance, or recreates it if its changed
	 * @return
	 */
	public AnimatableModelInstance getModel() {
		if (modelNeedsUpdate){
			createModel();
		}
		return labelInstance;
	}


	//
	//
	//--------------
	//Styleing functions below.
	//These are all subject to a lot of change
	//Especially as we
	//a) Try to make this Label extend ModelInstance
	//b) Try to make it as GWT-like as possible in its api
	public void setLabelBackColor(Color labelBackColor) {
		LabelBackColor = labelBackColor;
		modelNeedsUpdate=true;
	}

	public void setOpacity(float opacity){
		//get the material from the model
		Material infoBoxsMaterial = this.getModel().getMaterial("LabelMaterial");
		((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type)).opacity = opacity;
		
		
	}
	
	
	
	

}
