package com.lostagain.nl.me.gui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.Management.GWTishModelManagement;
import com.lostagain.nl.me.features.ConceptObject;
import com.lostagain.nl.me.objects.DataObject;

/** handles the various semantic objects you can carry with, either for concept gun use
 * or as data to unclock locations **/
public class STMemory extends Table implements DataObjectDropTarget {
	

	//static Logger Log = Logger.getLogger("ME.TempMemory");
	final static String logstag = "ME.TempMemory";
	
	
	static HashSet<DataObject> carryingObjects = new HashSet<DataObject>();
	public static DataObject currentlyHeld;
	
	/** The object the player is holding Eventually this will replace the DataObject currentlyHeld above**/
	public static ConceptObject currentlyHeldNEW;

	/**
	 * An item that was just dropped
	 */
	public static ConceptObject justDropItem;
	
	
	int ItemLimit = 7; //can be expanded in future
	
	
	/** keeps track of the last time something is picked up.
	 * This is to help stop missclicks dropping items straight away*/
	private static long lastTime=0l;



	//public static SSSNode currentlyHeld;
	private float prefwidth;

	private float prefheight;
	
	
	public STMemory() {
		super();
		//super.pad(0);
		super.setName(DROPSPOTTYPENAME);// this name determains its a drop target (ie, objects can be dropped too it)
		super.setUserObject(this); //tells the object dropped where to fire the drop command (in this case its this very widget, but in some cases it might be different...ie, you drop it somewhere and it appears somewhere else)
		
		
		
		Gdx.app.log(logstag,"_________TempMemory_____");
		Color ColorM = new Color(Color.DARK_GRAY);
		ColorM.a=0.8f;
		
		super.setBackground(DefaultStyles.colors.newDrawable("white", ColorM));


		//LabelStyle back = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));
		
		//Color ColorM = new Color(Color.DARK_GRAY);
		//ColorM.a=0.5f;
		//back.background = DefaultStyles.colors.newDrawable("white", ColorM);

	//	Label backgroundClicker = new Label("",back);
		//super.add
		

		//super.debugAll();
		
		//Label test = new Label("test",DefaultStyles.linkstyle);
		super.top();
		/*
		DataObject test = new DataObject(StaticSSSNodes.Computer);		
		super.add(test).expandX();
		super.row();
		DataObject test2 = new DataObject(StaticSSSNodes.ability,"KPaqmyT");		
		super.add(test2).expandX();
		super.row();
		DataObject test3 = new DataObject(StaticSSSNodes.knows,"know");		
		super.add(test3);
		super.row();*/
		
		//set the width to one standard item wide
		setPrefWidth(DataObject.getStandardScaledWidth());		
		setPrefHeight(DataObject.getStandardScaledHeight()*ItemLimit); //it should fit ItemLimit number of items
		
		super.pack();
		super.setBounds(0, 0, super.getWidth(), super.getHeight());
		Gdx.app.log(logstag,"bounds__"+ super.getWidth()+","+ super.getHeight());
		
		
		
		
		this.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){

				Gdx.app.log(logstag,"clicked");
				
		}});
		
	}
	public void removeItem(DataObject dataobject)
	{
			
		carryingObjects.remove(dataobject);
		dataobject.remove();
		
	}
	


	
	protected static void setCurrentlyHeld(ConceptObject object) {
		
		currentlyHeldNEW=object;

		//Make sure its on overlay mode
		object.setAsOverlay(true);
		
	//	currentlyHeldNEW.setAsHitable(false); //no hitting while held;
		
		//temp we might want to truely attach it to the camera rather then switching the cursor image?
		//not sure which is better here theres many pros and cons to both
	//    MainExplorationView.setCursor(currentlyHeldNEW.getObjectsTexture());
	   ME.setCursorToHolding();
	    

			lastTime = TimeUtils.millis();
		
	}
	
	
	protected static void setCurrentlyHeld(DataObject object) {
		
		currentlyHeld=object;
		
		
		/*
		// 
		

		 
		// SpriteBatch batch = new SpriteBatch();
		 
		// ME.font.draw(batch, "test text", 5, 5);

		
		//the following should be catched so we dont need to keep regenerating the images
		
	    String Letters=itemsnode2.getPLabel();
	    
	    
			    int TILE_WIDTH=130;
			    int TILE_HEIGHT=30;
			    
				Pixmap textPixmap = new Pixmap(TILE_WIDTH, TILE_HEIGHT, Format.RGBA8888);
				textPixmap.setColor(0.44f, 0.44f, 0.44f, 1);
				
				textPixmap.fill();
				
		    // get the glyph info
		    BitmapFontData data = ME.font.getData();
		    Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0]));
		    
			// draw the character onto our base pixmap
		  
		  int totalwidth=0;
		  //precalcwidth
		    for (int i = 0; i < Letters.length(); i++) {
				
				Glyph glyph = data.getGlyph(Letters.charAt(i));
				
				totalwidth=totalwidth+ glyph.width+5;
			}
			
		    int currentX=0;
		    int xpad = ((TILE_WIDTH-totalwidth)/2);
		    
			for (int i = 0; i < Letters.length(); i++) {
				
				Glyph glyph = data.getGlyph(Letters.charAt(i));
				
				textPixmap.drawPixmap(
						fontPixmap,
						xpad+currentX,
						(TILE_HEIGHT - glyph.height) / 2, 
						glyph.srcX,
						glyph.srcY, glyph.width, glyph.height);
				
				currentX=currentX+ glyph.width+5;
			}
			
			// save this as a new texture
		    Texture texture = new Texture(currentlyHeld.getDrawable());
		    */
		   // MainExplorationView.setCursor(currentlyHeld.imagesTextureWithMipMaps);
		    ME.setCursorToHolding();
		    

		lastTime = TimeUtils.millis();
		
	}
	public static void dropHeldItem(){
		
		
		dropHeldItem(false);
		
	}
	
	/**
	 * should be run once per frame after a drop
	 * The list is just to assist concept slots which should collect drops if something is dropped over them
	 */
	public static void clearJustDropedList(){
		justDropItem=null;
	}
	
	public static void dropHeldItem(boolean overrideDelay){
		

		long LastHeld =  TimeUtils.timeSinceMillis(lastTime);
		
		//have a delay to ensure they dont drop it straight away by mistake
		if (LastHeld>500 || overrideDelay)
		{
			//set cursor to none
			ME.setCursorToDefault();
			if (currentlyHeld!=null){
				//dump on ground where cursor is
				dropItemToGround(currentlyHeld);			
			
				//remove currently held
				currentlyHeld=null;
			}
			
			//NEW drop function
			if (currentlyHeldNEW!=null){

				//Make sure its off overlay mode
				currentlyHeldNEW.setAsOverlay(false);
				//dump on ground where cursor is
				dropItemToGround(currentlyHeldNEW);			
				
				//set as clickable again (shouldnt be while held)
				//currentlyHeldNEW.setAsHitable(true);
				
				//add to just droped 
				Gdx.app.log(logstag,"added to just dropped variable");
				justDropItem = currentlyHeldNEW;
				
				//remove currently held
				currentlyHeldNEW=null;
			}
			
			

		
			
		}
		
	}
	
	 private static void dropItemToGround(DataObject item) {
		 
		 
		 //get cursor location
		 Vector2 cursor = ME.getCurrentStageCursorPosition();
		 
		 //drop the item on the ground
		 ME.addnewdrop(item, cursor.x, cursor.y);
		 
		 
		 
		
	}
	
	 private static void dropItemToGround(ConceptObject item) {
		 
		 
		 //get cursor location
		 Vector2 cursor = ME.getCurrentStageCursorPosition();
		 
		 //drop the item on the ground
		 ME.addnewdrop(item, cursor.x, cursor.y,0); //0 is ground
		 
		 
		 
		
	}
	
	public  boolean  addItem(DataObject dataobject){
		if (carryingObjects.size()<ItemLimit){
		
			carryingObjects.add(dataobject);
			super.add(dataobject);
			super.row();
		//	MainExplorationView.usersGUI.mySTMemory.setDownForABit();
			
			return true;
		} else {
			Gdx.app.log(logstag,"inventory full");
			return false;
		}
		
	}
	

	public void setPrefWidth(float width) {
		prefwidth=width;
		
	}
	public void setPrefHeight(float height) {
		prefheight=height;
		
	}

	@Override
	public float getPrefWidth() {
		return prefwidth;
		
		
	}
	@Override
	public float getPrefHeight() {
		return prefheight;
		
		
	}

	//is clicked while holding something, we attempt to add it to the temp memory
		public void clickedWhileHolding(){

			Gdx.app.log(logstag,"clickedWhileHolding");
			
			Boolean success = addItem(currentlyHeld);

			//if was successfully added we set currently held to nothing
			if (success){
					currentlyHeld = null;
			} else {
				
				//should have some feedback here for STMemory full up
				
			}
			
		}
		
		
		
		@Override
		public boolean onDrop(DataObject droppedOn) {
			Gdx.app.log(logstag,"adding to inventory if not full");
			boolean accepted = addItem(droppedOn);
			
			
			if (droppedOn==currentlyHeld && accepted){
				
				droppedOn.setRotation(0); //ensure its straight when going on
				droppedOn.setScale(1); //natural scale (might change in future)
				
				droppedOn.setStoredIn(this);
				Gdx.app.log(logstag, "removing from held");
				
				
				
				currentlyHeld = null;			
				

				ME.setCursorToDefault();
			
			}
	
			
			
			return accepted;
		}
		
		@Override
		public void onDrag(DataObject dataobject) {
			
			 removeItem(dataobject);
		}
		
		
		public static boolean wasHoldingItem(){
			if (justDropItem==null ){
				return false;
			} else {
				return true;
			}
		}
		
		public static boolean isHoldingItem(){
			if (currentlyHeld==null && currentlyHeldNEW ==null ){
				return false;
			} else {
				return true;
			}
		}
		
		
		
		
		public static ConceptObject getCurrentlyOrRecentlyHeld(){
			if (currentlyHeldNEW != null ){
				return currentlyHeldNEW;
			} else if (justDropItem != null ){
				return justDropItem;
			} else {
				return null;
			}
		}
		
		public static void clearCurrentlyHeld(){
			currentlyHeld=null;
			//set as clickable again (shouldn't be while held)
			if (currentlyHeldNEW!=null){
				currentlyHeldNEW.setAsHitable(true);
			}
			
			currentlyHeldNEW=null;
			

			ME.setCursorToDefault();
			
		}
		
		static public void holdItem(ConceptObject objectsnode) {
			if (currentlyHeldNEW==null){
				setCurrentlyHeld(objectsnode);
			} else {
				dropHeldItem(true);
			}
			
		}
		
		static public void holdItem(DataObject objectsnode) {
			if (currentlyHeld==null){
				setCurrentlyHeld(objectsnode);
			} else {
				dropHeldItem(true);
			}
			
		}
}
