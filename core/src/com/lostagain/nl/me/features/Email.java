package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.GWTish.Style.TextAlign;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.models.objectType;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.shaders.GwtishWidgetDistanceFieldAttribute;
import com.lostagain.nl.shaders.MySorter.ZIndexAttribute;

/**
 * represents 1 single email page.
 * If a email is a reply to another one, it automatically links itself to that one
 * 
 * @author Tom
 *
 */
public class Email extends VerticalPanel implements GenericMeshFeature {
	final static String logstag = "ME.Email";
	Email    parentEmail; //what this is a reply to?
	EmailHub parentHub;

	Label emailContents;
	
	//temp notes;
	//size doesn't update when email does
	
	public Email(EmailHub parentHub,SSSNode sssNode, SSSNode writtenIn) {
		super.setPadding(25f); //test
		this.setAsHitable(true); //we need this to control dragging
		
		this.parentHub=parentHub;
		emailContents = new Label("Loading.....");	
		emailContents.getStyle().setColor(Color.BLACK);
		emailContents.setToScale(new Vector3(0.7f,0.7f,0.7f));
		
		this.add(emailContents);
		
		emailContents.getStyle().setTextStyle(GwtishWidgetDistanceFieldAttribute.presetTextStyle.whiteWithShadow);
		this.getStyle().clearBackgroundColor(); 
		this.getStyle().clearBorderColor();
		
		emailContents.getStyle().setTextAlignment(TextAlign.LEFT);
		emailContents.getStyle().clearBackgroundColor();
		emailContents.getStyle().clearBorderColor();
		emailContents.setMaxWidth(720f);
		
		
		Gdx.app.log(logstag,"added emailContents:"+emailContents.isVisible());	
		
		
		testIfWeHaveLanguage(writtenIn);
		getEmailContents(sssNode,writtenIn);
		
	}

	/**
	 * returns true if no language is specified (ie, its null) else uses a internal callback to update
	 * the label with the correct style
	 * 
	 * @param language
	 * @return
	 */
	private boolean testIfWeHaveLanguage(SSSNode language) {
		
		
		 if (language!=null){

				Gdx.app.log(logstag,"testing if we have ________________"+language);
				
			LabelStyle labstyle = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));
			
		 	labstyle.font = DefaultStyles.scramabledFont;
		 	labstyle.font.getData().setScale(0.3f);
		 	
		 	setStyleAsScrambled(labstyle);
		 	
		 	
			 PlayersData.knownsLanguage(language, new Runnable(){

				@Override
				public void run() {
					
					Gdx.app.log(logstag,"has lan decoder!!!!!!!!!!!!");						
					setStyleAsNormal();
					
				}

				 
			 }, new Runnable(){

					@Override
					public void run() {
						
						Gdx.app.log(logstag,"does not have lan decoder!!!!!!!!!!!!");	
						
					}
					 
				 });
			 
			 return false;
		 } else {
			 return true;
		 }
		
	}

	/**
	 * sets the style to scrambled by changing the font
	 * (later we might have nicer scambled effects)
	 * @param labstyle
	 */
	private void setStyleAsScrambled(LabelStyle labstyle) {
		// TODO Auto-generated method stub
		
	}

	private void setStyleAsNormal() {
		// TODO Auto-generated method stub
		
	}
	private void getEmailContents(SSSNode sssNode, SSSNode writtenIn) {
		
		
		//load the data and fill in the message with it
		//we can borrow the SuperSimpleSemantics file load for this and 
		
		//set up the runnable for when the data is retrieved
		FileCallbackRunnable runoncomplete = new FileCallbackRunnable(){
			@Override
			public void run(String responseData, int responseCode) {
	
				
				emailContents.setText(responseData);
				
				
				
			}

			
			
		};
		
		FileCallbackError runonerror = new FileCallbackError(){

			@Override
			public void run(String errorData, Throwable exception) {
				
				Gdx.app.log(logstag,""+errorData);

				emailContents.setText(errorData);
			}
			
		};
		
		//String url = "semantics\\"+sssNode.getPLabel();
		
		String uri = sssNode.getPURI();
		
		Gdx.app.log(logstag,"getting email  uri:"+uri);
		Gdx.app.log(logstag,"getting email  label:"+sssNode.getPLabel());
		
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
		
		Gdx.app.log(logstag,"getting email from uri:"+uri);
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

		Gdx.app.log(logstag,"directory:"+directory);
		String url = directory+"/"+sssNode.getPLabel();
		
		Gdx.app.log(logstag,"url::"+url);
				
		//trigger the file retrieval
		SuperSimpleSemantics.fileManager.getText(url, runoncomplete, runonerror, false);
		
	}

	@Override
	public AnimatableModelInstance getAnimatableModelInstance() {
		return this;
	}

	@Override
	public void updateApperance(float alpha, FeatureState currentState) {
		setOpacity(alpha);
		
	}

	MeshIcon parentIcon = null;
	@Override
	public void setParentMeshIcon(MeshIcon icon) {
		parentIcon = icon;
		return;
	}

	@Override
	public MeshIcon getParentMeshIcon() {
		return parentIcon;
	}

	@Override
	public void fireTouchDown() {
		super.fireTouchDown();
	}

	
	
	@Override
	public void fireTouchUp() {
		super.fireTouchUp();
		
		//close if was not dragging 
		if (!dragging){
			parentIcon.close();
		}
		

		dragging=false;
		
	}
	boolean dragging=false;
	
	@Override
	public void fireDragStart() {
		super.fireDragStart();
		dragging = true;
		
		//currently when dragging we tell the scene itself to drag
		//this is because we have no widget with scroll functionality, long emails just take up the whole landscape
		//and you need to pan the landscape
		MainExplorationView.setAsDragging();
		
		Gdx.app.log(logstag,"dragging email");
		
	}

	/**
	 * does this object block whats behind it?
	 * @return
	 */
	@Override
	public objectType getInteractionType() {
		return objectType.Blocker;
	}


	
	@Override
	public Vector3 getDefaultCameraPosition() {
		//gets the center of this email on the stage
		Vector3 center = getCenterOnStage().cpy();
		center.z = ScreenUtils.getSuitableDefaultCameraHeight();
		
		
		
		//we need to align the top of the email to the top of the screen
		//BoundingBox topOfEmail   = getLocalBoundingBox().mul(getMatrixTransform());
				
		float screenHeight = Gdx.graphics.getHeight();
		Vector2 topLeftOfScreen     = ME.screenToStage(new Vector2(0,0));
		Vector2 bottomRightOfScreen = ME.screenToStage(new Vector2(0,screenHeight));
		
		//Gdx.app.log(logstag,"  topOfEmail:"+topOfEmail);
		Gdx.app.log(logstag,"topLeftOfScreen:"+topLeftOfScreen.y);
		Gdx.app.log(logstag,"bottomRightOfScreen:"+bottomRightOfScreen.y);
		Gdx.app.log(logstag,"stage height:"+(topLeftOfScreen.y-bottomRightOfScreen.y));
		
		float viewHeight = (topLeftOfScreen.y-bottomRightOfScreen.y);
		
		
		float difference = this.getHeight() - viewHeight;
		
		center.y = center.y+(difference/2);

		Gdx.app.log(logstag,"difference:"+difference);
		Gdx.app.log(logstag,"center:"+center);
		
		//center.y = center.y - (this.getHeight()/2);
				//topOfEmail.get.getCorner000().;
		
		return center;
	}


	@Override
	public void setZIndex(int index, String group) {
		//set zindex of back material
		super.getStyle().addAttributeToShader(new ZIndexAttribute(index,group));
	
		//but we also need to apply it to all subobjects (only a little higher!)
		for (Widget childwidget : super.getChildren()) {
			childwidget.getStyle().addAttributeToShader(new ZIndexAttribute(index+1,group)); //NOTE; wont work for sub-sub objects as zindex isnt part of gwtish
		}
		
		
	}

	
}
