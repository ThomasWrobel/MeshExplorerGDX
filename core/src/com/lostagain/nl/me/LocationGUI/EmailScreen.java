package com.lostagain.nl.me.LocationGUI;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;

public class EmailScreen extends Container<ScrollPane>  implements LocationScreen {

	static Logger Log = Logger.getLogger("EmailScreen");
	private int loadingEmails=0;
	//private Table container = new Table();
	private ArrayList<Message> AllMessages = new ArrayList<Message>();
	final Table scrollTable;
	//static Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	 final ScrollPane scroller;
	 
	public int numOfEmails=0;
	
	public EmailScreen(LocationsHub parentLocationContainer, SSSNode securedBy) {
		
		//super.setDebug(true);
		super.setX(0);
		super.setY(0);
		
		

	        scrollTable = new Table();
	        scrollTable.setX(0);
	        scrollTable.setY(0);
	        
	        scroller = new ScrollPane(scrollTable);
	        
	       // ScrollPaneStyle scrollerstyle = new ScrollPaneStyle(DefaultStyles.defaultStyles.get(ScrollPaneStyle.class));
	       // scrollerstyle.background = DefaultStyles.colors.newDrawable("white", Color.RED);
	       // scroller.setStyle(scrollerstyle);
	        
	        
	        scrollTable.setBackground(DefaultStyles.colors.newDrawable("white", new Color(0.2f,0.2f,0.2f,0.5f)));
	        scroller.setX(0);
	        scroller.setY(0);
	        
	        scroller.setDebug(true);
	        scroller.setFillParent(true);
	      //  final Table table = new Table();
	       // table.setFillParent(true);
	      //  table.add(scroller).fill().expand();

	        super.left();	       
	        super.bottom();
	        
        super.setActor(scroller);
   
        
        

	}
	

	
	@Override
	public void layout(){
		
		//Log.info("validate email__");
		super.layout();
		
		scroller.validate();
		scroller.setHeight(super.getParent().getHeight());
		
		scrollTable.validate();
		
	}
	
	
	public void addEmailLocation(SSSNode sssNode,final SSSNode language) {
		
		
		loadingEmails++;

		Log.info("Adding email location________________");
		
		//add a placeholder email straight away to ensure they show up in the correct order 
		final Message NewEmailMessage = addEmail(" Loading..."+sssNode,language);
		NewEmailMessage.setSourcefilename(sssNode.getPLabel());
		
		
		
		//load the data and fill in the message with it
		//we can borrow the SuperSimpleSemantics file load for this and 
		
		//set up the runnable for when the data is retrieved
		FileCallbackRunnable runoncomplete = new FileCallbackRunnable(){
			@Override
			public void run(String responseData, int responseCode) {
				loadingEmails--;
				
				NewEmailMessage.setText(responseData);
				
				invalidate();
				checkForEmailsFinnishedLoading();
				
				
			}

			
			
		};
		
		FileCallbackError runonerror = new FileCallbackError(){

			@Override
			public void run(String errorData, Throwable exception) {
				loadingEmails--;
				
				Log.info(errorData);

				NewEmailMessage.setText(errorData);
				invalidate();
				checkForEmailsFinnishedLoading();
			}
			
		};
		
		//String url = "semantics\\"+sssNode.getPLabel();
		
		String uri = sssNode.getPURI();
		
		Log.info("getting email from uri:"+uri);
		Log.info("getting email from label:"+sssNode.getPLabel());
		
		//strip the uri to its path and add the label too it
		//this turns the URI location into a file location
		
		//ie http://darkflame.co.uk/semantics/darksnet.ntlist#darkspc\message2.txt
		// should become
		// http://darkflame.co.uk/semantics/message2.txt
		String directory = "";
				
		//crop to # 
		if (uri.contains("#")){
			uri = uri.substring(0,uri.indexOf("#"));
		}
		
		Log.info("getting email from uri:"+uri);
		//as we know at least one is present we can just look or the largest index
		int endslash = 0;
		int endslash2 =0;
		
		//remove to /
		if (uri.contains("/")){
			endslash =  uri.lastIndexOf("/");
		}
		//remove to \
		if (uri.contains("\\")){
			endslash2 = uri.lastIndexOf("\\");
			
		}
		//using the maths command
		endslash = Math.max(endslash, endslash2);
		
		directory = uri.substring(0, endslash);

		Log.info("directory:"+directory);
		String url = directory+"/"+sssNode.getPLabel();
		
		Log.info("url::"+url);
				
		//trigger the file retrieval
		SuperSimpleSemantics.fileManager.getText(url, runoncomplete, runonerror, false);
		

		
	}

	public void sortEmails(){
				
		Collections.sort(AllMessages, new Comparator<Message>() {			
			@Override
			public int compare(Message o1, Message o2) {		
				return o1.sourcefilename.compareTo(o2.sourcefilename);				
			}
		});		
		
		//re add them
		scrollTable.clear();
		
		
		for (Message cur : AllMessages) {
			scrollTable.row();
			scrollTable.top();
			scrollTable.add(cur).expandX().fillX(); //.fillX();//.width(200);
			
		}
		
		invalidate();
		
	
	}
	
	private void checkForEmailsFinnishedLoading() {
		// TODO Auto-generated method stub
		if (loadingEmails==0){
			
			Log.info("emails finnished loading");
			
			sortEmails();
			
			/*
			//trigger layout
			validate();
			
			//validate contents
			for (Message email : AllMessages) {
				email.validate();
			}
			
			//sometimes needs to be done twice due to wordwrap in use
			validate();
			*/
		}
		
	}
	
	
	protected Message addEmail(String data,SSSNode lan) {
		
		Message newmessage; 
		if (lan==null){
			 newmessage = new Message(data,StaticSSSNodes.stdascii);
		} else {
			Log.info("message written in:"+lan.getPURI());
			
			 newmessage = new Message(data,lan);
		}

		scrollTable.row();
		scrollTable.top();
		scrollTable.add(newmessage).expandX().fillX(); //.fillX();//.width(200);
	
		
		AllMessages.add(newmessage);
		
		numOfEmails=numOfEmails+1;
		
		return newmessage;
		
	}



	public void removeAllMessages() {
		scrollTable.clear();
		AllMessages.clear();
		validate();
	}

	 protected static class Message extends Label 
	 {
		 
		 public String getSourcefilename() {
			return sourcefilename;
		}

		public void setSourcefilename(String sourcefilename) {
			this.sourcefilename = sourcefilename;
		}

		String sourcefilename = ""; //optional source filename 
		 
		 public Message(String contents,SSSNode language){
			 
			 super(contents,DefaultStyles.linkstyle);				 
			 super.setWrap(true);
			 invalidate();
			 super.setDebug(true);

			 //set to scrambled till language is cofirmed to be known
			 
			 
			 if (language!=null){

				 LabelStyle labstyle = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));
			 	labstyle.font = DefaultStyles.scramabledFont;
			 	labstyle.font.setScale(0.3f);
			 	
			 	//save the default style (means if it changes this wont need to be be)
			 	final LabelStyle defaultStyle = super.getStyle();
			 	
			 	super.setStyle(labstyle);
			 	
				 PlayersData.knownsLanguage(language, new Runnable(){

					@Override
					public void run() {
						
						Log.info("has lan decoder!!!!!!!!!!!!");						
						setStyle(defaultStyle);
						
					}
					 
				 }, new Runnable(){

						@Override
						public void run() {
							
							Log.info("done not have lan decoder!!!!!!!!!!!!");	
							
						}
						 
					 });
				 
				 
			 }
			 
			 
		 }
		 
	 }
	

}
