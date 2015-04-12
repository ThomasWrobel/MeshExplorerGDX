package com.lostagain.nl.me.locationFeatures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.locationFeatures.Link.LinkMode;
import com.lostagain.nl.me.locationFeatures.ObjectFile.ObjectFileState;

public class LinksScreen extends Table implements LocationScreen {

	static Logger Log = Logger.getLogger("LinksScreen");
	
		
	Label title = new Label("Links:",DefaultStyles.linkstyle);


	ArrayList<Link> allLinks = new ArrayList<Link>();

	LocationsHub parentLocationContainer;
	
		

	public LinksScreen(LocationsHub parentLocationContainer, SSSNode securedBy) {
		super();
		super.setFillParent(true);
		this.parentLocationContainer=parentLocationContainer;
		super.setDebug(true, true);
		
		//Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
				
	//	super.add(title);
		
		
	}

	
	public void addLink(SSSNode sssNode) {
		
		
	//	Label linkLabel = new Label(sssNode.getPLabel(),DefaultStyles.linkstyle);	
	//	linkLabel.setHeight(20);
		
		
		Link newlink = new Link(sssNode,this);			
	//	newlink.setHeight(30);
		super.left();
		super.top();
		super.add(newlink).fillX().height(30).expandX();// expandX().
		newlink.validate();
		super.row();
		
		allLinks.add(newlink);
		
	}

	public void recheckLinksAndLines(){
		
		for (Link linktocheck : allLinks) {
			
			linktocheck.recheckAndRefresh();
			
		}
		
	}
	
	



	public void removeAllContents() {
		
		super.clearChildren();
		
	}


	public void clearLinks() {
		 removeAllContents() ;
		
	}
	
	

}
