package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.GWTish.Button;
import com.lostagain.nl.GWTish.DeckPanel;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.camera.MECamera;
import com.lostagain.nl.me.features.ConceptObjectSlot.OnDragRunnable;
import com.lostagain.nl.me.features.ConceptObjectSlot.OnDropRunnable;
import com.lostagain.nl.me.models.ConceptBeam;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.shaders.ConceptBeamShader;

/**
 * New concept gun, currently manages the visual interface and the firing of the gun.
 * In future will also manage stat changes for the gun as well
 * Stats will include;
 * 
 *  - fire frequency 
 *  - recharge speed
 *  - maybe multibeam? 
 *  - ?? (See notes text files for more)
 *  
 *  
 * @author Tom
 */
public class ConceptGunPanel extends HorizontalPanel {
	final static String logstag = "ME.ConceptGunPanel";
	
	/**
	 * the sssnode describes the property of this gun (complexity,firefrequency etc)
	 */
	SSSNode ConceptGunsNode;
	
	
	//guns current stats
	public static int MaxComplexityLevel = 2; //will be used to determine how complex the equipped class can be
	public static float FireFrequency = 2.5f;//means the gun will actually hit targets at this frequency per sec regardless of visual effect (you should ensure your visuals are sycned to this)
		
	//size
	static int width = 50; //3
	public static SSSNode equipedConcept = null; //the currently equipped concept. Think of it as ammo

	int height = 50; //width is always screen width as its a bar at the top
	
	
	enum LazerState {
		disabled,fireing,
		/** ready means its over 50% charged. If unfired it will continue charging till 100% **/
		ready,
		/** less then 50% charged, can't fire **/
		charging
	}
	
	static LazerState currentLazerState = LazerState.disabled;	
	Material currentBeamMaterial;
	
	
	//impact effect
    static Timer impactEffectTimer = new Timer();
	static Task  impactEffectTask;
	
	ConceptBeam lazer3d;
	
	private float timeSinceLastHitCheck = 0f;	
	private float totalCharge=5f; //0.8
	
	
	private float rechargeTime=totalCharge;	
	private PosRotScale lazerbeamdisplacement = new PosRotScale(0f,0f,-30f);
	Vector2 beamcenteroffset = new Vector2(0,1f);
	
	boolean pinned = false; //are we pinned to the interface 	
	
	//--------------------------------
	//widgets:	
	ConceptObjectSlot ammoSlot = new ConceptObjectSlot();
	Label status               = new Label("needs ammo");
	DeckPanel slotAndCharge    = new DeckPanel(ammoSlot.getWidth(),ammoSlot.getHeight());

	private ProgressBar rechargeProgress = new ProgressBar(ammoSlot.getHeight(),0,ammoSlot.getWidth());	
	
	//we also have associated button that shows/hides this panel
	//unlike the panel, this button is always on the interface

	final static String CGunOpen   =  "My CGun<<";
	final static String CGunClosed =  "My CGun>>";
	/*
	Button ConceptGunDeployButton = new Button(ConceptObjectSlot.WIDTH,ConceptObjectSlot.WIDTH, new Runnable() {		
		@Override
		public void run() {
			
			Gdx.app.log(logstag, "myCGun clicked");			
			//toggle concept gun
			if (!ConceptGunPanel.this.isVisible()){
				
				ConceptGunPanel.this.show();
				ConceptGunPanel.this.setEnabled(true);
			//	ConceptGunDeployButton.setText(CGunOpen);
				
			} else {
				ConceptGunPanel.this.hide();				
				ConceptGunPanel.this.setEnabled(false);
			//	ConceptGunDeployButton.setText(CGunClosed);
			}
			
		}
	});*/
	
	
	
	
	public ConceptGunPanel() {
		super.setPadding(15f);
		super.setSpaceing(15f);
		
		this.setMinSize(400f, ammoSlot.getScaledHeight());
		status.getStyle().clearBackgroundColor();
		
		//style widgets
		ammoSlot.getStyle().clearBackgroundColor();
		
		slotAndCharge.getStyle().clearBackgroundColor();
		slotAndCharge.getStyle().clearBorderColor();
		
		//add widgets
		add(status);
		
		slotAndCharge.add(rechargeProgress);
		slotAndCharge.add(ammoSlot);

		add(slotAndCharge);
		this.setCellHorizontalAlignment(slotAndCharge, HorizontalAlignment.Right);
		this.setCellVerticalAlignment(slotAndCharge, VerticalAlignment.Top);
		
		//add(rechargeProgress);

		Color ColorM = new Color(Color.MAROON);
		ColorM.a=0.95f;
		getStyle().setBackgroundColor(ColorM);
		


		
		ammoSlot.setOnDragRun(new OnDragRunnable() {
			public void run(ConceptObject drop) {
				
				status.setText("Ammo Not Set:");
				//disabledFire = true;
				currentLazerState = LazerState.disabled;
				
				equipedConcept = null;
				
				//need a way to reset style of drop?
				drop.setToDefaultBackColour();
				
			}
		});


		ammoSlot.setOnDropRun(new OnDropRunnable() {
			@Override
			public void run(ConceptObject drop) {
				status.setText("Ammo Set To: ");
				Gdx.app.log(logstag, "Ammo Set To: "+drop.itemsnode.getPLabel());
				
				//style drop so the back is transparent?
				drop.setBackgroundColour(new Color(0.5f,0.5f,0.5f,0.5f));
				
				
				currentLazerState = LazerState.ready;
				
				//disabledFire = false;
				equipedConcept = drop.itemsnode;

				updateRayStyle(drop);
			}
				
		});
			
		
		
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
		//createLazerObject(randomColorFromConcept());
		createLazerObject(Color.RED);
		
		setToDefaultPosition();
	}
	
	//default position depends on screen res, as displacement is relative to camera center
	private void setToDefaultPosition() {
		
		//float screenWidth  = Gdx.graphics.getWidth();
		//floa/t screenHeight = Gdx.graphics.getHeight();
/*
		//to work out the point we are attaching things too, we need to cast a ray down from the screen position to the distance of our attachment
		Ray ray = MainExplorationView.camera.getRelativePickRay(10, 70);
		Plane testplane = new Plane(new Vector3(0f, 0f,-1f),-222f);//MainExplorationView.camera.direction.rotate(new Vector3(0f, 0f,1f),90),-50f);
		
		Vector3 intersection = new Vector3();
		Intersector.intersectRayPlane(ray, testplane, intersection);
	
		Gdx.app.log(logstag,"intersection:"+intersection.x+" , "+intersection.y);
		*/
		
		this.setToScale(new Vector3(0.4f,0.4f,0.4f));
		
		MainExplorationView.camera.attachThisRelativeToScreen(this,147,0,240f); //a little behind the rest of the interface
		
		
		this.pinned=true;
	}

	/** updates the ray style based on the drop (ie, its nodes color= property if any)**/
	private void updateRayStyle(ConceptObject drop) {

		MainExplorationView.infoPopUp.displayMessage(" Concept Ammo Loaded:"+drop.itemsnode.getPLabel());
		
		//update beam;
		lazer3d.updateBeam(drop.itemsnode, FireFrequency);
		
		
		
		//get all objects property
		/*
		SSSNode node = drop.itemsnode;

		currentColors = DefaultStyles.getColorsFromNode(node); //we only use the first color

		if (currentColors==null){
			currentColors = new ArrayList<Color>();
			currentColors.add(Color.RED); //red by default
		}
		 */
	}





	/**creates a screen relative beam from the interface to the point clicked
	 * 
	 * Note; This is just for the effect of firing, objects will react to being independent of this function as they are triggered
	 * by their own mouse actions 
	 * 
	 * returns true if it can fire right now**/
	public boolean fireAt(float x, float y){
//
		//if (disabledFire || lazer!=null){
			
		//	return;
		//}
		
		if (currentLazerState != LazerState.ready){
			return false;
		}
		//MainExplorationView.currentPos.z = MainExplorationView.currentPos.z+5f; 
		//MainExplorationView.CurrentZoom = MainExplorationView.CurrentZoom +0.02f; 
		
		Gdx.app.log(logstag, " createBeamEffect targeting:"+x+","+y);

		//from
		//Vector2 sc = MainExplorationView.gameStage.screenToStageCoordinates(firePoint.cpy());
		

		Vector2 cp = new Vector2(x,y);						
		Vector2 cursor_on_stage =  MainExplorationView.gameStage.screenToStageCoordinates(cp);
		Gdx.app.log(logstag, " createBeamEffect targeting stage:"+cursor_on_stage.x+","+cursor_on_stage.y);
		
		//color
		//Color BeamColor = new Color(randomColorFromConcept());
	//	Color BeamColor = Color.RED;
		
		//col.a  = 1 - (500 / 1000) ^ 2;
		
		//set color
		//lazer.materials.get(0).set(new ConceptBeamShader.ConceptBeamAttribute(0.35f,BeamColor,FireFrequency,Color.WHITE));
		
	
		
		//attach lazer to render list if not there already (this command checks that itself)
		//ModelManagment.addmodel(lazer,ModelManagment.RenderOrder.infrontStage);

		//rechargeTime = totalCharge;
		currentLazerState=LazerState.fireing;		
		
		
		
		
		//--------------
		//update its position and look at.
		lazer3d.setToPosition(new Vector3(cursor_on_stage.x,cursor_on_stage.y,0));
		lazer3d.lookAt(MECamera.FirePoint,new Vector3(0,1,0)); //at the moment its the visualizer cube, in future we need a gun shotty shotty point.

		//Tell the beam its been fired
		lazer3d.beamFired(FireFrequency);
		//--------------
		return true;

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
			float height = 300f;//50f; //Gdx.graphics.getHeight()*1.1f; //always do it a bit bigger to allow for movements
			
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
		//	lazer=newlazer;
			

			//display it		
		//	ModelManagment.addmodel(lazer,ModelManagment.RenderOrder.infrontStage);
			
			//Create the new 3d lazer model and fix it to the camera
			//this model is created at a fixed width and a length arbitrarily long.
			//Its positioned at the point of impact and made to point back at the camera.
			//This ensures its always accurate as to where its hitting, as well as ensuring it doesn't go past its impact point
			
			//Model newlazermodel = ModelMaker.createRectangle(-hw,0,hw,700f,0, currentBeamMaterial);			
			//lazer3d = new AnimatableModelInstance(newlazermodel);
			
			lazer3d = new ConceptBeam();
			ModelManagment.addmodel(lazer3d,ModelManagment.RenderOrder.infrontStage);
			//hide by default
			lazer3d.hide();
	}

	/**
	 * If the gun is firing this tests for hits under its target and returns the nearest
	 * returns the nearest object it hit.
	 * @return 
	 */
	public hitable testForHits(boolean processHits){

		
		//Vector2 currentCursor = MainExplorationView.getCurrentCursorScreenPosition();
		
		//Gdx.app.log(logstag, " testing for hits at: "+currentCursor.x+","+currentCursor.y);
		//Ray ray = MainExplorationView.camera.getPickRay(currentCursor.x, currentCursor.y);
		
		Ray ray = ME.getCurrentStageCursorRay();
		MainExplorationView.touchedAModel = ModelManagment.testForHits(ray,true,processHits);
		
		if (MainExplorationView.touchedAModel!=null){
			Gdx.app.log(logstag,"_-(hit at least one thing)-_");
		}
		
		return MainExplorationView.touchedAModel;
		
	}
	
	public void update(float delta){
		if (currentLazerState==LazerState.fireing){

			
			//work out new angle (if mouse has moved)

			//update beam is mouse still down
			if (Gdx.input.isTouched() && rechargeTime>0){
				
				
			/*
				//from is the fire target
				Vector2 fromPoint = ME.getCurrentCursorScreenPosition();   //MainExplorationView.getCurrentStageCursorPosition();// .gameStage.screenToStageCoordinates(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
				//too is the gun mussel (yeah, backwards a bit I know)
				Vector2 tooPoint  = firePoint.cpy(); ///MainExplorationView.gameStage.screenToStageCoordinates(firePoint.cpy());
				
				//setup fake camera to work this out (note; really stupid but I struggled with the maths)
				Ray ray = MainExplorationView.camera.getRelativePickRay(fromPoint.x,fromPoint.y);
								
				float displaceFromCameraZ = -150f; //-10f
				
				//we need a plane at the distance of the lazer
				Plane testplane = new Plane(new Vector3(0f, 0f,-1f),displaceFromCameraZ);//MainExplorationView.camera.direction.rotate(new Vector3(0f, 0f,1f),90),-50f);
			
				Vector3 intersection = new Vector3();
				Intersector.intersectRayPlane(ray, testplane, intersection);
				//Gdx.app.log(logstag,"_intersection "+foundinc+" = "+intersection.toString());
				
			
				lazerbeamdisplacement.setToPosition(new Vector3(intersection.x,intersection.y,	displaceFromCameraZ));
				
				//set rotation				
				fromPoint.sub(tooPoint);
				
				float newAng = 180-(fromPoint.angle()+90);	
				lazerbeamdisplacement.setToRotation(0, 0, 1, newAng);
			
				
				
				MainExplorationView.camera.updateAtachment(lazer,lazerbeamdisplacement);
				*/
				
				
				
				
				
				
				
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
				
				float defaultZ = 0;
				if (timeSinceLastHitCheck>(1/FireFrequency)){
					timeSinceLastHitCheck = timeSinceLastHitCheck - (1/FireFrequency); // (1/FireFrequency) is interval
					Gdx.app.log(logstag, " hit check triggered! " + timeSinceLastHitCheck);					
					hitable collision= testForHits(true);
					
					if (collision!=null){						
						defaultZ = collision.getTransform().position.z;
					}
					
				}
				

				///Update the new 3d lazer;
				Vector2 fromPointStage = ME.getCurrentStageCursorPosition(); 
				lazer3d.setToPosition(new Vector3(fromPointStage.x,fromPointStage.y,defaultZ));
				lazer3d.lookAt(MECamera.FirePoint,new Vector3(0,1,0)); //at the moment its the visualizer cube, in future we need a gun shotty shotty point.
					
				float pulseMul = (timeSinceLastHitCheck/(1/FireFrequency)); //0-1?
				pulseMul = 1+((float) Math.sin(pulseMul * Math.PI*2)); //add 1 and deviding by two ensures we stay positive
				pulseMul=pulseMul/2;
				
						//Math.sin(rechargeTime/(1/FireFrequency));
				
					
				 MainExplorationView.setMouseLight(this.lazer3d.getBeamColor() , 5+(15f*pulseMul));//20f should be linked to power

			} else {
				//remove lazer 
				
				//reset
				//rechargeTime=totalCharge;			
			//	MessyModelMaker.removeModelInstance(lazer);
				
				lazer3d.hide();				
				 currentLazerState = LazerState.charging;
				 
				 
			//	lazer=null;;
				 
				 //set stage light back to normal
				 MainExplorationView.resetMouseLight();
				
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
			//	MessyModelMaker.removeModelInstance(lazer);

				lazer3d.hide();
				 currentLazerState = LazerState.charging;
				 MainExplorationView.resetMouseLight();
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
		//only update if needed
		if (percentage<100 || (percentage==100 && rechargeProgress.CurrentValue!=100)){
			rechargeProgress.setValue(percentage);
		}
		
		
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


	public static boolean isDisabled() {
		if (currentLazerState == LazerState.disabled){
			return true;
		}
		return false;
	}

	
	public void updateParameters(SSSNode ability) {
		
		// TODO Auto-generated method stub
		
	}


}
