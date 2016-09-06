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
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.GWTish.Style.TextAlign;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.shaders.DistanceFieldShader;
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute;
import com.lostagain.nl.shaders.GwtishWidgetDistanceFieldAttribute;
import com.lostagain.nl.shaders.GwtishWidgetDistanceFieldAttribute.TextScalingMode;

/**
 * A Libgdx label that will eventually emulate most of the features of a GWT label (ish. VERY ish.)<br>
 * <br>
 * The most significant thing here though is we enable it to use distance mapped fonts in a 3d view. <br>
 * This lets things look sharp regardless of how close the camera gets.<br>
 * <br>
 * For details of how this works see;<br>
 * <br>
 * https://github.com/libgdx/libgdx/wiki/Distance-field-fonts<br>
 * <br>
 * and the original Valve paper;<br>
 * <br>
 * http://www.valvesoftware.com/publications/2007/SIGGRAPH2007_AlphaTestedMagnification.pdf<br>
 * <br>
 * <br> * 
 * With the DistanceFieldShader we can also emulate shadows,outlines and glows - sort of letting the label have "css styles" <br>
 * These are controlled in the objects style object, accessible with functions like .getStyle().setTextGlow(..)<br>
 *
 ***/
public class Label extends LabelBase {

	final static String logstag = "ME.Label";

	/**
	 * The name of the label material
	 */
	public static final String LABEL_MATERIAL = "LabelMaterial";

	/**
	 * contents of this text label;
	 */
	String contents = "TextNotSetError";


	//static int LabelNativeWidth =512;
	//static int LabelNativeHeight=512;



	/**
	 * flags if we need to do any initial setup work or not.
	 * (might be unnecessary)
	 */
	static Boolean labelSetupDone = false;

	//defaults
	BitmapFont defaultFont;

	/** default scale factor of the text **/
	float ModelScale = 1.0f;

	enum SizeMode {
		/** label is a fixed, specified size and everything is scaled to fit.
		 *  Padding does not increase size. **/
		Fixed,
		/** label expands till it contains the text **/
		ExpandXYToFit,
		/**
		 * Expands variably with new lines, but wraps to the width.
		 * This is somewhat like a HTML DIV with a style width specified
		 */
		ExpandHeightMaxWidth,

	}

	SizeMode labelsSizeMode = SizeMode.ExpandXYToFit;


	/**
	 * under fixed width mode this marks the maximum width of the widget. Any expansion of the text beyond it will result
	 * in word wrapping.
	 * Under fixed mode this will be the size regardless of its its all needed or not.
	 */
	float maxWidth = -1; //default for no max
	/**
	 * Not used unless we are on fixed mode, then its the height.
	 */
	float maxHeight = -1; //default for no max

	/**
	 * The text alignment used to last generate the text texture
	 */
	private TextAlign lastUsedTextAlignment;


	//Texture currentTexture = null;
	//boolean modelNeedsUpdate = true;

	//Style data (mostly controlled by shader)
	//	static private Color defaultBackColour = Color.CLEAR;

	/**
	 * 
	 * @param contents
	 */
	public Label (String contents){ 
		this(contents, -1,-1, SizeMode.ExpandXYToFit, MODELALIGNMENT.TOPLEFT,TextAlign.LEFT); //defaults to top left alignment of pivot with no max width
	}
	/**
	 * 
	 * @param contents
	 * @param MaxWidth
	 */
	public Label (String contents,float MaxWidth){ 
		this( contents, MaxWidth,-1, SizeMode.ExpandHeightMaxWidth, MODELALIGNMENT.TOPLEFT,TextAlign.LEFT); //defaults to top left alignment of pivot
	}

	/**
	 * fixed size label - texture scales into it (non-gwt like)
	 * @param contents
	 * @param Width
	 * @param Height
	 * @param modelalignment
	 * @param textalign
	 */
	public Label(String contents, float Width, float Height, MODELALIGNMENT modelalignment, TextAlign textalign) {
		this( contents, Width, Height, SizeMode.Fixed, modelalignment,textalign); //defaults to top left alignment of pivot
	}
	/**
	 * * fixed size label - texture scales into it (non-gwt like)
	 * @param contents
	 * @param MaxWidth
	 * @param MaxHeight
	 */
	public Label (String contents,float MaxWidth,float MaxHeight){ 
		this( contents, MaxWidth,MaxHeight, SizeMode.Fixed, MODELALIGNMENT.TOPLEFT,TextAlign.LEFT); //defaults to top left alignment of pivot
	}

	/**
	 * 
	 * @param contents
	 * @param MaxWidth
	 *  @param MaxHeight
	 */
	public Label (String contents,float Width,float Height, MODELALIGNMENT alignment){ 
		this( contents, Width,Height, SizeMode.Fixed, alignment,TextAlign.LEFT); 
	}

	/**
	 * 
	 * @param contents
	 * @param MaxWidth
	 */
	public Label (String contents,float MaxWidth,MODELALIGNMENT alignment){ 
		this( contents, MaxWidth,-1, SizeMode.ExpandHeightMaxWidth, alignment,TextAlign.LEFT); 
	}
	/**
	 * 
	 * @param contents
	 * @param MaxWidth
	 * @param modelAlignement
	 */
	public Label (String contents,float MaxWidth,float MaxHeight, SizeMode sizeMode, MODELALIGNMENT modelAlignement, TextAlign textAlignment){ 

		super(generateObjectData(true, true, contents, sizeMode, MaxWidth, MaxHeight, modelAlignement,textAlignment,null));
		super.setStyle(getMaterial(LABEL_MATERIAL)); //no style settings will work before this is set

		this.lastUsedTextAlignment = textAlignment;
		this.userData="label_"+contents;


		if (!labelSetupDone){
			firstTimeSetUp();
			labelSetupDone=true;
		}


		labelsSizeMode = sizeMode;
		this.maxWidth = MaxWidth;
		this.maxHeight = MaxHeight;
		this.contents=contents;

		Material materialAccordingToStyle = this.getStyle().getMaterial();
		Material materialAccordingToGetMaterial = getMaterial(LABEL_MATERIAL);

		//debug checks
		if (materialAccordingToStyle!=materialAccordingToGetMaterial){
			Gdx.app.log(logstag, "materials dont match!"); 
		}
		if (materialAccordingToStyle==null){
			Gdx.app.log(logstag, "materialAccordingToStyle is null"); 
		}
		if (materialAccordingToGetMaterial==null){
			Gdx.app.log(logstag, "materialAccordingToGetMaterial is null"); 
		}
		//--------------------------------------------------------

		GwtishWidgetDistanceFieldAttribute matttest = (GwtishWidgetDistanceFieldAttribute) this.getStyle().getMaterial().get(GwtishWidgetDistanceFieldAttribute.ID);

		Gdx.app.log(logstag, "fitarea set as:"+matttest.textScaleingMode); 
		Gdx.app.log(logstag, "paddingLeft:   "+matttest.paddingLeft); 

		if (sizeMode==SizeMode.Fixed){
			//calc needed shader scaleing. Fixed size mode enlarges and shrinks texture to fit model - but does so in the shader, not by changing the underlying texture resolution
			//(which is pointless - as you could only ever lose information and make it look worse.)
			calculateCorrectShaderTextScale();

		//old;
		//if fixed mode we might need to pad the texture to ensure it keeps its ratio			
		//setPaddingToPreserveTextRatio(TextAlign.LEFT,  maxWidth , maxHeight, this.textureSize.x, this.textureSize.y);

		}

	}


	/*
	 * Generates a label with the specified contents, auto expanding its size to fit
	 * Newlines in the content are respected 
	 * 
	 * @param contents
	 *
	public Label (String contents){
		super(generateObjectData(true, true, contents, SizeMode.ExpandXYToFit,-1));//No max width

		super.setStyle(this.getMaterial(LABEL_MATERIAL));

		labelsSizeMode = SizeMode.ExpandXYToFit;
		this.maxWidth = -1;
		this.contents=contents;

		if (!labelSetupDone){
			firstTimeSetUp();
			labelSetupDone=true;
		}


	}
	 */




	/**
	 * The object data needed on creation is just the background mesh instance and the cursor position.
	 * This shouldn't need to be run outside the objects first creation.
	 * After its created everything should eventually be alterable separately without full recreation
	 * 
	 * @param regenTexture - probably can be removed
	 * @param regenMaterial
	 * @param contents
	 * @param labelsSizeMode
	 * @param maxWidth
	 * @param alignment - the meshs pivot alignment
	 * @param textAlignment 
	 * @return
	 */
	private static backgroundAndCursorObject generateObjectData(boolean regenTexture,boolean regenMaterial,String contents,SizeMode labelsSizeMode, float maxWidth ,float maxHeight, MODELALIGNMENT alignment, TextAlign textAlignment,Style style) {
		TextureAndCursorObject textureData = null;

		if (regenTexture){			
			textureData = generateTexture(labelsSizeMode, contents,maxWidth,textAlignment,style); //left default			
		}

		Texture newTexture = textureData.textureItself;

		newTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);//MipMapLinearNearest does not work with DistanceField shaders

		//DistanceFieldAttribute textStyle = null;
		//GwtishWidgetDistanceFieldAttribute textStyle = null;

		//if (textStyle==null){
		//	textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);
		GwtishWidgetDistanceFieldAttribute textStyle = new GwtishWidgetDistanceFieldAttribute(GwtishWidgetDistanceFieldAttribute.presetTextStyle.whiteWithShadow);

		//}


		//we normally get the model size from the generated material unless its specified as fixed
		float textureSizeX = newTexture.getWidth();
		float textureSizeY = newTexture.getHeight();
		float SizeX = textureSizeX;
		float SizeY = textureSizeY;

		if (labelsSizeMode==SizeMode.Fixed){

			SizeX = maxWidth;			
			SizeY = maxHeight;


			textStyle.setTextScaleing(TextScalingMode.fitPreserveRatio); 

			
			
			Gdx.app.log(logstag, "fitarea detected texture size mode set as:"+textStyle.textScaleingMode); 
			//setPaddingToPreserveTextRatio(TextAlign.LEFT,  maxWidth , maxHeight, sizeX, sizeY);

		}


		Material mat = 	
				new Material(LABEL_MATERIAL,
						new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1.0f),
						TextureAttribute.createDiffuse(newTexture),
						//	ColorAttribute.createDiffuse(defaultBackColour), //needs to be passed into this function
						textStyle);


		GwtishWidgetDistanceFieldAttribute matttest = (GwtishWidgetDistanceFieldAttribute) mat.get(GwtishWidgetDistanceFieldAttribute.ID);



		//Gdx.app.log(logstag,"______________text glow col is: "+teststyle.glowColour);
		//Gdx.app.log(logstag,"______________generating rect of "+LabelWidth+","+LabelHeight);

		//Note the *1 is the scale. We have scale 1 by default, duh.
		Model newModel = Widget.generateBackground(SizeX, SizeY, mat, alignment);

		GwtishWidgetDistanceFieldAttribute matttest2 = (GwtishWidgetDistanceFieldAttribute) newModel.getMaterial(LABEL_MATERIAL).get(GwtishWidgetDistanceFieldAttribute.ID);

		Gdx.app.log(logstag, "1fitarea set as:"+matttest2.textScaleingMode); 
		Gdx.app.log(logstag, "paddingLeft:   "+matttest2.paddingLeft); 

		//ModelMaker.createRectangle(0, 0, sizeX*1,sizeY*1, 0, mat); 


		backgroundAndCursorObject setupData = new backgroundAndCursorObject(newModel,0,0,new Vector2(textureSizeX,textureSizeY));


		return setupData;


	}


	static public TextureAndCursorObject generatePixmapExpandedToFit(String text, float sizeratio, float maxWidth,TextAlign align, Style stylesettings) {

		//  BitmapFontData data = DefaultStyles.standdardFont.getData();

		GlyphLayout layout = new GlyphLayout();	    
		//layout.setText(DefaultStyles.standdardFont, text);

		BitmapFont font = DefaultStyles.standdardFont;

		if (stylesettings!=null){

			//make a copy of the font so we can customize the data
			//probably not very efficient?
			font = new BitmapFont(DefaultStyles.standdardFont.getData(),
								DefaultStyles.standdardFont.getRegion(), 
								DefaultStyles.standdardFont.usesIntegerPositions());

			float LineHeight = (float) stylesettings.getLineHeightValue();
			Style.Unit LineHeightUnit = stylesettings.getLineHeightUnit();

			//we only support PX at the moment		
			if (LineHeightUnit == Style.Unit.PX){
				font.getData().setLineHeight(LineHeight);
			}


		}
		
		///font.getData().setScale(3.5f); //scaling only effects spaceing, not font size
		

		//if maxWidth is zero or -1 then we dynamically work it out instead
		if (maxWidth<1){
			layout.setText(font, text);
			maxWidth = layout.width;
		}	    

		Gdx.app.log(logstag,text+"__"+text+"_layout width:"+maxWidth);
		Gdx.app.log(logstag,text+"___layout line height:"+font.getLineHeight());

		//convert from text align to layout align
		int layoutAlignment = Align.center;

		switch (align) {
		case CENTER:
			layoutAlignment = Align.center;
			break;
		case JUSTIFY:		    
			Gdx.app.log(logstag,"___JUSTIFY NOT SUPPORTED. DEFAULTING TO CENTER");
			layoutAlignment = Align.center;
			break;
		case LEFT:
			layoutAlignment = Align.left;
			break;
		case RIGHT:
			layoutAlignment = Align.right;
			break;
		default:
			layoutAlignment = Align.center;
			break;
		}



		layout.setText(font, text, Color.BLACK, maxWidth, layoutAlignment, true); //can't centralize without width
		TextureAndCursorObject textureDAta = generateTexture_fromLayout(layout, font); 

		//Font size  trying to figure out
		
		//Note; in order to scale text to fit in other modes we still render at the native size, but dont effect the mesh size
		//the texture will then auto-scale into the space. 
		//
		//However...this "solution" doesn't deal with maxwidth because the width of the characters determines how many per line can fit.
		//So this might take some thinking about.
		//a) there should be a minimum texture pixel size per character, regardless of final visual size
		//b) for the wrapping to work, however, at least the correct ratio needs to end up in the layout?
		//ii) or maybe we change the width proportionally the other way? (then when its sized up to the widget size it will be correct? err...seems weird but should work? )
		//
		
		
		
		
		
		//old;


		//   float currentWidth  = layout.width;
		//   float currentHeight = layout.height;
		/*
	    for (GlyphRun grun : layout.runs) {

	    	Gdx.app.log(logstag,"______________run width:"+grun.width+" at "+grun.y);	
	    	String runstring = "";

	    	for (Glyph g : grun.glyphs) {

	    	//	Gdx.app.log(logstag,"___g:"+g.toString());
	    		runstring=runstring+g.toString();

			}
	    	Gdx.app.log(logstag,"___runstring:"+runstring);



		}
		 */

		//	Gdx.app.log(logstag,"______________predicted size = "+currentWidth+","+currentHeight);

		//	TextureAndCursorObject textureDAta = generateTexture( text, 0, 0,  sizeratio, true,maxWidth); //note zeros as size isn't used




		return textureDAta;

	}



	/**
	 * new method using the layout function to give us the data needed to...well..layout the glyphs
	 * @param layout
	 * @param standdardFont - should match the one used to generate the layout
	 * @return
	 **/
	static public TextureAndCursorObject generateTexture_fromLayout(GlyphLayout layout, BitmapFont standdardFont){

		//create according to predicted size (in future add padding option to texture?)
		int currentWidth  = (int) layout.width;
		int currentHeight = (int) (layout.height+standdardFont.getCapHeight()); //not sure if cap  height is correct

		Pixmap textPixmap = new Pixmap(currentWidth, currentHeight, Format.RGBA8888);

		BitmapFontData data = standdardFont.getData();  //need optional font too, should match whats used in layout
		Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0])); //as pixmap

		//now loop over each run of letters. 
		for (GlyphRun grun : layout.runs) {

			String runstring = "";
			float currentRunX=0;
			//now draw each letter
			Gdx.app.log(logstag,"_________grun="+grun.x+","+grun.y+" ");
			int i =0;
			for (Glyph glyph : grun.glyphs) {

				float advance = grun.xAdvances.get(i);
				i++;
				currentRunX=currentRunX   +   advance    ; //1 should not be needed

				textPixmap.drawPixmap(
						fontPixmap,
						glyph.srcX,
						glyph.srcY, 
						glyph.width, 
						glyph.height,
						(int)grun.x + glyph.xoffset + (int)currentRunX,
						(int)grun.y + glyph.yoffset,//+(TILE_HEIGHT - (cheight)) / 2,						
						glyph.width, 
						glyph.height);

				// 	Gdx.app.log(logstag,"___ "+glyph.toString()+" glyph.xadvance:"+glyph.xadvance+" w:"+glyph.width);	

				//	Gdx.app.log(logstag,"___g:"+g.toString());
				runstring=runstring+glyph.toString();

			}
			if (grun.glyphs.size>0){
				float advance = grun.xAdvances.get(i);
				i++;
				currentRunX=currentRunX+advance;
				Gdx.app.log(logstag,"______________last run width:"+grun.width+" drawn was till "+currentRunX);	
			}
			//Gdx.app.log(logstag,"___runstring drawen:"+runstring);



		}

		PixmapAndCursorObject pixmapAndCursor = new PixmapAndCursorObject(textPixmap, currentWidth, currentHeight);


		return new TextureAndCursorObject(new Texture(pixmapAndCursor.textureItself),pixmapAndCursor.Cursor.x,pixmapAndCursor.Cursor.y);


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


		regenerateTexture(text);





	}

	private void regenerateTexture(String text) {
		TextAlign align = this.getStyle().getTextAlignment();
		lastUsedTextAlignment = align;

		//note; we subtract the left/right padding from maxwidth if we are on fixed size mode
		float effectiveMaxWidth = maxWidth;
		if (maxWidth!=-1){
			effectiveMaxWidth = maxWidth - (this.getStyle().getPaddingLeft() + this.getStyle().getPaddingRight());
		}

		TextureAndCursorObject textureAndData = generateTexture(labelsSizeMode, contents,effectiveMaxWidth,align,this.getStyle()); //-1 is the default max width which means "any size"


		Material infoBoxsMaterial = this.getMaterial(LABEL_MATERIAL);	

		Texture newTexture = textureAndData.textureItself;

		infoBoxsMaterial.set(TextureAttribute.createDiffuse(newTexture));


		//if (textStyle==null){
		//textStyle = new DistanceFieldShader.DistanceFieldAttribute(DistanceFieldAttribute.presetTextStyle.whiteWithShadow);

		//ColorAttribute ColorAttributestyle = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));	
		//  TextureAttribute.createDiffuse(NewTexture.textureItself)	,	
		// ColorAttribute.createDiffuse(defaultBackColour)


		float x = textureAndData.textureItself.getWidth();
		float y = textureAndData.textureItself.getHeight();
		//boolean autoPadToPreserveRatio = true;
		Gdx.app.log(logstag,"_________setting text to;"+text+" size:"+x+","+y);

		switch (labelsSizeMode) {
		case ExpandHeightMaxWidth:
			this.setSizeAs(x, y); //width should be locked thanks to generateTexture wrapping, but we could insert a test here to be sure? Or only set Y?
			break;
		case ExpandXYToFit:
			this.setSizeAs(x, y); 
			break;
		case Fixed:			
			//real size should only set if not on fixed size mode. However, we do want to effect the padding as the real widget ratio might not match the text texture, so we need to pad the widget to compansate
			//setPaddingToPreserveTextRatio(align, maxWidth, maxHeight, x, y);

			break;
		}
	}

	/*
	//now handled in shader
	private void setPaddingToPreserveTextRatio(TextAlign align, float maxWidth , float maxHeight, float textureSizeX, float textureSizeY) {

		boolean autoPadToPreserveRatio = true;

		if (autoPadToPreserveRatio){

			Gdx.app.log(logstag,"____(using padding to correct aspect ratio of text. Raw texture size is:"+textureSizeX+","+textureSizeY+")__");
			Gdx.app.log(logstag,"____(using padding to correct aspect ratio of text. fixed size is:"+maxWidth+","+maxHeight+")__");

			//we need to work out which dimension needs to be scaled down more to fit 
			float diffX = maxWidth  - textureSizeX;
			float diffY = maxHeight - textureSizeY;

			//ie
			//200 - 50
			//50 - 40gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggcdddddddddddddddddddddddddddddddddddxggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg < - THIS line contributed by a visiting cat
			//+150,+10
			float newX =0;
			float newY =0;
			boolean textureSmallerInX = false;
			boolean textureSmallerInY = false;

			if (diffX>0){
				//texture size is smaller then width in X
				textureSmallerInX = true;
			}
			if (diffY>0){
				//texture size is smaller then width in X
				textureSmallerInY = true;
			}

			//if they are both smaller
			if (textureSmallerInX && textureSmallerInY){
				Gdx.app.log(logstag,"____(both dimensions are smaller)__");
				//find the smaller difference and set that equal to the limit
				if (diffX<diffY){
					float textureSizeRatio = textureSizeY/textureSizeX;
					Gdx.app.log(logstag,"______y needs more padding, ratio to x is"+textureSizeRatio);
					 newX = maxWidth;
					 newY = textureSizeY*textureSizeRatio;
				} else {
					float textureSizeRatio = textureSizeX/textureSizeY;
					Gdx.app.log(logstag,"______x needs more padding, ratio to y is"+textureSizeRatio);
					 newX = textureSizeX*textureSizeRatio;
					 newY = maxHeight;
				}
			}


			//which is the bigger difference?
			/**
			if (diffX>diffY){
				//x needs more shrinking
				float textureSizeRatio = textureSizeY/textureSizeX;
				Gdx.app.log(logstag,"______x needs more shrinking, ratio to y is"+textureSizeRatio);
				 newX = maxWidth;
				 newY = textureSizeY*textureSizeRatio;

			} else {
				//y needs more				
				float textureSizeRatio = textureSizeX/textureSizeY;
				Gdx.app.log(logstag,"______y needs more shrinking, ratio to x is"+textureSizeRatio);
				 newX = textureSizeX*textureSizeRatio;
				 newY = maxHeight;
			}/

			float paddingX = maxWidth  - newX;
			float paddingY = maxHeight - newY;


			Gdx.app.log(logstag,"______"+this.contents+"____paddingX="+paddingX+" paddingY="+paddingY);

			//this becomes the padding, and its applied based on alignment
			if (align == TextAlign.CENTER){

				Gdx.app.log(logstag,"__________setting padding both sides="+(paddingX/2));
				this.getStyle().setPaddingLeft(paddingX/2);
				this.getStyle().setPaddingRight(paddingX/2);
			}
			if (align == TextAlign.LEFT){

				Gdx.app.log(logstag,"__________padding on right="+paddingX);
				this.getStyle().setPaddingRight(paddingX);
			}
			if (align == TextAlign.RIGHT){                             
				this.getStyle().setPaddingLeft(paddingX);
			}                                                                                  



		}
	}
	 */

	/*
	/**
	 * A scaleing factor that will enlarge of shrink the text relative to the standard font size.
	 * NOTE: this does not scale the internal texture size. As we are using a distance field font, it should look sharp at all distances anyway.
	 * Scaleing would not help.
	 * @param text

	public void setTextScale(float scale){
		ModelScale = scale;

		//currentTexture  =null; //null tells it to regenerate
		//modelNeedsUpdate=true;

	}
	 */

	/**
	 * 
	 * @param labelsSizeMode
	 * @param contents
	 * @param maxWidth
	 * @param align
	 * @param style
	 * 
	 * @return
	 */
	static private TextureAndCursorObject generateTexture(SizeMode labelsSizeMode, String contents, float maxWidth,TextAlign align, Style style) {



		TextureAndCursorObject NewTexture = null;


		float sizeRatio = 1f; // was going to be used for font size, but we dont really want to achieve that by shrinking the texture DO WE? 
		//HMMM.....we do need some indication of size though to get the correct wrapping points on maxWidth
		

		switch (labelsSizeMode) {
		case ExpandHeightMaxWidth:
			NewTexture = generatePixmapExpandedToFit(contents,1f,maxWidth,align,style); //-1 = no max width
			break;
		case Fixed:			
			//Note; the textures internal size is not related to the widgets size directly, but it needs to know
			//the final width/height ratio in order to pad the Pixmap enough to preserve its ratio.
			NewTexture = generatePixmapExpandedToFit(contents,1f,-1,align,style); //-1 = no max width
			break;
			//expand to fit is also the default
		case ExpandXYToFit:
		default:
			Gdx.app.log(logstag,"______________generating expand to fit text ");
			NewTexture = generatePixmapExpandedToFit(contents,1f,-1,align,style); //-1 = no max width
			break;

		}

		NewTexture.textureItself.setFilter(TextureFilter.Linear, TextureFilter.Linear);//ensure mipmaping is disabled, else distance field shaders wont work
		//NewTexture.textureItself.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);//ensure mipmaping is disabled, else distance field shaders wont work
		return NewTexture;
	}

	//None needed right now
	static public void firstTimeSetUp(){
		//None needed right now

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
		//Material infoBoxsMaterial = this.getMaterial(LABEL_MATERIAL);		
		//ColorAttribute ColorAttributestyle = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));

		//infoBoxsMaterial.set( ColorAttribute.createDiffuse(labelBackColor));


		super.getStyle().setBackgroundColor(labelBackColor);

	}

	/**
	 *   Gets this object's text.
	 * @return
	 */
	public String getText() {		
		return this.contents;
	}


	public Material getTextMaterial(){
		return this.getMaterial(LABEL_MATERIAL);
	}


	@Override
	public void setOpacity(float opacity){


		super.setOpacity(opacity);


		/*
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial(LABEL_MATERIAL);
		GwtishWidgetDistanceFieldAttribute style = ((GwtishWidgetDistanceFieldAttribute)infoBoxsMaterial.get(GwtishWidgetDistanceFieldAttribute.ID));
		style.setOverall_Opacity_Multiplier(opacity);

		//Gdx.app.log(logstag,"_____________current            col:"+style.textColour);
		//Gdx.app.log(logstag,"_____________current shadow     col:"+style.shadowColour);
		//Gdx.app.log(logstag,"_____________current glowColour col:"+style.glowColour);



		//ColorAttribute background = ((ColorAttribute)infoBoxsMaterial.get(ColorAttribute.Diffuse));
		//background.color.a = opacity;
		BlendingAttribute backgroundOpacity = ((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type));
		backgroundOpacity.opacity = opacity;
		//	Gdx.app.log(logstag,"_____________opacity:"+opacity);
		 */
	}

	public void setMaxWidth(float maxWidth) {
		this.maxWidth = maxWidth;
		labelsSizeMode = SizeMode.ExpandHeightMaxWidth;
	}

	//regenerates the texture if the style layout is changed (ie, text alignment or padding)
	@Override
	public void layoutStyleChanged() {
		super.layoutStyleChanged();

		//If the padding has changed and we are on fixed width mode, we need to regenerate
		//the texture to ensure its still contained within the widget

		///if (this.getStyle().getTextAlignment() != lastUsedTextAlignment){
		regenerateTexture(contents);

		//}

		//work out a appropriate text scale for the shader so it fits
		calculateCorrectShaderTextScale();


		//the shader takes care of text positioning, but not sizing, so we need to recalculate sizes based on mode.
		/*
		float newTotalWidth  = -1;
		float newTotalHeight = -1;	
		float newTextWidth  = -1;
		float newTextHeight = -1;	


		//different behavior based on sizemode
		switch (this.labelsSizeMode){
		case ExpandHeightMaxWidth:


			break;
		case ExpandXYToFit:


			break;
		case Fixed:
			//same totals as before
			newTotalWidth  = maxWidth;
			newTotalHeight = maxHeight;

			//new text size is the total size, minus all the padding		
			newTextWidth  = newTotalWidth  - (getStyle().getPaddingLeft()+getStyle().getPaddingRight());
			newTextHeight =	newTotalHeight - (getStyle().getPaddingTop()+getStyle().getPaddingBottom());

			//regenerate texture
			regenerateTexture(contents);

			break;
		default:
			break;

		}
		/**
		 * For fixed size labels;

Size is still w/h
Label size is (w-(left+right), h-(top+bottom)

For dynamic sized labels

Label size is  w+(leftpadding+rightpadding), h+(top+bottom)
Text size remains just h/w, however


		 */

		//		Gdx.app.log(logstag," size now::"+this.getWidth()+","+this.getHeight());


	}
	/**
	 * work out a appropriate text scale for the shader so it fits
	 * This is most important for fixedSize mode, where the text scales to the widget		
	 */
	private void calculateCorrectShaderTextScale() {
		float widgetWidth  = this.getWidth();
		float widgetHeight = this.getHeight();
		float totalPaddingWidth =  (getStyle().getPaddingLeft()+getStyle().getPaddingRight());
		if (totalPaddingWidth>widgetWidth){
			totalPaddingWidth=widgetWidth;
		}
		float totalPaddingHeight =  (getStyle().getPaddingTop()+getStyle().getPaddingBottom());
		if (totalPaddingHeight>widgetHeight){
			totalPaddingHeight=widgetHeight;
		}
		float textScale = Math.min(( widgetWidth-totalPaddingWidth)/textureSize.x, (widgetHeight-totalPaddingHeight)/textureSize.y); 

		this.getStyle().setTextScale(textScale);
	}








}
