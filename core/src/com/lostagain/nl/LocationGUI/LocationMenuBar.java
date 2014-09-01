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
	
	MenuLink infoLink;
	MenuLink emailLink ;
	MenuLink dataLink;
	MenuLink linkLinks;
	
	ArrayList<MenuLink> allLinks = new ArrayList<MenuLink>();
	
	boolean locked;
	
	public LocationMenuBar(final LocationContainer parentLocationContainer,Skin skin) {
		super();
		
		//create links
		infoLink = new MenuLink("(INFO)",skin,true);
		emailLink = new MenuLink("(EMAIL)",skin,false);
		dataLink = new MenuLink("(DATA)",skin,false);
		linkLinks = new MenuLink("(LINKS)",skin,true);
		
		infoLink.setAlignment(Align.center);
		dataLink.setAlignment(Align.center);
		emailLink.setAlignment(Align.center);
		linkLinks.setAlignment(Align.center);
		
		
		infoLink.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				

				if (!locked){
					parentLocationContainer.gotoSecplace();
				}
				
			}
						
		});
		emailLink.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				

				if (!locked){
					parentLocationContainer.gotoEmail();
				
				}
				
			}
						
		});
		dataLink.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				
				if (!locked){
					parentLocationContainer.gotoContents();
				}
				
			}
						
		});
		
		linkLinks.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				
				if (!locked){
					parentLocationContainer.gotoLinks();
				}
				
			}
						
		});
				
		allLinks.add(infoLink);
		allLinks.add(emailLink);
		allLinks.add(dataLink);
		allLinks.add(linkLinks);
		
	
		refreshlinks();
		
		
		super.setWidth(65);
		//super.setHeight(400);
		super.fill();
		
		
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
		
		emailLink.setText("Emails ("+emails+")");
		
		if (emails==0){
			
			emailLink.isVisible=false;
			
			
		}  else {
			emailLink.isVisible=true;
		}
		
		refreshlinks();
		
	}

	public void setNumberOfDataObjects(int objects) {
		
		dataLink.setText("Data ("+objects+")");
		
		if (objects==0){
			
			dataLink.isVisible=false;
			
			
		}  else {
			dataLink.isVisible=true;
		}
		
		refreshlinks();
		
	}

	public void setNumberOfLinks(int links) {
		
		linkLinks.setText("Links ("+links+")");
		
		if (links==0){
			
			linkLinks.isVisible=false;
			
			
		}  else {
			linkLinks.isVisible=true;
		}
		
		refreshlinks();
		
	}
	public void setlocked(Boolean locked) {
			
		
		this.locked=locked;
		
		if (locked){
			
			//locked style
			infoLink.setColor(DefaultStyles.lockedLabel);			
			dataLink.setColor(DefaultStyles.lockedLabel);
			emailLink.setColor(DefaultStyles.lockedLabel);
			linkLinks.setColor(DefaultStyles.lockedLabel);
			
		} else {
			
			//unlocked style
			infoLink.setColor(DefaultStyles.unlockedLabel);			
			dataLink.setColor(DefaultStyles.unlockedLabel);
			emailLink.setColor(DefaultStyles.unlockedLabel);
			linkLinks.setColor(DefaultStyles.unlockedLabel);			
			
		}
		
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
	
	
}




