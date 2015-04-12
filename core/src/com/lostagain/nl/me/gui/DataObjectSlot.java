package com.lostagain.nl.me.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.objects.DataObject;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;


/** a place to drop dataobjects onto.
 * If accepted they can be slotted into place till they are picked up again **/
public class DataObjectSlot  extends WidgetGroup implements DataObjectDropTarget  {

	final static String logstag = "ME.DataObjectDropSpot";

//	public static final String DROPSPOTTYPENAME = "DataObjectDropSpot";
	
	
	
	/** Determines if it takes drops **/
	boolean slotEnabled = true;
	
	/** Determines if the dataobject is locked in **/
	boolean lockEnabled = false;
	
	
    static int TITLE_WIDTH  = DataObject.getStandardWidth();    
    static int TITLE_HEIGHT = DataObject.getStandardHeight();    
    static float SCALE     =  DataObject.getStandardScale();
        
    static int PADDING_X   =3;
    static int PADDING_Y   =3;
    static int ImageBorder = 3;

	public Texture imagesTextureWithMipMaps;
	public Texture overlayTexture;
	
	DataObject stored;
	
	/** not only  the background object, but also the clickcatcher **/
	Image background;

	/** overlay for when its locked **/
	Image lockedOverlay;
	
	private OnDropRunnable runAfterSomethingDroppedOn;

	private Runnable onDragRunnable; 
	
	
	public DataObjectSlot(){
		
		
		LabelStyle back = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));		
		Color ColorM = new Color(Color.GREEN);
		ColorM.a=0.8f;
		back.background = DefaultStyles.colors.newDrawable("white", ColorM);
		this.setColor(ColorM);
		
		this.setSize(getPrefWidth(), getPrefHeight());

		
		setupApperance();
		
		//setup click detection

		super.debugAll();
		super.pack();
		super.setBounds(0, 0, super.getWidth(), super.getHeight());
		Gdx.app.log(logstag,"bounds__"+ super.getWidth()+","+ super.getHeight());
		/*
		Target testtarget = new Target(this) {
			
			@Override
			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				
				Gdx.app.log(logstag,"Accepted: " + payload.getObject() + " " + x + ", " + y);		
				
			}
			
			@Override
			public boolean drag(Source source, Payload payload, float x, float y,
					int pointer) {
				
				Gdx.app.log(logstag,"__hjkhjjjjjjjjjjjjjjjhe to new line");		
				
				return true;
			}
		};
		
		if (	MainExplorationView.usersGUI!=null){
		MainExplorationView.usersGUI.dragAndDrop.addTarget(testtarget);
		}
		
		/*
		super.addListener(new InputListener() {
		 	@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		 		Gdx.app.log(logstag, "___________________________________________________________DataObjectDropSpot clicked");
				if (Old_Inventory.currentlyHeld!=null){

					Gdx.app.log(logstag, "detected object");
					
					onDrop(Old_Inventory.currentlyHeld);
					
					
				}
		 	}
		 });
*/
		
		
		
		//for dragging items of this spot (spot disables clicks on itemswhen its stored)
		 super.addListener(new InputListener() {
			 
			 	@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			 		
			 		if (stored!=null && !lockEnabled){
			 			stored.setTouchable(Touchable.enabled);
			 			stored.triggerPickedUp();
			 		
			 			
			 		}	else {
			 			Gdx.app.log(logstag," no stored object or locked");
			 		}
					
					return true;
					
			 	}
			 	
			 	@Override
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
			 		
			 		if (STMemory.currentlyHeld!=null)
			 				{

						 Gdx.app.log(logstag,"_________________: Object dropped from slot "+x+","+y);
						 STMemory.currentlyHeld.droppedAt(x, y);
			 				}
			 		 
					
			 	}
			 	
			 });
		 
		
	}
	
	 
	public float getPrefWidth() {
		return TITLE_WIDTH;
	}
	
	public float getPrefHeight() {
		return TITLE_HEIGHT;
	}
	/** for now, its just a outlined box **/
	private void setupApperance() {
		
			//the following should be cached so we don't need to keep regenerating the images
			
		    
		    Pixmap borderMap = new Pixmap(TITLE_WIDTH, TITLE_HEIGHT, Format.RGBA8888); //imagesTexture.getTextureData().consumePixmap();
		    
					borderMap.setColor(0.3f, 0.2f, 0.6f, 0.1f);					
					borderMap.fill();
					borderMap.setColor(0, 1, 1, 1);					
					borderMap.drawRectangle(0, 0, TITLE_WIDTH, TITLE_HEIGHT);
					borderMap.setColor(0, 0.6f, 1, 1);					
					borderMap.drawRectangle(1, 1, TITLE_WIDTH-2, TITLE_HEIGHT-2);
					borderMap.setColor(0, 0.3f, 1, 0.7f);					
					borderMap.drawRectangle(2, 2, TITLE_WIDTH-4, TITLE_HEIGHT-4);
					borderMap.setColor(0, 0.1f, 1, 0.5f);					
					borderMap.drawRectangle(3, 3, TITLE_WIDTH-6, TITLE_HEIGHT-6);
			
					
				imagesTextureWithMipMaps = new Texture(borderMap,true);
				imagesTextureWithMipMaps.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
				
				background = new Image(imagesTextureWithMipMaps);
				background.setPosition(0, 0, Align.bottomLeft);
				
				super.addActor(background);
				
				background.setUserObject(this);			
				background.setName(DROPSPOTTYPENAME);				
				super.setTouchable(Touchable.enabled);	
				
				//-------------------------
				 Pixmap lockedMap = new Pixmap(TITLE_WIDTH, TITLE_HEIGHT, Format.RGBA8888); //imagesTexture.getTextureData().consumePixmap();
				    
				 lockedMap.setColor(0.7f, 0.2f, 0.2f, 0.3f);					
				 lockedMap.fill();
				 lockedMap.setColor(0.7f, 0.2f, 0.2f,1f);
				 lockedMap.drawLine(0, TITLE_HEIGHT, TITLE_WIDTH,0 );	
				 lockedMap.drawLine(0, 0, TITLE_WIDTH, TITLE_HEIGHT);	
				 
				 lockedMap.drawLine(1, TITLE_HEIGHT, TITLE_WIDTH+1,0 );	
				 lockedMap.drawLine(1, 0, TITLE_WIDTH+1, TITLE_HEIGHT);	
				 
				 lockedMap.drawLine(0, TITLE_HEIGHT+1, TITLE_WIDTH,1 );	
				 lockedMap.drawLine(0, 1, TITLE_WIDTH, TITLE_HEIGHT+1);	
				 
				 overlayTexture = new Texture(lockedMap,true);
				 overlayTexture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
				
				 lockedOverlay = new Image(overlayTexture);
				 lockedOverlay.setPosition(0, 0, Align.bottomLeft);
				 super.addActor(lockedOverlay);
				 
				 if (lockEnabled){
					 lockedOverlay.setVisible(true);					 
				 } else {
					 lockedOverlay.setVisible(false);
				 }
				
				
				
				//background.setTouchable(Touchable.disabled);
				//background.setBounds(0, 0, 0, 0); //the above disable doesnt work, maybe this does?
				
				
				super.invalidate();
				//this.setDrawable(new SpriteDrawable(new Sprite(imagesTextureWithMipMaps)));
				
			    return;
	 }

	/** can be override to allow selective objects to be addable**/
	public boolean willAccept(DataObject object){
		return true;
	}
	
	

	/** fired when a DataObject is dragged from it onto it **/
	public void onDrag(DataObject dragged){
		
		if (stored!=null){
			Gdx.app.log(logstag, " stored concept emptying.  was ="+stored.itemsnode.getPLabel());
			stored = null;
		}
	
		if (onDragRunnable!=null){	
			onDragRunnable.run();
		}
	}
	
	/** fired when a DataObject is dropped onto it **/
	public boolean onDrop(DataObject droppedOn){
		
		Boolean accepted = willAccept(droppedOn);
		
		Gdx.app.log(logstag, "DataObjectDropSpot dropped:"+accepted);
		
		if (!accepted || !slotEnabled){
			animatedRejection();
			return false;
		}
		
		//put into slot
		Gdx.app.log(logstag, "adding to slot...");
		droppedOn.setPosition(0, 0);	
		droppedOn.setRotation(0);	
		droppedOn.setScale(1);
		
		this.addActorAt(0, droppedOn); //add at bottom so doesnt mess up clicks on image, as well as allowing the image to act as an overlay
		if (stored==null){
			Gdx.app.log(logstag, "(no previous stored node)");
		}
		DataObject old_stored=stored; 
		
	
		
		droppedOn.setStoredIn(this);
		stored=droppedOn;
		
		Gdx.app.log(logstag, "thing stored now is:  "+stored.itemsnode.toString()+" ");
		
		stored.setTouchable(Touchable.disabled);
		
		if (droppedOn==STMemory.currentlyHeld){
			
			Gdx.app.log(logstag, "removing "+STMemory.currentlyHeld.itemsnode.toString()+" from held");
			
			
			
			STMemory.currentlyHeld = null;			
			MainExplorationView.setCursor(null);
			
			
		
		}
		if (old_stored!=null){

			Gdx.app.log(logstag, "old_stored = "+old_stored.itemsnode.getPLabel());
			
			old_stored.setStoredIn(null);
			Gdx.app.log(logstag, "************************dropping old_stored "+old_stored.itemsnode.getPLabel());
			
			 //get cursor location
			 Vector2 cursor = ME.getCurrentStageCursorPosition();
			 
			 //drop the item on the ground
			 Gdx.app.log(logstag, "************************dropping old_stored at "+cursor.x+","+ cursor.y+30);
			 ME.addnewdrop(old_stored, cursor.x, cursor.y-90);
			 
			 
			//Old_Inventory.setCurrentlyHeld(old_stored);
			
		} else {
			Gdx.app.log(logstag, "(no previous stored node 2)");
		}
		
		if (accepted && runAfterSomethingDroppedOn!=null){
			runAfterSomethingDroppedOn.run(stored);
			
		}
		return true;
	}
	

	private void animatedRejection() {
		// TODO Auto-generated method stub
		
	}

	
	public void onDragRun(Runnable runnable) {
		
		onDragRunnable = runnable;
		
		
	}
	
	public void onDropRun(OnDropRunnable runnable) {
		
		runAfterSomethingDroppedOn = runnable;
		
		
	}
	
	
	
	
	
	public interface OnDropRunnable {
		public void run(DataObject drop);
	}




	/** Allows the dataobject to be removed **/
	public void unlock() {		
		lockEnabled = false;
		
		 if (lockEnabled){
			 lockedOverlay.setVisible(true);					 
		 } else {
			 lockedOverlay.setVisible(false);
		 }
		
	}
	/** stops the dataobject being removed **/
	public void lock() {		
		lockEnabled = true;
		 if (lockEnabled){
			 lockedOverlay.setVisible(true);					 
		 } else {
			 lockedOverlay.setVisible(false);
		 }
		
	}
	/** Allows the dataobjects  to be added**/
	public void setSlotEnabled(boolean state) {		
		slotEnabled = state;
	}




	
	
}
