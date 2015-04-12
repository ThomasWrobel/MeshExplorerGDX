package com.lostagain.nl.me.locationFeatures;

import java.util.ArrayList;


import java.util.Iterator;



import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.me.locationFeatures.ObjectFile.ObjectFileState;

public class ContentsScreen  extends Container<ScrollPane>  implements LocationScreen {
	static Logger Log = Logger.getLogger("ContentsScreen");

	Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	

	private ArrayList<SSSNode> allContentsNodes = new  ArrayList<SSSNode>();
	
	static Boolean isRunning = false;
		
	String contentsTitle = "Contents:";
	Label title  = new Label(contentsTitle,skin);
	
	boolean locked = false;
	
	//scroll contents
	final Table scrollTable;
	
	//scroll stuff
	final ScrollPane scroller;
	
	public ContentsScreen(LocationsHub parentLocationContainer, SSSNode securedBy, String contentsTitle) {
		
		
		
		super();

        scrollTable = new Table();
		

        scrollTable.setX(0);
        scrollTable.setY(0);
             
        scroller = new ScrollPane(scrollTable);
        scrollTable.setBackground(DefaultStyles.colors.newDrawable("white",new Color(0,0,1,0.5f)));
        
        
        
        
        scroller.setX(0);
        scroller.setY(0);
    	//scroller.setScrollingDisabled(true, true);
    	scroller.setCancelTouchFocus(false);
        scroller.setDebug(true);
        scroller.setFillParent(true);
        

        super.left();	       
        super.bottom();        
        super.setActor(scroller);
        
        
        
        
		this.contentsTitle = contentsTitle;

		Label title = new Label(contentsTitle,skin);
		//title.setX(150);
	//	title.setY(50);
		scrollTable.add(title).fillX().height(30).expandX();
		//super.addActor(title);
		scrollTable.row();
		
	}

	
	@Override
	public void layout(){
		Log.info("validate contents__");
		super.layout();
		
		//scroller.validate();
		scroller.setHeight(super.getParent().getHeight());
	
		scrollTable.validate();
	}
	
	
	
	public void addObjectFile(ObjectFile ObjectFile){
		
		//ensure its not already on here
		if (allContentsNodes.contains(ObjectFile.objectsnode)){			
			return;
		};
		
		allContentsNodes.add(ObjectFile.objectsnode);
		scrollTable.left();
		scrollTable.top();
		
		
		scrollTable.add(ObjectFile).fillX().expandX(); //height(30)
		ObjectFile.validate();
		
		scrollTable.row();

		invalidate();
	}
	
	public void addObjectFile(SSSNode sssNode) {
		Gdx.app.log("", "adding node:"+sssNode.PURI);
		
		//make a ObjectFile from this sssnode rk) 
		ObjectFile newObjectFile = new ObjectFile(sssNode,this,locked);		
		addObjectFile(newObjectFile);
		
		invalidate();
	
	}

	public void removeAllContents() {
		
		scrollTable.clearChildren();
		allContentsNodes.clear();
		
		//read add title at top
		scrollTable.add(title).fillX().height(30).expandX();
		scrollTable.row();
		invalidate();
	}


	public void setLocked(boolean b) {
		locked=b;
		
	}
	
	

}
