package com.lostagain.nl.LocationGUI;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.ME;

public class LocationMenuBar extends VerticalGroup {
	
	MenuLink infoButton;
	MenuLink emailButton;
	MenuLink dataButton;
	MenuLink linkButton;
	
	ArrayList<MenuLink> allLinks = new ArrayList<MenuLink>();
	
	boolean locked;
	
	public LocationMenuBar(final LocationContainer parentLocationContainer,Skin skin) {
		super();
		
		//create links
		infoButton = new MenuLink("(INFO) ",skin,true);
		emailButton = new MenuLink("(EMAIL) ",skin,false);
		dataButton = new MenuLink("(DATA) ",skin,false);
		linkButton = new MenuLink("(LINKS) ",skin,true);
		
		infoButton.setAlignment(Align.center);
		dataButton.setAlignment(Align.center);
		emailButton.setAlignment(Align.center);
		linkButton.setAlignment(Align.center);
		
		
		infoButton.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				
				if (!locked){
					parentLocationContainer.gotoSecplace();
					
					//change all styles to down
					setAllButtonsUp();
					
					infoButton.setDownStyle();
				}
				
			}
						
		});
		emailButton.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				

				if (!locked){
					parentLocationContainer.gotoEmail();
					

					//change all styles to down
					setAllButtonsUp();
					
					emailButton.setDownStyle();
				
				}
				
			}
						
		});
		dataButton.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				
				if (!locked){
					parentLocationContainer.gotoContents();
					

					//change all styles to down
					setAllButtonsUp();
					
					dataButton.setDownStyle();
				}
				
			}
						
		});
		
		linkButton.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				
				if (!locked){
					parentLocationContainer.gotoLinks();
					
				}
				
			}
						
		});
				
		allLinks.add(infoButton);
		allLinks.add(emailButton);
		allLinks.add(dataButton);
		allLinks.add(linkButton);
		
	
		refreshlinks();
		
		
		super.setWidth(65);
		//super.setHeight(400);
		super.fill();
		
		
	}

	
	protected void setAllButtonsUp() {
		
		for (MenuLink link : allLinks) {
			
				link.setUpStyle();
			
			
		}
		
	}


	private void refreshlinks() {
		super.clearChildren();
		super.align(Align.center);
		for (MenuLink link : allLinks) {
			
			if (link.isVisible==true){
				super.addActor(link);
			}
			
		}
	}


	public void setNumberOfMessages(int emails) {
		
		emailButton.setText("Emails ("+emails+") ");
		
		if (emails==0){
			
			emailButton.isVisible=false;
			
			
		}  else {
			emailButton.isVisible=true;
		}
		
		refreshlinks();
		
	}

	public void setNumberOfDataObjects(int objects) {
		
		dataButton.setText("Data ("+objects+") ");
		
		if (objects==0){
			
			dataButton.isVisible=false;
			
			
		}  else {
			dataButton.isVisible=true;
		}
		
		refreshlinks();
		
	}

	public void setNumberOfLinks(int links) {
		
		linkButton.setText("Links ("+links+") ");
		
		if (links==0){
			
			linkButton.isVisible=false;
			
			
		}  else {
			linkButton.isVisible=true;
		}
		
		refreshlinks();
		
	}
	public void setlocked(Boolean locked) {
			
		
		this.locked=locked;
		
		if (locked){
			
			//locked style
			infoButton.setColor(DefaultStyles.lockedLabel);			
			dataButton.setColor(DefaultStyles.lockedLabel);
			emailButton.setColor(DefaultStyles.lockedLabel);
			linkButton.setColor(DefaultStyles.lockedLabel);
			
		} else {
			
			//unlocked style
			infoButton.setColor(DefaultStyles.unlockedLabel);			
			dataButton.setColor(DefaultStyles.unlockedLabel);
			emailButton.setColor(DefaultStyles.unlockedLabel);
			linkButton.setColor(DefaultStyles.unlockedLabel);			
			
		}
		
	}
	public void setinfoButtonUp() {
		setAllButtonsUp();
		
		infoButton.setDownStyle();
	}
	

	public void setLinkButtonUp() {
		setAllButtonsUp();
		
		linkButton.setDownStyle();
	}
	public void setEmailButtonUp() {
		setAllButtonsUp();
		
		emailButton.setDownStyle();
	}
	
	public void setDataButtonUp() {
		setAllButtonsUp();
		
		dataButton.setDownStyle();
	}
	
}


/** a link that represents a page on the computer  **/
class MenuLink extends Label {
	
	Boolean isVisible = true;
	
	public MenuLink(String name,Skin skin,Boolean isVisible){
			
		super(name,skin);
		this.isVisible=isVisible;
		
		super.setAlignment(Align.center);		
		
	}
	public void setUpStyle(){
		
		super.setColor( DefaultStyles.unlockedLabel);
		
		
	}
		
	public void setDownStyle(){
		
		super.setColor( DefaultStyles.labelpressed);
		
		
	}
	
}




