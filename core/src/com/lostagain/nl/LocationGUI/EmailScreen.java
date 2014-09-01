package com.lostagain.nl.LocationGUI;


import java.util.ArrayList;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.darkflame.client.semantic.SSSNode;

public class EmailScreen extends Container<ScrollPane>  implements LocationScreen {

	static Logger Log = Logger.getLogger("EmailScreen");
	private int loadingEmails=0;
	//private Table container = new Table();
	private ArrayList<Message> AllMessages = new ArrayList<Message>();
	final Table scrollTable;
	static Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	 final ScrollPane scroller;
	 
	public int numOfEmails=0;
	
	public EmailScreen(LocationContainer parentLocationContainer, SSSNode securedBy) {
		
		//super.setDebug(true);
		super.setX(0);
		super.setY(0);
		
		

	      //  final Label text2 = new Label("This is a short string!", skin);
	      //  text2.setAlignment(Align.center);
	      //  text2.setWrap(true);
	       // final Label text3 = new Label("fghghjgjh", skin);
	      //  text3.setAlignment(Align.center);
	       // text3.setWrap(true);

	        scrollTable = new Table();
	       // scrollTable.add(text).expandX().fillX();
	      //  scrollTable.row();
	       // scrollTable.add(text2).expandX().fillX();
	      //  scrollTable.row();
	       // scrollTable.add(text3).expandX().fillX();
	        //scrollTable.setFillParent(true);
	        scrollTable.setX(0);
	        scrollTable.setY(0);
	        scrollTable.bottom();
	        
	        scroller = new ScrollPane(scrollTable);
	        scrollTable.setBackground(DefaultStyles.colors.newDrawable("white", Color.DARK_GRAY));
	        scroller.setX(0);
	        scroller.setY(0);
	        
	        scroller.setDebug(true);
	        scroller.setFillParent(true);
	      //  final Table table = new Table();
	       // table.setFillParent(true);
	      //  table.add(scroller).fill().expand();

	        super.left();
	        super.top();
        super.setActor(scroller);
        super.setFillParent(true);
        
        /*
        final ScrollPane scroller = new ScrollPane(scrollTable);

        final Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();

		
		*/
        
		/*
		super.setFillParent(true);		
		super.setWidget(container);
		
		
		super.setWidth(100);
		super.setHeight(100);
		
		
		container.left();
		container.top();
		
		
		Label title = new Label("Emails:",skin);
		container.add(title);
		container.row();*/

	}
	
	public void validate(){	
		
		
		super.validate();
		scroller.validate();
		scrollTable.validate();
		

	}
	public void addEmailLocation(SSSNode sssNode) {
		
		loadingEmails++;
		
		
		
		//load the data
		//we can borrow the SuperSimpleSemantics file load for this and 
		
		//set up the runnable for when the data is retrieved
		FileCallbackRunnable runoncomplete = new FileCallbackRunnable(){
			@Override
			public void run(String responseData, int responseCode) {
				loadingEmails--;
				
				addEmail(responseData);
				invalidate();
				checkForEmailsFinnishedLoading();
				
				
			}

			
			
		};
		
		FileCallbackError runonerror = new FileCallbackError(){

			@Override
			public void run(String errorData, Throwable exception) {
				loadingEmails--;
				
				Log.info(errorData);
				
				addEmail(errorData);

				checkForEmailsFinnishedLoading();
			}
			
		};
		
		String url = "semantics\\"+sssNode.getPLabel();
		
		//trigger the file retrieval
		SuperSimpleSemantics.fileManager.getText(url, runoncomplete, runonerror, false);
		

		
	}

	
	private void checkForEmailsFinnishedLoading() {
		// TODO Auto-generated method stub
		if (loadingEmails==0){
			
			Log.info("emails finnished loading");
			
			
			//trigger layout
			validate();
			
			//validate contents
			for (Message email : AllMessages) {
				email.validate();
			}
			
			//sometimes needs to be done twice due to wordwrap in use
			validate();
		}
		
	}
	
	
	protected void addEmail(String data) {
		Message newmessage = new Message(data);

		scrollTable.row();
		scrollTable.top();
		scrollTable.add(newmessage).expandX().fillX(); //.fillX();//.width(200);
	
		
		AllMessages.add(newmessage);
		
		numOfEmails=numOfEmails+1;
		
	}



	public void removeAllMessages() {
		scrollTable.clear();
		validate();
	}

	 protected static class Message extends Label 
	 {
		 public Message(String contents){
			 
			 super(contents,DefaultStyles.linkstyle);				 
			 super.setWrap(true);
			 invalidate();
			 
		 }
		 
	 }
	

}
