package com.lostagain.nl.me.LocationGUI;

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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.LocationGUI.Inventory.Item;

/** handles the various semantic objects you can aquire and use **/
public class Inventory extends Table {

	static Logger Log = Logger.getLogger("Inventory");
	
	static HashSet<Item> allItems = new HashSet<Item>();
	
	/** keeps track of the last time something is picked up.
	 * This is to help stop missclicks dropping items straight away*/
	private static long lastTime=0l;

	static SSSNode currentlyHeld;

	private float prefwidth;

	
	
	public Inventory() {
		super();
		//super.pad(0);
    	Log.info("_________Inventory_____");
    	
		super.setBackground(DefaultStyles.colors.newDrawable("white", Color.DARK_GRAY));
	//	Label Title = new Label("Objects:",DefaultStyles.linkstyle);
	//	Title.setAlignment(Align.center);
	//	super.add(Title).top().left();

    	Log.info("_________Inventory_____");
		super.debugAll();
		
		Label test = new Label("test",DefaultStyles.linkstyle);
		super.add(test).fill();
		super.setWidth(400);
		
		
	}
	public void removeItem(SSSNode itemsnode)
	{
	

		PlayersData.playerslocationcontents.removeNodeFromThisSet(itemsnode);
		
		//update the home machines location
		if (PlayersData.homeLoc!=null){
		    PlayersData.homeLoc.locationsHub.refreshContents();
		}
		
		Iterator<Item> allItemsit = allItems.iterator();
		
	 while (allItemsit.hasNext()) {
		 
		Item citem = allItemsit.next();
		 
		if (citem.itemsnode==itemsnode){
			allItemsit.remove();

			 super.removeActor(citem);
		}
				
	}
	

		pack();
		super.validate();
		
		
		//if theres no items left the users GUI should have its data tab disabled
		if (allItems.size()==0){
		   MainExplorationView.usersGUI.setDataVisible(true);
		}
		
	}
	public  void addItem(SSSNode itemsnode){
		
		
		//add it to the users machine
		PlayersData.playerslocationcontents.addNodeToThisSet(itemsnode, "local");
		
		//update the home machines location
		if (PlayersData.homeLoc!=null){
		    PlayersData.homeLoc.locationsHub.refreshContents();
		}
		
		
		
		//SSSNodesWithCommonProperty usersInventory  = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.isOn, PlayersStartingLocation.computersuri);
		//usersInventory.add(itemsnode);
		
		Item newitem = new Item(itemsnode);		
		allItems.add(newitem);
		

		//Log.info("______________size="+allItems.size());
		//if we are too big, start a new row
		if ((allItems.size()>6)&&(allItems.size() %8 == 0)){
			
			Log.info("______________slash 8_"+allItems.size());			
			//super.row();	
			
		}

		//Label test = new Label("test",DefaultStyles.linkstyle);
	//	super.add(newitem).size(60, 30).top().left().fill().expandY();
		super.add(newitem).size(60, 30).top().left().fillY().expandY();

		//pack();
		//super.invalidate();
		
	//	super.validate();

		//update the GUI bar in case the inventory tab isnt there yet
		MainExplorationView.usersGUI.setDataVisible(true);
		
		
	}
	
	
	protected static void setCurrentlyHeld(SSSNode itemsnode2) {
		
		currentlyHeld=itemsnode2;
		
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
		    Texture texture = new Texture(textPixmap);
		    MainExplorationView.setCursor(texture);
		    
		    

		lastTime = TimeUtils.millis();
		
	}
	public void dropHeldItem(){
		dropHeldItem(false);
	}
	public void dropHeldItem(boolean overrideDelay){
		

		long LastHeld =  TimeUtils.timeSinceMillis(lastTime);
		
		//have a delay to ensure they dont drop it straight away by mistake
		if (LastHeld>500 || overrideDelay)
		{
			MainExplorationView.setCursor(null);
		}
		
	}
	
	 protected static class Item extends Label 
	 {
		 
		 SSSNode itemsnode;
		 
		 public Item(final SSSNode itemsnode){	
			 
			 super(itemsnode.getPLabel(),DefaultStyles.linkstyle);				 
			 super.setWrap(true);
			 this.itemsnode = itemsnode;
			 
			 super.addListener(new ClickListener () {			
					@Override
					public void clicked(InputEvent ev, float x , float y){
						
						setCurrentlyHeld(itemsnode);
						MainExplorationView.usersGUI.closeInventory();

					}

				});
			 
			 invalidate();
			 
		 }

		
		 
	 }

	public void setPrefWidth(float width) {
		prefwidth=width;
		
	}

	@Override
	public float getPrefWidth() {
		return prefwidth;
		
		
	}
	@Override
	public float getPrefHeight() {
		return 200;
		
		
	}

	

}
