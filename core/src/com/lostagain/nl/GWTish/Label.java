package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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
		ExpandToFitText	
	}

	SizeMode labelsSizeMode = SizeMode.ExpandToFitText;

	Texture currentTexture = null;
	boolean modelNeedsUpdate = true;

	//Style data (mostly controlled by shader)
	static private Color defaultBackColour = Color.WHITE;

	

	/**
	 * Generates a label with the specified contents.
	 * If no size is specified it will size both the model and the internal texture resolution
	 * based on the default font size to ensure the full word is fit
	 * 
	 * @param contents
	 **/
	public Label (String contents){
		super(generateObjectData(true, true, contents, SizeMode.ExpandToFitText));
		
		 
		this.contents=contents;
			
		if (!setup){
			firstTimeSetUp();
			setup=true;
		}
		
		currentTexture  =null; //null tells it to regenerate
		modelNeedsUpdate=true;

	}


	/**
	 * The object data needed on creation is just the background mesh instance and the cursor position.
	 * This shouldn't need to be run outside the objects first creation.
	 * After its created everything should be alterable separately without recreation
	 * 
	 * @return	  
	 **/
	private static backgroundAndCursorObject generateObjectData(boolean regenTexture,boolean regenMaterial,String contents,SizeMode labelsSizeMode ) {
		TextureAndCursorObject textureData = null;
		
		
		if (regenTexture){
			textureData = generateTexture(labelsSizeMode, contents);
			
		}
		Texture newTexture = textureData.textureItself;
		
		newTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);//MipMapLinearNearest does not work with DistanceField shaders

		DistanceFieldAttribute textStyle = null;
		
		
		if (textStyle==null){
			textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);
		}
				
		
		
		Material mat = 	new Material("LabelMaterial",
									 TextureAttribute.createDiffuse(newTexture),			
									 ColorAttribute.createDiffuse(defaultBackColour), //needs to be passed into this function
									 textStyle);

		
		//we get the size from the generated material
		float sizeX = newTexture.getWidth();
		float sizeY = newTexture.getHeight();
		
		
		//Gdx.app.log(logstag,"______________text glow col is: "+teststyle.glowColour);
		//Gdx.app.log(logstag,"______________generating rect of "+LabelWidth+","+LabelHeight);
		
		//Note the *1 is the scale. We have scale 1 by default, duh.
		Model newModel = ModelMaker.createRectangle(0, 0, sizeX*1,sizeY*1, 0, mat); 

		
		backgroundAndCursorObject setupData = new backgroundAndCursorObject(newModel,0,0);
		
		
		return setupData;
		
		
	}


	static public TextureAndCursorObject generatePixmapExpandedToFit(String text, float sizeratio) {

		

		
	  //  BitmapFontData data = DefaultStyles.standdardFont.getData();

	    GlyphLayout layout = new GlyphLayout();	    

	    layout.setText(DefaultStyles.standdardFont, text);
	    
	    float currentWidth  = layout.width;
	    float currentHeight = layout.height;
	    

		Gdx.app.log(logstag,"______________predicted size = "+currentWidth+","+currentHeight);
		
		TextureAndCursorObject textureDAta = generateTexture( text, 0, 0,  sizeratio, true); //note zeros as size isn't used

		
		return textureDAta;

	}









	static public TextureAndCursorObject generateTextureNormal(String text,int TITLE_WIDTH,int TITLE_HEIGHT, float sizeratio) {

		TextureAndCursorObject textureDAta = generateTexture( text, TITLE_WIDTH, TITLE_HEIGHT,  sizeratio,false);
		
		return textureDAta;
	}

	static public TextureAndCursorObject generateTexture(String text,int DefaultWidth,int DefaultHeight, float sizeratio, boolean expandSizeToFit) {
		 
		PixmapAndCursorObject data = generatePixmap(text, DefaultWidth, DefaultHeight, sizeratio, expandSizeToFit);
					
		
		
		return new TextureAndCursorObject(new Texture(data.textureItself),data.Cursor.x,data.Cursor.y);
	}
	
	static public PixmapAndCursorObject generatePixmap(String text,int DefaultWidth,int DefaultHeight, float sizeratio, boolean expandSizeToFit) {


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
				textPixmap = sizePixmapTo(textPixmap, biggestX, biggestY);
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
	 * Sets the text and regenerates the texture (does not yet auto-update any generated models from this label!)
	 * Also doesn't remember cursor position. This is needed if we want to correctly ADD text to the texture in future, rather then recreating it all
	 * For animated text this optimization is pretty essential
	 **/
	public void setText(String text){
		this.contents=text;
		
		TextureAndCursorObject NewTexture = generateTexture(labelsSizeMode, contents); 
		

		Material infoBoxsMaterial = this.getMaterial("LabelMaterial");		
		//ColorAttribute ColorAttributestyle = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));	
		infoBoxsMaterial.set(  TextureAttribute.createDiffuse(NewTexture.textureItself));
		
		float x = NewTexture.textureItself.getWidth();
		float y = NewTexture.textureItself.getHeight();
		
		
		this.setSizeAs(x, y);
		
		
		
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

	static private TextureAndCursorObject generateTexture(SizeMode labelsSizeMode, String contents) {
		
		
		TextureAndCursorObject NewTexture;
		
		
		if (labelsSizeMode == SizeMode.ExpandToFitText){

			Gdx.app.log(logstag,"______________generating expand to fit text ");

			NewTexture = generatePixmapExpandedToFit(contents,1f); 


		} else {
			NewTexture = generateTextureNormal(contents,LabelNativeWidth, LabelNativeHeight,1f); 

		}
		
		return NewTexture;
	}

	public void firstTimeSetUp(){

		//	Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), true);
		//	texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		//testImage  = new Image(texture);
		//defaultFont = new BitmapFont(Gdx.files.internal("data/dfield.fnt"), new TextureRegion(texture), false);

	}



	/**
	 * Changes this objects rect mesh to the new specified size
	 * The internal texture will be stretched
	 * 
	 * @param newWidth
	 * @param newHeight
	 */
	public void setSizeAs(float newWidth,float newHeight){
		
		Mesh IconsMesh = labelInstance.meshes.get(0);
		
		final VertexAttribute posAttr = IconsMesh.getVertexAttribute(Usage.Position);
		final int offset = posAttr.offset / 4;
		final int numComponents = posAttr.numComponents;
		final int numVertices = IconsMesh.getNumVertices();
		final int vertexSize = IconsMesh.getVertexSize() / 4;

		final float[] vertices = new float[numVertices * vertexSize];
		IconsMesh.getVertices(vertices);
		int idx = offset;
		
		float hw =  newWidth/2;
		float hh = newHeight/2;
		
		float newSizeArray[] = new float[] { -hw,-hh,0,
											  hw,-hh,0,
											  hw,hh,0,
											 -hw,hh,0 };
				
		//can be optimized latter by pre-calcing the size ratio and just multiply
		for (int i = 0; i < 12; i=i+3) {
			
			//Gdx.app.log(logstag," new::"+comboX+","+comboY+","+comboZ);
			
			//currently just scale up a bit
			vertices[idx    ] = newSizeArray[i];
			vertices[idx + 1] = newSizeArray[i+1];
			vertices[idx + 2] = newSizeArray[i+2];
			
			idx += vertexSize;
		}
		
		
		IconsMesh.setVertices(vertices);
		
	}

	//
	//
	//--------------
	//Styleing functions below.
	//These are all subject to a lot of change
	//Especially as we
	//a) Try to make this Label extend ModelInstance
	//b) Try to make it as GWT-like as possible in its api

	/**
	 * sets the back color
	 * @param labelBackColor
	 */
	public void setLabelBackColor(Color labelBackColor) {
	
		Material infoBoxsMaterial = this.getMaterial("LabelMaterial");		
		//ColorAttribute ColorAttributestyle = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));
	
		infoBoxsMaterial.set( ColorAttribute.createDiffuse(labelBackColor));
		
	}

	
	
	public void setOpacity(float opacity){
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial("LabelMaterial");
		
		DistanceFieldAttribute style = ((DistanceFieldAttribute)infoBoxsMaterial.get(DistanceFieldAttribute.ID));
		
		//Gdx.app.log(logstag,"_____________current            col:"+style.textColour);
		//Gdx.app.log(logstag,"_____________current shadow     col:"+style.shadowColour);
		//Gdx.app.log(logstag,"_____________current glowColour col:"+style.glowColour);
		
	   style.outlineColour.a = opacity;
		  style.textColour.a = opacity;
		  style.glowColour.a = opacity;
		style.shadowColour.a = opacity;
		
		
	}
	
	
	
	

}
