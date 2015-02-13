package com.lostagain.nl.me.gui;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
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
import com.lostagain.nl.me.gui.DataObjectSlot.OnDropRunnable;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
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

	static int MaxComplexityLevel = 2; //will be used to determine how complex the equipped class can be
	public static SSSNode equipedConcept = null; //the currently equipped concept. Think of it as ammo

	//private static float firePointX = 0;
	//private static float firePointY = 0;

	private static Vector2 firePoint = new Vector2(0,0);

	final static String logstag = "ME.ConceptGun";

	DataObjectSlot conceptInUse = new DataObjectSlot();
	int height = 50; //width is always screen width as its a bar at the top


	final Label status = new Label("needs ammo",DefaultStyles.linkstyle);


	public static boolean disabledFire = true;
	

	//ammo dependent styles
	private ArrayList<Color> currentColors = new ArrayList<Color>();

	//impact effect

	static Timer impactEffectTimer = new Timer();
	static Task impactEffectTask;
	
	
	//other

	ModelInstance lazer;

	private float currenttime=0f;
	private float totaltime=0.8f;

	public ConceptGun() {

		LabelStyle back = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));		
		Color ColorM = new Color(Color.MAROON);
		ColorM.a=0.8f;
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

		super.addActor(conceptInUse);


		conceptInUse.onDragRun(new Runnable() {
			public void run() {
				
				status.setText("Ammo Not Set:");
				disabledFire = true;
				equipedConcept = null;
				
				
			}
		});



		conceptInUse.onDropRun(new OnDropRunnable() {			
			@Override
			public void run(DataObject drop) {

				status.setText("Ammo Set To: "+drop.itemsnode.getPLabel());
				Gdx.app.log(logstag, "Ammo Set To: "+drop.itemsnode.getPLabel());

				disabledFire = false;
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

		conceptInUse.setPosition(this.getWidth()-conceptInUse.getWidth(), height-5,Align.topRight);

		status.setPosition(90, 10, Align.bottom);

		firePoint.x = super.getWidth()/2;
		firePoint.y = super.getHeight()/2;
	}

	/**creates a screen relative beam from the interface to the point clicked
	 * 
	 * Note; This is just for the effect of firing, objects will react to being independent of this function as they are triggered
	 * by their own mouse actions **/
	public void fireAt(float x, float y){

		if (disabledFire){
			return;
		}
		MainExplorationView.currentPos.z = MainExplorationView.currentPos.z+5f; 
		//MainExplorationView.CurrentZoom = MainExplorationView.CurrentZoom +0.02f; 
		
		Gdx.app.log(logstag, " createBeamEffect targeting:"+x+","+y);

		//from
		Vector2 sc = MainExplorationView.gameStage.screenToStageCoordinates(firePoint.cpy());


		float fx = sc.x;
		float fy = sc.y; 


		//size
		int width = 175;

		Vector2 cp = new Vector2(x,y);						
		Vector2 cursor_on_stage =  MainExplorationView.gameStage.screenToStageCoordinates(cp);

		//color
		Color col = new Color(randomColorFromConcept());

		//col.a  = 1 - (500 / 1000) ^ 2;

		//generate mesh and texture
		
		ModelInstance newlazer = ModelMaker.createLineBetween(fx, fy, width, cursor_on_stage.x, cursor_on_stage.y,22,col,10);
		
		
		
		
	//	MessyModelMaker.createLine(fx, fy, width, cursor_on_stage.x-(width/2), cursor_on_stage.y,22,col,true,false,10); //10 makes sure the end point is wayyyyyyyy of the screen at the top to ensure the beam end never becomes visible during movement
		
		Gdx.app.log(logstag, "set to gun beam ");
		newlazer.userData = MyShaderProvider.shadertypes.conceptbeam;
	//	newlazer.nodes.get(0).
		
		//Renderable test = new Renderable();		
		//newlazer.getRenderable(test);
	//	test.shader = new ConceptBeamShader();
		//	ModelManagment.myshaderprovider.testListShader(test);
		
		
		if (lazer!=null){
			MessyModelMaker.removeModelInstance(lazer); //one beam at a time for now!
		}
		lazer=newlazer;


		//display it		
		ModelManagment.addmodel(lazer);

		currenttime = 0;



	}

	public void update(float delta){
		if (lazer!=null){

			//work out new angle (if mouse has moved)

			//update beam is mouse still down
			if (Gdx.input.isTouched()){
				Vector2 tooPoint  = MainExplorationView.gameStage.screenToStageCoordinates(firePoint.cpy());
				
				Vector2 fromPoint = MainExplorationView.getCurrentStageCursorPosition();// .gameStage.screenToStageCoordinates(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
				Vector2 fromPointOri = fromPoint.cpy(); //MainExplorationView.gameStage.screenToStageCoordinates(new Vector2(Gdx.input.getX(),Gdx.input.getY()));

				fromPoint.sub(tooPoint); 	
				float newAng = fromPoint.angle()+90;	

				Gdx.app.log(logstag, " from point:"+fromPoint.x+","+fromPoint.y+" ---- "+tooPoint.x+","+tooPoint.y+"  ang="+newAng);

				lazer.transform.setToTranslation(fromPointOri.x,fromPointOri.y,22);

				Matrix4 newmatrix = new Matrix4();			
				newmatrix.setToRotation(0, 0, 1, newAng);

				lazer.transform.mul(newmatrix);
				
				Gdx.app.log(logstag, " user data:"+lazer.userData.toString());

			}

			//	Color col = currentColor;
			currenttime = currenttime+delta;

			float times = currenttime/totaltime;
			//Gdx.app.log(logstag, " times:"+times);


			float wave = (float) Math.cos(((times)*(2*Math.PI))+Math.PI);   //Math.pow((1f - ((10-currenttime) / 10f)),2);// ^ 2f;
			// col.a = (wave+1)/2;

			//Gdx.app.log(logstag, " a"+col.a);

			lazer.materials.get(0).set(new BlendingAttribute((float) (wave*0.3)));

			//lazer.materials.get(1).set(new BlendingAttribute(wave));

			//lazer.materials.get(2).set(new BlendingAttribute((float) (wave*0.7)));

			//lazer.materials.get(3).set(new BlendingAttribute(wave)); //ColorAttribute.createDiffuse(col)

			if (currenttime>totaltime){
				currenttime=0;			
				MessyModelMaker.removeModelInstance(lazer);
				lazer=null;
				
				MainExplorationView.currentPos.z = MainExplorationView.currentPos.z-5f; 
				//MainExplorationView.CurrentZoom = MainExplorationView.CurrentZoom -0.02f; 
			}

		}
	}

	public void setEnabled(boolean status) {
		if (equipedConcept!=null){
			disabledFire = !status;
		} else {
			disabledFire = true;

		}
	}



	/** effect to use when the beam hits something **/
	static public void animateImpactEffect(){
		
		//do nothing if not active
		if (disabledFire){
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


}
