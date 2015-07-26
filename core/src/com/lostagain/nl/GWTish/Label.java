package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
public class Label extends LabelBase {

	public static final String LABEL_MATERIAL = "LabelMaterial";

	String contents = "TextNotSetError";
	

	final static String logstag = "ME.Label";

	static int LabelNativeWidth =512;
	static int LabelNativeHeight=512;

	Model labelModel = null;

	

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
		ExpandXYToFit,
		/**
		 * Expands variably with new lines, but wraps to the width
		 */
		ExpandHeightMaxWidth,
		
	}

	SizeMode labelsSizeMode = SizeMode.ExpandXYToFit;
	float maxWidth = -1; //default for no max
	
	//Texture currentTexture = null;
	//boolean modelNeedsUpdate = true;

	//Style data (mostly controlled by shader)
	static private Color defaultBackColour = Color.WHITE;

	public Label (String contents,float MaxWidth){
		super(generateObjectData(true, true, contents, SizeMode.ExpandHeightMaxWidth,MaxWidth));
		 
		super.setStyle(getMaterial(LABEL_MATERIAL));
		this.maxWidth = MaxWidth;
			this.contents=contents;
				
			if (!setup){
				firstTimeSetUp();
				setup=true;
			}
	}

	/**
	 * Generates a label with the specified contents.
	 * If no size is specified it will size both the model and the internal texture resolution
	 * based on the default font size to ensure the full word is fit
	 * 
	 * @param contents
	 **/
	public Label (String contents){
		super(generateObjectData(true, true, contents, SizeMode.ExpandXYToFit,-1));//No max width

		super.setStyle(this.getMaterial(LABEL_MATERIAL));
		this.maxWidth = -1;
		this.contents=contents;
			
		if (!setup){
			firstTimeSetUp();
			setup=true;
		}
		
		//currentTexture  =null; //null tells it to regenerate
		//modelNeedsUpdate=true;

	}


	/**
	 * The object data needed on creation is just the background mesh instance and the cursor position.
	 * This shouldn't need to be run outside the objects first creation.
	 * After its created everything should be alterable separately without recreation
	 * @param maxWidth 
	 * 
	 * @return	  
	 **/
	private static backgroundAndCursorObject generateObjectData(boolean regenTexture,boolean regenMaterial,String contents,SizeMode labelsSizeMode, float maxWidth ) {
		TextureAndCursorObject textureData = null;
		
		
		
		if (regenTexture){
			
			
			
			textureData = generateTexture(labelsSizeMode, contents,maxWidth);
			
		}
		Texture newTexture = textureData.textureItself;
		
		newTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);//MipMapLinearNearest does not work with DistanceField shaders

		DistanceFieldAttribute textStyle = null;
		
		
		//if (textStyle==null){
			textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);
			
		//}
				
			
		
		Material mat = 	new Material(LABEL_MATERIAL,	
									 TextureAttribute.createDiffuse(newTexture),
									 new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1.0f),
									 ColorAttribute.createDiffuse(defaultBackColour), //needs to be passed into this function
									 textStyle);

		
		//we get the size from the generated material
		float sizeX = newTexture.getWidth();
		float sizeY = newTexture.getHeight();
		
		
		//Gdx.app.log(logstag,"______________text glow col is: "+teststyle.glowColour);
		//Gdx.app.log(logstag,"______________generating rect of "+LabelWidth+","+LabelHeight);
		
		//Note the *1 is the scale. We have scale 1 by default, duh.
		Model newModel = Widget.generateBackground(sizeX, sizeY, mat, MODELALIGNMENT.TOPLEFT);
		
				
				//ModelMaker.createRectangle(0, 0, sizeX*1,sizeY*1, 0, mat); 

		
		backgroundAndCursorObject setupData = new backgroundAndCursorObject(newModel,0,0);
		
		
		return setupData;
		
		
	}


	static public TextureAndCursorObject generatePixmapExpandedToFit(String text, float sizeratio,float maxWidth) {

		

		
	  //  BitmapFontData data = DefaultStyles.standdardFont.getData();

	    GlyphLayout layout = new GlyphLayout();	    

	    layout.setText(DefaultStyles.standdardFont, text);
	    
	    float currentWidth  = layout.width;
	    float currentHeight = layout.height;
	    

		Gdx.app.log(logstag,"______________predicted size = "+currentWidth+","+currentHeight);
		
		TextureAndCursorObject textureDAta = generateTexture( text, 0, 0,  sizeratio, true,maxWidth); //note zeros as size isn't used

		
		return textureDAta;

	}









	static public TextureAndCursorObject generateTextureNormal(String text,int TITLE_WIDTH,int TITLE_HEIGHT, float sizeratio) {

		TextureAndCursorObject textureDAta = generateTexture( text, TITLE_WIDTH, TITLE_HEIGHT,  sizeratio,false,-1);
		
		return textureDAta;
	}

	static public TextureAndCursorObject generateTexture(String text,int DefaultWidth,int DefaultHeight, float sizeratio, boolean expandSizeToFit, float maxWidth) {
		 
		PixmapAndCursorObject data = generatePixmap(text, DefaultWidth, DefaultHeight, sizeratio, expandSizeToFit,maxWidth);
					
		
		
		return new TextureAndCursorObject(new Texture(data.textureItself),data.Cursor.x,data.Cursor.y);
	}
	
	static public PixmapAndCursorObject generatePixmap(String text,int DefaultWidth,int DefaultHeight, float sizeratio, boolean expandSizeToFit, float maxWidth) {

		//if maxWidth = -1 then theres no max width
		
		String Letters    = text;
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

		//Glyph defaultglyph = data.getGlyph(Letters.charAt(0));


		//int totalheight=defaultglyph.height+9;

		Gdx.app.log(logstag,"scaledown="+scaledown);
		double lastremainder =0;
		int yp=0;

		int destX = 0;
		int destY = 0;
		int cheight = 0;
		
		//the bottom right corner of the texture map thats used
		int biggestX = 0;
		int biggestY = 0;
		
		for (int i = 0; i < Letters.length(); i++) {

			Glyph glyph = data.getGlyph(Letters.charAt(i));

			if (glyph==null){
				Gdx.app.log(logstag,"_______(current glyph not valid not in character set, setting glyph to space)");
				glyph=data.getGlyph(' '); //temp

				
			}


			int cwidth =  (int)(glyph.width  * scaledown);
			cheight = (int)(glyph.height * scaledown);

			int yglyphoffset = (int) (glyph.yoffset * scaledown);

			destX = 0+currentX+glyph.xoffset;
		
			
			//Gdx.app.log(logstag,"Letters.charAt(i)="+Letters.charAt(i));

			if (Letters.charAt(i) == '\n' || (destX>maxWidth && maxWidth!=-1) ){

				//new line  NB; defaultglyph.height seems to be zero for some reason
				yp=(int) (yp+(data.lineHeight* scaledown)+5);
				currentX=0;
				destX=glyph.xoffset;
				lastremainder=0;
				//Gdx.app.log(logstag,"______________adding line. (yp now="+yp+") next char is:"+Letters.charAt(i));
				
				//we skip \n as we don't want to really write that
				if (Letters.charAt(i) == '\n'){
					continue;
				}
				//---
			}
			
			destY = 0+(yp+(yglyphoffset ));



			//note if we are going to go of the edge, and we are on expand mode, we have to quickly get a bigger map to work in
			boolean hadToEnlarge = false;
			int cbiggestX = destX+cwidth;
			int cbiggestY = destY+cheight;

			//ensure its bigger then anything we have already (remember cbiggest is just the current lines largest x value, not the overall for all lines)
			if (cbiggestX>biggestX){
				biggestX = cbiggestX;
			}
			if (cbiggestY>biggestY){
				biggestY = cbiggestY;
			}
			if (expandSizeToFit && (biggestX>textPixmap.getWidth())){
				Gdx.app.log(logstag,"______________x ("+biggestX+") out of range, having to make canvas bigger");
				//we just double the X size, as we are cropping later anyway
				cbiggestX=biggestX*2;
				hadToEnlarge =  true;					
			} else {
				cbiggestX = textPixmap.getWidth();
			}
			
			if (expandSizeToFit && (biggestY>textPixmap.getHeight())){
				Gdx.app.log(logstag,"______________y ("+cbiggestY+") out of range, having to make canvas bigger");
				//we just double the Y size, as we are cropping later anyway
				cbiggestY=biggestY*2;
				hadToEnlarge =  true;					
			} else {
				cbiggestY = textPixmap.getHeight();
			}
			
			if (hadToEnlarge){
				textPixmap = sizePixmapTo(textPixmap, cbiggestX, cbiggestY);
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



			double newprecisepos =  ((glyph.xadvance+2)  * scaledown)+lastremainder;//glyph.width+3
			lastremainder = newprecisepos - Math.floor(newprecisepos);
			int newpos = (int) (Math.floor(newprecisepos));
			//	Gdx.app.log(logstag,"newpos="+newpos);
			//	Gdx.app.log(logstag,"lastremainder="+lastremainder);
			currentX=currentX + newpos;
		}

		if (expandSizeToFit){
			//crop down to final size
			// biggestX = currentX;
			// biggestY = destY+cheight;

			Gdx.app.log(logstag,"______________final cropped size="+biggestX+","+biggestY);

			textPixmap = sizePixmapTo(textPixmap, biggestX, biggestY);
			
		//	LabelNativeWidth  = biggestX;
			//LabelNativeHeight = biggestY;

		}

		//0,0 should be current cursor position after this update
		return new PixmapAndCursorObject(textPixmap,0,0);

	}


	private static Pixmap sizePixmapTo(Pixmap textPixmap, int biggestX, int biggestY) {
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
	//public void setDistanceFieldAttribute(DistanceFieldAttribute style){
	//	textStyle = style;
	//}
	
/*
	private Model createModel() {

		if (currentTexture==null){
			regenerateTexture(labelsSizeMode, contents);
			
		}


		currentTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);//MipMapLinearNearest does not work with DistanceField shaders
		
		if (textStyle==null){
			textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);
		}
				
		
		
		Material mat = 	new Material("LabelMaterial",
									 TextureAttribute.createDiffuse(currentTexture),			
									 ColorAttribute.createDiffuse(defaultBackColour),
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
	*/
	
	
	/**
	 * Sets the text and regenerates the texture 
	 * Also doesn't remember cursor position. This is needed if we want to correctly ADD text to the texture in future, rather then recreating it all
	 * For animated text this optimization is pretty essential
	 **/
	public void setText(String text){
		this.contents=text;
		
		
		
		TextureAndCursorObject textureAndData = generateTexture(labelsSizeMode, contents,maxWidth); //-1 is the default max width which means "any size"
		

		Material infoBoxsMaterial = this.getMaterial(LABEL_MATERIAL);	


		Texture newTexture = textureAndData.textureItself;
		
		
				
		//if (textStyle==null){
			//textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);
		
		//ColorAttribute ColorAttributestyle = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));	
			//  TextureAttribute.createDiffuse(NewTexture.textureItself)	,	
		// ColorAttribute.createDiffuse(defaultBackColour)
		infoBoxsMaterial.set(TextureAttribute.createDiffuse(newTexture));

		float x = textureAndData.textureItself.getWidth();
		float y = textureAndData.textureItself.getHeight();
		
		Gdx.app.log(logstag,"_________setting text to;"+text+" size:"+x+","+y);

		Gdx.app.log(logstag,"_________vis1:"+this.isVisible()+" parent:"+this.parentObject.isVisible());
		
		this.setSizeAs(x, y);
		

		Gdx.app.log(logstag,"_________vis2:"+this.isVisible()+" parent:"+this.parentObject.isVisible());
		
	}
	/**
	 * A scaleing factor that will enlarge of shrink the text relative to the standard font size.
	 * NOTE: this does not scale the internal texture size. As we are using a distance field font, it should look sharp at all distances anyway.
	 * Scaleing would not help.
	 * @param text
	 */
	public void setTextScale(float scale){
		ModelScale = scale;
		
		//currentTexture  =null; //null tells it to regenerate
		//modelNeedsUpdate=true;
		
	}

	static private TextureAndCursorObject generateTexture(SizeMode labelsSizeMode, String contents, float maxWidth) {
		
		
		
		TextureAndCursorObject NewTexture = null;
		
		
		switch (labelsSizeMode) {
		
		case ExpandXYToFit:
			Gdx.app.log(logstag,"______________generating expand to fit text ");
			NewTexture = generatePixmapExpandedToFit(contents,1f,-1); //-1 = no max width
			break;
		case ExpandHeightMaxWidth:
			NewTexture = generatePixmapExpandedToFit(contents,1f,maxWidth); //-1 = no max width
			break;
		case Fixed:
			break;
		default:
			NewTexture = generateTextureNormal(contents,LabelNativeWidth, LabelNativeHeight,1f);
			break;
	
		}
		
		NewTexture.textureItself.setFilter(TextureFilter.Linear, TextureFilter.Linear);//ensure mipmaping is disabled, else distance field shaders wont work
		
		return NewTexture;
	}

	public void firstTimeSetUp(){

		//	Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), true);
		//	texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		//testImage  = new Image(texture);
		//defaultFont = new BitmapFont(Gdx.files.internal("data/dfield.fnt"), new TextureRegion(texture), false);

	}



	


	
	//
	//
	//--------------
	// Styling functions below.
	// These are all subject to a lot of change
	// Especially as we
	// Try to make it as GWT-like as possible in its api

	/**
	 * sets the back color
	 * @param labelBackColor
	 */
	public void setLabelBackColor(Color labelBackColor) {
	//	labelBackColor = Color.PINK; //TEMP during testing. Currently another shader bug - the background colour isn't being used correctly for the transparancy, its only effecting the shadows blending
		Material infoBoxsMaterial = this.getMaterial(LABEL_MATERIAL);		
		//ColorAttribute ColorAttributestyle = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));
	
		infoBoxsMaterial.set( ColorAttribute.createDiffuse(labelBackColor));
		
	}

	
	@Override
	public void setOpacity(float opacity){
		//super.setOpacity(opacity);
		
		//Material infoBoxsMaterial = this.getMaterial("LabelMaterial");	
		
		/*
		if (infoBoxsMaterial.has(BlendingAttribute.Type)){
			((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type)).opacity = 0.1f;
		} else {
			BlendingAttribute blend = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.1f);
			infoBoxsMaterial.set(blend);
			
		}*/
		
		
		
		
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial(LABEL_MATERIAL);
		
		DistanceFieldAttribute style = ((DistanceFieldAttribute)infoBoxsMaterial.get(DistanceFieldAttribute.ID));
		style.setOverall_Opacity_Multiplier(opacity);
		
		//Gdx.app.log(logstag,"_____________current            col:"+style.textColour);
		//Gdx.app.log(logstag,"_____________current shadow     col:"+style.shadowColour);
		//Gdx.app.log(logstag,"_____________current glowColour col:"+style.glowColour);
		/*
	   style.outlineColour.a = opacity; //this doesn't work right in shader ...hmm...we also shouldn't overwrite like this anyway

		  style.textColour.a = opacity; //as they might have deliberately seperate opacitys even when fully visible
		  style.glowColour.a = opacity;
		style.shadowColour.a = opacity;
		*/
		
		
		//NOTE: This backgrounds color shouldn't be set directly like this, as it might not be 100% opacity to start with
		//ColorAttribute background = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));
		//background.color.a = opacity;
		BlendingAttribute backgroundOpacity = ((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type));
		backgroundOpacity.opacity = opacity;
	//	Gdx.app.log(logstag,"_____________opacity:"+opacity);
	}

	public void setMaxWidth(float maxWidth) {
		this.maxWidth = maxWidth;
		labelsSizeMode = SizeMode.ExpandHeightMaxWidth;
	}

	

	
	
	
	

}
