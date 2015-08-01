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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
//import com.lostagain.nl.Old_Inventory.Item;
import com.lostagain.nl.me.objects.DataObject;

/** handles the various semantic objects you can aquire and use **/
public class Inventory  {

	static Logger Log = Logger.getLogger("ME.Old_Inventory");
	
	static HashSet<DataObject> allItems = new HashSet<DataObject>();
	
	/** keeps track of the last time something is picked up.
	 * This is to help stop missclicks dropping items straight away*/
	private static long lastTime=0l;

//	public static DataObject currentlyHeld;
	

	private float prefwidth;

	
	
	public Inventory() {
		super();
		//super.pad(0);
    	Log.info("_________Inventory_____");
    	
		//super.setBackground(DefaultStyles.colors.newDrawable("white", Color.DARK_GRAY));
	//	Label Title = new Label("Objects:",DefaultStyles.linkstyle);
	//	Title.setAlignment(Align.center);
	//	super.add(Title).top().left();

    	Log.info("_________Inventory_____");
	//	super.debugAll();
		
		Label test = new Label("test",DefaultStyles.linkstyle);
		//super.add(test).fill();
		//super.setWidth(400);
		
		
	}
	public void removeItem(SSSNode itemsnode)
	{
	

		PlayersData.playerslocationcontents.removeNodeFromThisSet(itemsnode);
		
		//update the home machines location
		if (PlayersData.homeLoc!=null){
		    PlayersData.homeLoc.locationsHub.refreshContents();
		}
		
		Iterator<DataObject> allItemsit = allItems.iterator();
		
	 while (allItemsit.hasNext()) {
		 
		 DataObject citem = allItemsit.next();
		 
		if (citem.itemsnode==itemsnode){
			allItemsit.remove();

			// super.removeActor(citem);
		}
				
	}
	

		//pack();
		//super.validate();
		
		
		//if theres no items left the users GUI should have its data tab disabled
		if (allItems.size()==0){
		   MainExplorationView.usersGUI.setSTMemVisible(true);
		}
		
	}
	public  void addItem(DataObject obj){
		
		
		//add it to the users machine
		PlayersData.playerslocationcontents.addNodeToThisSet(obj.itemsnode, "local");
		
		//update the home machines location
		if (PlayersData.homeLoc!=null){
		    PlayersData.homeLoc.locationsHub.refreshContents();
		}
		
		
		
		//SSSNodesWithCommonProperty usersInventory  = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(StaticSSSNodes.isOn, PlayersStartingLocation.computersuri);
		//usersInventory.add(itemsnode);
		
		//Item newitem = new Item(itemsnode);		
		allItems.add(obj);
		

		//Log.info("______________size="+allItems.size());
		//if we are too big, start a new row
		if ((allItems.size()>6)&&(allItems.size() %8 == 0)){
			
			Log.info("______________slash 8_"+allItems.size());			
			//super.row();	
			
		}

		//Label test = new Label("test",DefaultStyles.linkstyle);
	//	super.add(newitem).size(60, 30).top().left().fill().expandY();
		//super.add(obj).size(60, 30).top().left().fillY().expandY();

		//pack();
		//super.invalidate();
		
	//	super.validate();

		//update the GUI bar in case the inventory tab isnt there yet
		if (obj.itemsnode== StaticSSSNodes.STMemoryAbility){ //wont work with current nodes/propertys need the precise subclass of gun
			MainExplorationView.usersGUI.setSTMemVisible(true);
		}
		
		if (obj.itemsnode== StaticSSSNodes.PrototypeConceptGun){
			MainExplorationView.usersGUI.setmyCGunVisible(true);
			
		}
		
		
	}
	
/*
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

		
		 
	 }*/

	public void setPrefWidth(float width) {
		prefwidth=width;
		
	}



	

}
