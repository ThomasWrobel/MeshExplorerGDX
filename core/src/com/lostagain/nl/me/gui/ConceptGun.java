package com.lostagain.nl.me.gui;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.camera.MECamera;
import com.lostagain.nl.me.gui.DataObjectSlot.OnDropRunnable;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.me.objects.DataObject;
import com.lostagain.nl.shaders.ConceptBeamShader;
import com.lostagain.nl.shaders.MyShaderProvider;

/** the concept gun is in many ways the main gimic of the game 
 * 
 * It allows you to apply a "concept" to a creature in the game. 
 * This is represented by shooting it with a beam. The creature then responds or not based on if the concept 
 * forfills its criteria.
 * eg. Lactos intolerant enemy's get damaged by cheese,milk,yogaurt beams
 * Cyberman would be damaged by gold beams
 * etc.
 * 
 * The style of the beam should eventually reflect some aspects of the concept thats equiped into it 
 * Start with just color, but add size and some other effects later **/
public class ConceptGun  extends WidgetGroup {
	final static String logstag = "ME.ConceptGun";

	//guns current stats
	static int MaxComplexityLevel = 2; //will be used to determine how complex the equipped class can be
	static float FireFrequency = 2.5f;//means the gun will actually hit targets at this frequency per sec regardless of visual effect (you should ensure your visuals are sycned to this)
	//size
	static int width = 3;
	
	public static SSSNode equipedConcept = null; //the currently equipped concept. Think of it as ammo

	//private static float firePointX = 0;
	//private static float firePointY = 0;

	private static Vector2 firePoint = new Vector2(0,0); //relative to the middle of the top of the screen


	DataObjectSlot conceptInUse = new DataObjectSlot();
	int height = 50; //width is always screen width as its a bar at the top


	final Label status = new Label("needs ammo",DefaultStyles.linkstyle);


//	public static boolean disabledFire = true;
	
	enum LazerState {
		disabled,fireing,
		/** ready means its over 50% charged. If unfired it will continue charging till 100% **/
		ready,
		/** less then 50% charged, cant fire **/
		charging
	}
	
	static LazerState currentLazerState = LazerState.disabled;
	
	Material currentBeamMaterial;
	
	

	//ammo dependent styles
	private ArrayList<Color> currentColors = new ArrayList<Color>();

	//impact effect

	static Timer impactEffectTimer = new Timer();
	static Task impactEffectTask;
	
	
	//other
	AnimatableModelInstance lazer;
	
	private float timeSinceLastHitCheck = 0f;
	
	private float totalCharge=5f; //0.8

	private SpriteProgressBar rechargeProgress = new SpriteProgressBar();
	
	private float rechargeTime=totalCharge;
	
	private PosRotScale lazerbeamdisplacement = new PosRotScale(0f,0f,-30f);

	Vector2 beamcenteroffset = new Vector2(0,1f);
	
	public ConceptGun() {

		LabelStyle back = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));		
		Color ColorM = new Color(Color.MAROON);
		ColorM.a=0.95f;
		back.background = DefaultStyles.colors.newDrawable("white", ColorM);
		Label backgroundobject = new Label("",back);
		backgroundobject.setPosition(0,0);
		//backgroundobject.setSize(200, 220);
		backgroundobject.setFillParent(true);

		backgroundobject.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				Gdx.app.log(logstag, "backgroundobject clicked (" + x + ", " + y + ")");

			}
		});



		status.setPosition(70, 10, Align.center);
		
		super.addActor(backgroundobject);	
		super.addActor(status);

		rechargeProgress.setWidth (DataObject.getStandardWidth());
		rechargeProgress.setHeight(DataObject.getStandardHeight());
		rechargeProgress.invalidateHierarchy();
		
		super.addActor(rechargeProgress);		
		super.addActor(conceptInUse);

		
		conceptInUse.onDragRun(new Runnable() {
			public void run() {
				
				status.setText("Ammo Not Set:");
				//disabledFire = true;
				currentLazerState = LazerState.disabled;
				
				equipedConcept = null;
				
				
			}
		});



		conceptInUse.onDropRun(new OnDropRunnable() {			
			@Override
			public void run(DataObject drop) {

				status.setText("Ammo Set To: "+drop.itemsnode.getPLabel());
				Gdx.app.log(logstag, "Ammo Set To: "+drop.itemsnode.getPLabel());
				
				currentLazerState = LazerState.ready;
				
				//disabledFire = false;
				equipedConcept = drop.itemsnode;

				updateRayStyle(drop);



			}


		});

		//backgroundobject.setWidth(super.getPrefWidth());
		//backgroundobject.setHeight(super.getPrefHeight());

		this.setWidth(90);
		this.setHeight(40);
		this.setPosition(0, 100);
		
		
		//animation that happens at end of impact
		//currently just restores camera to normal displacement from which it was moved when impact started
		impactEffectTask  = new Task() {

			@Override
			public void run() {
				
				MainExplorationView.currentPos.z = MainExplorationView.currentPos.z+4f; 
				//MainExplorationView.CurrentZoom = MainExplorationView.CurrentZoom +0.04f; 

				
			}
			
		};
		
		

		//generate lazer mesh and texture		
		createLazerObject(randomColorFromConcept());

		
		this.invalidate();
	}

	/** updates the ray style based on the drop (ie, its nodes color= property if any)**/
	private void updateRayStyle(DataObject drop) {

		//get all objects property
		SSSNode node = drop.itemsnode;

		currentColors = DefaultStyles.getColorsFromNode(node); //we only use the first color

		if (currentColors==null){
			currentColors = new ArrayList<Color>();
			currentColors.add(Color.RED); //red by default
		}

	}



	@Override
	public void layout(){
		Gdx.app.log(logstag, "concept needslayout ");
		this.setWidth(MainExplorationView.guiStage.getWidth());

		//this.setWidth(300);
		this.setHeight(height);	

		//temp commwent out
		this.setPosition(this.getParent().getWidth(),this.getParent().getHeight()-height);		//MainExplorationView.guiStage.getHeight()


		rechargeProgress.layout();
		
		    conceptInUse.setPosition(this.getWidth()-conceptInUse.getWidth(), height-5,Align.topRight);
		rechargeProgress.setPosition(this.getWidth()-conceptInUse.getWidth(), height-5,Align.topRight);
		
		status.setPosition(90, 10, Align.bottom);

		firePoint.x = super.getWidth()/2;
		firePoint.y = super.getHeight()/2;
	}

	/**creates a screen relative beam from the interface to the point clicked
	 * 
	 * Note; This is just for the effect of firing, objects will react to being independent of this function as they are triggered
	 * by their own mouse actions **/
	public void fireAt(float x, float y){
//
		//if (disabledFire || lazer!=null){
			
		//	return;
		//}
		
		if (currentLazerState != LazerState.ready){
			return;
		}
		//MainExplorationView.currentPos.z = MainExplorationView.currentPos.z+5f; 
		//MainExplorationView.CurrentZoom = MainExplorationView.CurrentZoom +0.02f; 
		
		Gdx.app.log(logstag, " createBeamEffect targeting:"+x+","+y);

		//from
		Vector2 sc = MainExplorationView.gameStage.screenToStageCoordinates(firePoint.cpy());
		

		Vector2 cp = new Vector2(x,y);						
		Vector2 cursor_on_stage =  MainExplorationView.gameStage.screenToStageCoordinates(cp);
		Gdx.app.log(logstag, " createBeamEffect targeting stage:"+cursor_on_stage.x+","+cursor_on_stage.y);
		
		//color
		Color BeamColor = new Color(randomColorFromConcept());

		//col.a  = 1 - (500 / 1000) ^ 2;
		
		//set color
		lazer.materials.get(0).set(new ConceptBeamShader.ConceptBeamAttribute(0.35f,BeamColor,FireFrequency,Color.WHITE));
		
		
		//attach lazer to render list if not there already (this command checks that itself)
		ModelManagment.addmodel(lazer,ModelManagment.RenderOrder.infrontStage);



		//rechargeTime = totalCharge;

		currentLazerState=LazerState.fireing;

	}

	private void createLazerObject(Color BeamColor) {
		//	MessyModelMaker.createLine(fx, fy, width, cursor_on_stage.x-(width/2), cursor_on_stage.y,22,col,true,false,10); //10 makes sure the end point is wayyyyyyyy of the screen at the top to ensure the beam end never becomes visible during movement
			
			//ColorAttribute.createDiffuse(Color.RED),
			Gdx.app.log(logstag, "set to gun beam ");
			
/*
		    Material lazerMat = new Material(
		    		ColorAttribute.createDiffuse(Color.RED), 
					ColorAttribute.createSpecular(Color.WHITE),
					new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1f), 
					FloatAttribute.createShininess(16f));
		    
			*/

			
			 currentBeamMaterial = new Material("LazerMaterial", 
					
					               new ConceptBeamShader.ConceptBeamAttribute(0.35f,BeamColor,FireFrequency,Color.WHITE),
								   new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.99f)
			
			);
					
					
					//ColorAttribute.createDiffuse(Color.ORANGE),
					//new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.5f));//new ConceptBeamShader.ConceptBeamAttribute(0.25f,col,FireFrequency,Color.WHITE),
			
			
		//	ModelInstance newlazer = ModelMaker.createLineBetween(fx, fy, width, cursor_on_stage.x, cursor_on_stage.y,22,col,lazerMat,1);
			
			//new method we just create a rectangle then rotate/set its position ourselves
			float hw = width/2.0f;
			//float height = Math.abs(fy  - cursor_on_stage.y);
			float height = 50f; //Gdx.graphics.getHeight()*1.1f; //always do it a bit bigger to allow for movements
			
			//note we offset its creation points by the beam center offset
			//this means the "center" of the rectangle is where the beam effect his and can be adjusted easily to match any change inthe graphic effect
			Model lazermodel = ModelMaker.createRectangle((0-hw)-beamcenteroffset.x, -beamcenteroffset.y, hw-beamcenteroffset.x, height-beamcenteroffset.y, 0, currentBeamMaterial);
			
			
			
			AnimatableModelInstance newlazer = new AnimatableModelInstance(lazermodel);
			
			//align to camera
			
			//attach to camera?	
			newlazer.transState.setTo(MainExplorationView.camera.transState);
			
			MainExplorationView.camera.attachThis(newlazer, lazerbeamdisplacement);
			
			
			//give it the position and rotation of the camera
			//Vector3 direction = MainExplorationView.camera.direction;
			//Gdx.app.log(logstag, "direction "+direction.x+","+direction.y+","+direction.z);
			
			//Vector3 up = MainExplorationView.camera.up;
			//Gdx.app.log(logstag, "up "+up.x+","+up.y+","+up.z);

			
			//(how to sycn to camera?)
			
			
			//newlazer.userData = MyShaderProvider.shadertypes.conceptbeam;
			
			
			
		//	newlazer.nodes.get(0).
			
			//Renderable test = new Renderable();		
			//newlazer.getRenderable(test);
		//	test.shader = new ConceptBeamShader();
			//	ModelManagment.myshaderprovider.testListShader(test);
			
			
		//	if (lazer!=null){
		//		MessyModelMaker.removeModelInstance(lazer); //one beam at a time for now!
		//	}
			lazer=newlazer;
			

			//display it		
		//	ModelManagment.addmodel(lazer,ModelManagment.RenderOrder.infrontStage);
			
	}

	/**
	 * if the gun is firing this tests for hits under its target
	 */
	public void testForHits(){

		//Vector2 currentCursor = MainExplorationView.getCurrentCursorScreenPosition();
		
		//Gdx.app.log(logstag, " testing for hits at: "+currentCursor.x+","+currentCursor.y);
		//Ray ray = MainExplorationView.camera.getPickRay(currentCursor.x, currentCursor.y);
		
		Ray ray = MainExplorationView.getCurrentStageCursorRay();
		MainExplorationView.touchedAModel = ModelManagment.testForHits(ray,true);
		
		if (MainExplorationView.touchedAModel){
			Gdx.app.log(logstag,"_-(hit at least one thing)-_");
		}
		
	}
	
	public void update(float delta){
		if (currentLazerState==LazerState.fireing){

			
			//work out new angle (if mouse has moved)

			//update beam is mouse still down
			if (Gdx.input.isTouched() && rechargeTime>0){
			
				//from is the fire target
				Vector2 fromPoint = MainExplorationView.getCurrentCursorScreenPosition();   //MainExplorationView.getCurrentStageCursorPosition();// .gameStage.screenToStageCoordinates(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
				//too is the gun mussel (yeah, backwards a bit I know)
				Vector2 tooPoint  = firePoint.cpy(); ///MainExplorationView.gameStage.screenToStageCoordinates(firePoint.cpy());
				
				//setup fake camera to work this out (note; really stupid but I struggled with the maths)
				Ray ray = MainExplorationView.camera.getRelativePickRay(fromPoint.x,fromPoint.y);
								
				//we need a plane at the distance of the lazer
				Plane testplane = new Plane(new Vector3(0f, 0f,-1f),-10f);//MainExplorationView.camera.direction.rotate(new Vector3(0f, 0f,1f),90),-50f);
			
				Vector3 intersection = new Vector3();
				Intersector.intersectRayPlane(ray, testplane, intersection);
				//Gdx.app.log(logstag,"_intersection "+foundinc+" = "+intersection.toString());
				
			
				lazerbeamdisplacement.setToPosition(new Vector3(intersection.x,intersection.y,-10));
				
				//set rotation				
				fromPoint.sub(tooPoint);
				
				float newAng = 180-(fromPoint.angle()+90);	
				lazerbeamdisplacement.setToRotation(0, 0, 1, newAng);
			
				
				
				
				
				MainExplorationView.camera.updateAtachment(lazer,lazerbeamdisplacement);
				
				//currenscreentargetX = fromPoint.x; //update cursor pos
			//	currenscreentargetY = fromPoint.y;
				/*
				Vector2 fromPointOri = fromPoint.cpy(); //MainExplorationView.gameStage.screenToStageCoordinates(new Vector2(Gdx.input.getX(),Gdx.input.getY()));

				fromPoint.sub(tooPoint); 	
				float newAng = fromPoint.angle()+90;	

				Gdx.app.log(logstag, " from point:"+fromPoint.x+","+fromPoint.y+" ---- "+tooPoint.x+","+tooPoint.y+"  ang="+newAng);

				lazer.transState.position.set(fromPointOri.x,fromPointOri.y,22);
				lazer.transState.rotation.set(new Vector3(0f,0f,1f), newAng);
				lazer.sycnTransform();
				*/

			//	Matrix4 newmatrix = new Matrix4();			
				//newmatrix.setToRotation(0, 0, 1, newAng);

			//	lazer.transform.mul(newmatrix);
				
				//Gdx.app.log(logstag, " user data:"+lazer.userData.toString());

			//	Gdx.app.log(logstag, " timeSinceLastHitCheck:"+timeSinceLastHitCheck);
				//check for new hits every repeat of pulse
				timeSinceLastHitCheck = timeSinceLastHitCheck + delta;
				rechargeTime = rechargeTime-delta;
				
				if (timeSinceLastHitCheck>(1/FireFrequency)){
					timeSinceLastHitCheck = timeSinceLastHitCheck - (1/FireFrequency); // (1/FireFrequency) is interval
					Gdx.app.log(logstag, " hit check triggered! " + timeSinceLastHitCheck);					
					testForHits();
				}
				
				
				

			} else {
				//remove lazer 
				
				//reset
				//rechargeTime=totalCharge;			
				MessyModelMaker.removeModelInstance(lazer);
				 currentLazerState = LazerState.charging;
			//	lazer=null;;
				
			}

			//	Color col = currentColor;
	

			updateRechargeBar();
			//float times = currenttime/totaltime;
			//Gdx.app.log(logstag, " times:"+times);


			//float wave = (float) Math.cos(((times)*(2*Math.PI))+Math.PI);   //Math.pow((1f - ((10-currenttime) / 10f)),2);// ^ 2f;
			// col.a = (wave+1)/2;

			//Gdx.app.log(logstag, " a"+col.a);

			//lazer.materials.get(0).set(new BlendingAttribute((float) (wave*0.3)));

			//lazer.materials.get(1).set(new BlendingAttribute(wave));

			//lazer.materials.get(2).set(new BlendingAttribute((float) (wave*0.7)));

			//lazer.materials.get(3).set(new BlendingAttribute(wave)); //ColorAttribute.createDiffuse(col)

			if (rechargeTime<0){
				rechargeTime=0;			
				MessyModelMaker.removeModelInstance(lazer);
				 currentLazerState = LazerState.charging;
			//	lazer=null;
				
				//MainExplorationView.currentPos.z = MainExplorationView.currentPos.z-5f; 
				//MainExplorationView.CurrentZoom = MainExplorationView.CurrentZoom -0.02f; cyberman
			}

		}
		
		
		if (currentLazerState==LazerState.charging || currentLazerState==LazerState.ready ){
			//progress the charge timer
			rechargeTime = rechargeTime+delta;
			
			
			if (rechargeTime>(totalCharge/5)){
				currentLazerState=LazerState.ready; //should be over a5th
			}
			
			if (rechargeTime>totalCharge){

				rechargeTime=totalCharge;
			}
			
			updateRechargeBar();
			
			
		}
		
		
		
		
		
	}

	private void updateRechargeBar() {
		
		float percentage = (rechargeTime / totalCharge)*100;
		
		rechargeProgress.setPercentage(percentage);
		
		
	}

	public void setEnabled(boolean status) {
		
		
		
		if (equipedConcept!=null){
			
			if (!status){
				
				currentLazerState = LazerState.disabled;
				
			} else {
				currentLazerState = LazerState.ready;
				
			}
			
		} else {
			
			currentLazerState = LazerState.disabled;

		}
		
	}



	/** effect to use when the beam hits something **/
	static public void animateImpactEffect(){
		
		//do nothing if not active
		if (currentLazerState == LazerState.disabled){
			return;
		}

		
		
		//currently we just jog the camera zoom for a moment to fake a shake (removed, its too annoying)
		/*
		MainExplorationView.currentPos.z = MainExplorationView.currentPos.z-4f; 
		//MainExplorationView.CurrentZoom = MainExplorationView.CurrentZoom -0.04f; 

		
		if (!impactEffectTask.isScheduled()){

			Gdx.app.log(logstag, " scheduleTask");		
			impactEffectTimer.scheduleTask(impactEffectTask, 0.1f);
		}
		*/
	}

	/**
	 * defaults to red
	 * @return
	 */
	private Color randomColorFromConcept() {
		if (currentColors.size()==0){
			return Color.RED;	
		}


		int p = (int) (Math.random()*currentColors.size());


		return currentColors.get(p);

	}

	public static boolean isDisabled() {
		if (currentLazerState == LazerState.disabled){
			return true;
		}
		return false;
	}


}
