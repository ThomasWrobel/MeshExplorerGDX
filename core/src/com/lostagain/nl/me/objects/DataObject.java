package com.lostagain.nl.me.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.gui.DataObjectSlot;
import com.lostagain.nl.me.gui.DataObjectDropTarget;
import com.lostagain.nl.me.gui.Inventory;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
/** The visual representation of a piece of data 
 * as well as the node it presents.
 * Designed to be used in inventory's, or when the mouse "holds" it, or as the drop from a creature **/

public class DataObject extends Image 
{

	final static String logstag = "ME.DataObject";
	
	public SSSNode itemsnode;

	    static int TITLE_WIDTH=120;
	    static int TITLE_HEIGHT=40;
	    static float SCALE = 1f; //0.5f;
	    
	    static int PADDING_X=10;//each side text padding
	    static int PADDING_Y=3;//each side
	    
	    static int ImageBorder = 3;
		//Pixmap textPixmap = new Pixmap(TITLE_WIDTH+(ImageBorder*2), TILE_HEIGHT+(ImageBorder*2), Format.RGBA8888);
		String dataname = "";
		
		public Texture imagesTextureWithMipMaps;
		
		DataObject thisDataObject;

		private DataObjectDropTarget storedin;
				
		
		 /** create data object with specified name, if no name specified nodes label will be used**/
		 public DataObject(final SSSNode itemsnode){
			 
			 this.itemsnode = itemsnode;
			 dataname=itemsnode.getPLabel();			 
			 createDataObject();
			 setupAsDragable();
			 
			 thisDataObject=this;
		 }
		 
		 /** create data object with specified name, if no name specified nodes label will be used**/
	 public DataObject(final SSSNode itemsnode,String name){	
		 
		 //super(new Texture(new Pixmap(TITLE_WIDTH, TILE_HEIGHT, Format.RGBA8888)));
		
		 this.itemsnode = itemsnode;
		 dataname=name;
		 
		 
		 createDataObject();

		 setupAsDragable();

		 thisDataObject=this;
	 }

		private void setupAsDragable() {
			Gdx.app.log(logstag,"______________testing gui ffor adding dragable :"+this.itemsnode.getPLabel());
			//assign as draggable 
//			 if (MainExplorationView.usersGUI!=null){
//				 
//				 Gdx.app.log(logstag,"______________adding as dragable :"+this.itemsnode.getPLabel());
//				 
//			 MainExplorationView.usersGUI.dragAndDrop.addSource(new Source(this) {
//					public Payload dragStart (InputEvent event, float x, float y, int pointer) {
//						
//						Gdx.app.log(logstag,"______________dragStart");
//						MainExplorationView.disableDrag();
//						
//						Payload payload = new Payload();
//						payload.setObject(DataObject.this.imagesTextureWithMipMaps);
//						payload.setDragActor(DataObject.this);
//
//						Label validLabel = new Label("Some payload!!Q", DefaultStyles.linkstyle);
//						validLabel.setColor(0, 1, 0, 1);
//						payload.setValidDragActor(validLabel);
//
//						Label invalidLabel = new Label("Some payload!!", DefaultStyles.linkstyle);
//						invalidLabel.setColor(1, 0, 0, 1);
//						payload.setInvalidDragActor(invalidLabel);
//
//						return payload;
//					}
//				});
//			 }
		}

		private void createDataObject() {
			//use two or three lines if needed
			 if (dataname.length()>25 && dataname.length()<41 ){
				 
				 dataname = dataname.substring(0, 20) +"\n"+dataname.substring(20);
				 Gdx.app.log(logstag,"______________adding new line to dataname:"+dataname);
				 
			 } else if (dataname.length()>40){
				 
				 dataname = dataname.substring(0, 20) +"\n"
						   +dataname.substring(20, 40) +"\n"
						   +dataname.substring(40);
			 }
				
			 createImage();
			 	
			 super.setScale(SCALE);
			 
			 		 
			// set clicker (WORKS, dont delete)
			 
			 super.addListener(new InputListener() {
				 	@Override
					public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

				 		 triggerPickedUp();
						
						
						
						return true;
						
				 	}
				 	
				 	@Override
					public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				 		//Old_Inventory.dropHeldItem();

						 Gdx.app.log(logstag,"_________________: Object dropped from itself "+x+","+y);
				 		 
				 		droppedAt(x, y);
						
				 	}
				 	
				 });
			 
			 
			 //----------------------------------------------------------
			 /*
			 
			super.addListener(new ClickListener () {			
				@Override
				public void clicked(InputEvent ev, float x , float y){
					pickup();
				}

				
				
			});*/
		}
	 
		public float getPrefWidth() {
			return TITLE_WIDTH;
		}
		
		public float getPrefHeight() {
			return TITLE_HEIGHT;
		}
		
		
		private void pickup() {
			
			//we hold the item (dont add to inventory by default)
			ME.playersInventory.holdItem(this);
						

			//add it to the users machine
			//ME.playersInventory.addItem(itemsnode);
			
			//remove from parent
			this.remove();
			
		}
		
		
		
	 /** creates the image of the dataobject (outline and text, with filters) **/
	 public void createImage(){
			//the following should be cached so we don't need to keep regenerating the images
			
		    String Letters=dataname;
		    
		    Pixmap textPixmap = new Pixmap(TITLE_WIDTH, TITLE_HEIGHT, Format.RGBA8888); //imagesTexture.getTextureData().consumePixmap();
		    
					textPixmap.setColor(0.3f, 0.2f, 0.2f, 1);					
					textPixmap.fill();
					textPixmap.setColor(1, 0, 0, 1);					
					textPixmap.drawRectangle(0, 0, TITLE_WIDTH, TITLE_HEIGHT);
					textPixmap.setColor(0.6f, 0, 0, 1);					
					textPixmap.drawRectangle(1, 1, TITLE_WIDTH-2, TITLE_HEIGHT-2);
					textPixmap.setColor(0.3f, 0, 0, 0.7f);					
					textPixmap.drawRectangle(2, 2, TITLE_WIDTH-4, TITLE_HEIGHT-4);
					textPixmap.setColor(0.1f, 0, 0, 0.5f);					
					textPixmap.drawRectangle(3, 3, TITLE_WIDTH-6, TITLE_HEIGHT-6);
					
			    // get the glyph info
			  //  BitmapFontData data = ME.font.getData();
			    
				//Gdx.app.log(logstag,"______________data.imagePaths[0] ="+data.imagePaths[0]);			
				
			    BitmapFontData data = DefaultStyles.standdardFont.getData(); //new BitmapFontData(Gdx.files.internal(data.imagePaths[0]), true);
			    
			    Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0]));
			    
				// draw the character onto our base pixmap
			  
			  int totalwidth=0;
			  int current_testedwidth=0;
			 			  
			  Glyph defaultglyph = data.getGlyph(Letters.charAt(0));
			  
			  int totalheight=defaultglyph.height+9;
			  //precalcwidth
			    for (int i = 0; i < Letters.length(); i++) {
					
					Glyph glyph = data.getGlyph(Letters.charAt(i));
					
					if (Letters.charAt(i) == '\n'){
						Gdx.app.log(logstag,"______________resetting width due to new line");			
						
						//should track height to fit verticaly?	
						//yp=yp+glyph.height+3;
						totalheight=totalheight+defaultglyph.height+9;
						
						if (totalwidth<current_testedwidth){
							totalwidth=current_testedwidth;
						}
						
						current_testedwidth=0;
						continue;
					}
					
					//if a glyph is null (ie, unsupported character), we use a default space
					if (glyph==null){
						current_testedwidth=current_testedwidth+ defaultglyph.width +3;
						continue;
						
					}
					
					current_testedwidth=current_testedwidth+ glyph.xadvance+3;
					
				}

				if (totalwidth<current_testedwidth){
					totalwidth=current_testedwidth;
				}
				
			    int currentX=0;
		
			    //add padding
			 //   totalwidth=totalwidth+(PADDING_X*2);
			    
			    //work out scaling factor
			    float scaledown   = ((float)(TITLE_WIDTH-(PADDING_X*2))) / (float)totalwidth;
			    
			    //total width after scaling
			    float scaledwidth = (float)totalwidth * (float)scaledown;
				//total height after scaling
			    float scaledheight = (float)totalheight * (float)scaledown;
			    
			    int xpad = PADDING_X; //(int) (((TITLE_WIDTH-scaledwidth)/2));			    
			    int ypad = (int) ( (((TITLE_HEIGHT-(PADDING_Y*2))-scaledheight)/2));
			    
			    //int ypad=0;
			    
				Gdx.app.log(logstag,"totalwidth="+totalwidth);

				Gdx.app.log(logstag,"xpad="+xpad);
				Gdx.app.log(logstag,"ypad="+ypad);
				
				Gdx.app.log(logstag,"scaledwidth="+scaledwidth);
				
			    //xpad = (int) ((float)xpad * (float)scaledown);
			    
			    //xpad = 0;			    
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
						yp=yp+defaultglyph.height+9;
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
							xpad+currentX+glyph.xoffset,
							ypad+(yp+(yglyphoffset )),//+(TILE_HEIGHT - (cheight)) / 2,						
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
					double newprecisepos =  ((glyph.xadvance+3)  * scaledown)+lastremainder;
					lastremainder = newprecisepos - Math.floor(newprecisepos);
					int newpos = (int) (Math.floor(newprecisepos));
				//	Gdx.app.log(logstag,"newpos="+newpos);
				//	Gdx.app.log(logstag,"lastremainder="+lastremainder);
					currentX=currentX + newpos;
				}
				
				
			
				// save this as a new texture
			 //   Texture texture = new Texture(textPixmap);

				Gdx.app.log(logstag,"made objects texture"+currentX);
			
				
				//imagesTexture.draw(textPixmap, ImageBorder, ImageBorder); //ensure we have some padding to make edges look nicer when the object is at an angle
			//	imagesTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
				//add border
				Pixmap finalpixmap = new Pixmap(TITLE_WIDTH+(ImageBorder*2), TITLE_HEIGHT+(ImageBorder*2), Format.RGBA8888);
				finalpixmap.drawPixmap(textPixmap, ImageBorder,ImageBorder);
				
				imagesTextureWithMipMaps = new Texture(finalpixmap,true);
				textPixmap.dispose();
				finalpixmap.dispose();
				
				imagesTextureWithMipMaps.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
				this.setDrawable(new SpriteDrawable(new Sprite(imagesTextureWithMipMaps)));
				
			    return;
	 }

		public static int getStandardWidth() {
			
			return TITLE_WIDTH;
		}

		public static int getStandardHeight() {
				
			return TITLE_HEIGHT;
		}
		
		 
	 
	public static float getStandardScaledWidth() {
		
		return TITLE_WIDTH * SCALE;
	}

	public static float getStandardScaledHeight() {
			
		return TITLE_HEIGHT * SCALE;
	}

	public static float getStandardScale() {
		return SCALE;
	}

	public void setStoredIn(DataObjectDropTarget dataObjectDropSpot) {
		storedin=dataObjectDropSpot;
		
		
	}

	/** should be fired any time the mouse is released while holding a dataobject **/
	public void droppedAt(float x, float y) {

		Gdx.app.log(logstag,"_________________: Object dropped at "+x+","+y);
		 
		 
		//Vector2 screenCoOrdinates = MainExplorationView.gameStage.stageToScreenCoordinates(new Vector2(x,y));
		//float sx = screenCoOrdinates.x;
		//float sy = -screenCoOrdinates.y+MainExplorationView.gameStage.getHeight();
		 
	    Vector2 screenCoOrdinates = MainExplorationView.getCurrentCursorScreenPosition();
		float sx = screenCoOrdinates.x;
		float sy =  MainExplorationView.gameStage.getHeight()-screenCoOrdinates.y;
		
		
		 Gdx.app.log(logstag,"_________________: Object dropped at screen "+sx+","+sy);
		 
		 
		 boolean addedToObject = false;
		 
		 //find what we droped on, test usersGUI first then after that the game stage
		Actor test = MainExplorationView.usersGUI.hit(sx,sy, false);
		
		if (test==null){
			
			//we look at stage x/y
			Vector2 stageposition = MainExplorationView.gameStage.screenToStageCoordinates(screenCoOrdinates);
			
			//if we found no hit in the gui, then we look at the stage under it
			//x and y are already in stage co-ordinates
			
			 test = MainExplorationView.gameStage.hit(stageposition.x, stageposition.y, false);
				
		}
		
		if (test!=null){
			Gdx.app.log(logstag,"__hit:"+test.getClass().getName());
			Gdx.app.log(logstag,"__hit:"+test.getName());
			
		
		
		if (test.getName()!=null && test.getName().equalsIgnoreCase(DataObjectSlot.DROPSPOTTYPENAME)){
			
			Gdx.app.log(logstag,"________________droptarget spot found");
										
			DataObjectDropTarget dropedOnThis = (DataObjectDropTarget)test.getUserObject();
			addedToObject=dropedOnThis.onDrop( thisDataObject);
			
		}
		
		//test rest
		
		
		
		}
		
		if (addedToObject){
			
		} else {
			Inventory.dropHeldItem(true);
		}
		
		return;
	}

	public void triggerPickedUp() {
		Gdx.app.log(logstag,"___________dd__:pick up ");
		
		if (storedin!=null){
			
			//cancel if locked
			
			storedin.onDrag(this);
			storedin=null;
		}
		
		pickup();
		
		
		MainExplorationView.disableDrag();
	}
	
	 




}
