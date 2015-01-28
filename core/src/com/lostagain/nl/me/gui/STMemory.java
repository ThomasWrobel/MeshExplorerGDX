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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.objects.DataObject;

/** handles the various semantic objects you can carry with, either for concept gun use
 * or as data to unclock locations **/
public class STMemory extends Table implements DataObjectDropTarget {
	

	//static Logger Log = Logger.getLogger("ME.TempMemory");
	final static String logstag = "ME.TempMemory";
	
	
	static HashSet<DataObject> carryingObjects = new HashSet<DataObject>();
	
	int ItemLimit = 7; //can be expanded in future
	
	
	/** keeps track of the last time something is picked up.
	 * This is to help stop missclicks dropping items straight away*/
	private static long lastTime=0l;

	public static SSSNode currentlyHeld;

	private float prefwidth;

	private float prefheight;
	
	
	public STMemory() {
		super();
		//super.pad(0);
		super.setName(DROPSPOTTYPENAME);// this name determains its a drop target (ie, objects can be dropped too it)
		super.setUserObject(this); //tells the object dropped where to fire the drop command (in this case its this very widget, but in some cases it might be different...ie, you drop it somewhere and it appears somewhere else)
		
		
		
		Gdx.app.log(logstag,"_________TempMemory_____");
		Color ColorM = new Color(Color.DARK_GRAY);
		ColorM.a=0.5f;
		
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
	public  boolean 	 addItem(DataObject dataobject){
		if (carryingObjects.size()<ItemLimit){
		
			carryingObjects.add(dataobject);
			super.add(dataobject);
			super.row();
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
			
			Boolean success = addItem(Inventory.currentlyHeld);

			//if was successfully added we set currently held to nothing
			if (success){
					Inventory.currentlyHeld = null;
			} else {
				
				//should have some feedback here for STMemory full up
				
			}
			
		}
		
		
		
		@Override
		public boolean onDrop(DataObject droppedOn) {
			Gdx.app.log(logstag,"adding to inventory if not full");
			boolean accepted = addItem(droppedOn);
			
			
			if (droppedOn==Inventory.currentlyHeld && accepted){
				
				droppedOn.setRotation(0); //ensure its straight when going on
				droppedOn.setScale(1); //natural scale (might change in future)
				
				droppedOn.setStoredIn(this);
				Gdx.app.log(logstag, "removing from held");
				
				
				
				Inventory.currentlyHeld = null;			
				MainExplorationView.setCursor(null);
				
				
			
			}
	
			
			
			return accepted;
		}
		
		@Override
		public void onDrag(DataObject dataobject) {
			
			 removeItem(dataobject);
		}

}
