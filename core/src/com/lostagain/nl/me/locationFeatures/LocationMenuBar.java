package com.lostagain.nl.me.locationFeatures;

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
	
	InterfaceButton infoButton;
	InterfaceButton emailButton;
	InterfaceButton dataButton;
	InterfaceButton abilityButton;
	InterfaceButton linkButton;
	
	ArrayList<InterfaceButton> allLinks = new ArrayList<InterfaceButton>();
	
	boolean locked;
	
	public LocationMenuBar(final LocationsHub parentLocationContainer,Skin skin) {
		super();
		
		//create links
		infoButton = new InterfaceButton("(INFO) ",true);
		emailButton = new InterfaceButton("(EMAIL) ",false);
		dataButton = new InterfaceButton("(DATA) ",false);
		abilityButton  = new InterfaceButton("(ABIL) ",false);
		linkButton = new InterfaceButton("(LINKS) ",true);
		
		infoButton.setAlignment(Align.center);
		dataButton.setAlignment(Align.center);
		emailButton.setAlignment(Align.center);
		abilityButton.setAlignment(Align.center);
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
		abilityButton.addListener(new ClickListener () {
			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				
				if (!locked){
					parentLocationContainer.gotoAbilitys();
					

					//change all styles to down
					setAllButtonsUp();
					
					abilityButton.setDownStyle();
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
		allLinks.add(abilityButton);
		allLinks.add(linkButton);
		
	
		refreshlinks();
		
		
		super.setWidth(65);
		//super.setHeight(400);
		super.fill();
		
		
	}

	
	protected void setAllButtonsUp() {
		
		for (InterfaceButton link : allLinks) {
				
			if (!link.isDisabled){
				link.setUpStyle();
			}
			
		}
		
	}


	private void refreshlinks() {
		super.clearChildren();
		super.align(Align.center);
		for (InterfaceButton link : allLinks) {
			
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
	
	public void setNumberOfAbilityObjects(int objects) {
		
		abilityButton.setText("ABIL ("+objects+") ");
		
		if (objects==0){
			
			abilityButton.isVisible=false;
			
			
		}  else {
			abilityButton.isVisible=true;
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
			infoButton.setDisabledStyle();		
			dataButton.setDisabledStyle();	
			abilityButton.setDisabledStyle();	
			emailButton.setDisabledStyle();	
			linkButton.setDisabledStyle();	
			
		} else {
			
			//unlocked style
			infoButton.setUpStyle();			
			dataButton.setUpStyle();
			abilityButton.setUpStyle();
			emailButton.setUpStyle();	
			linkButton.setUpStyle();			
			
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
	
	public void setAbilityButtonUp() {
		setAllButtonsUp();
		
		abilityButton.setDownStyle();
	}
	
	
}







