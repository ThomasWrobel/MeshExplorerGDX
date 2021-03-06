package com.lostagain.nl.me.models;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.GWTish.PosRotScale;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.me.gui.ConceptGun;
import com.lostagain.nl.shaders.ConceptBeamImpactShader;
import com.lostagain.nl.shaders.ConceptBeamShader;

/**
 * A 3d model used to represent the concept beam being fired.
 * It is a flat plan, alligned to go from the target to cameras fire point.
 * It also has a impact point at the end - a seperate plane that will represent the effect where the beam hits the model.
 * 
 * The beams look is determained by the concept ammo currently equiped.
 * Currently this is just the color, but in future it could effect the style of the beam.
 * The concept gun being used also will effect the beam.
 * 
 * Controlled by concept ammo;
 * 
 * 1. Color
 * 2. Style
 * 3. How it effects the target (not controlled here)
 * 
 * Controlled by concept gun type;
 * 
 * 1. Width
 * 2. Speed of pulses
 * 3. Gun fire point (not controlled here)
 * 4. Also how it effects the target (not controlled here)
 * 
 * Typically the concept can be considered the "ammo" - with different things effected by different bits of ammo
 * The concept gun itself controls the rate of fire.
 * 
 * 
 * 
 * **/
public class ConceptBeam extends AnimatableModelInstance {
	final static String logstag = "ME.ConceptBeam";
	 //defaults (may change, but then the model will need adjusting or maybe dumped and recreated?)
	static float  defaultWidth = 60f;
	static float  defaultLength = 700f;
	
	static float  impactWidth = 60f;
	static float  impactLength = 60f;
		
	//ammo dependent styles
	private ArrayList<Color> currentColors = new ArrayList<Color>();
	Color BeamColor;
	
	AnimatableModelInstance impactPoint;
	
	public ConceptBeam() {
		super(makeModel(defaultWidth,defaultLength));	
		
		//create and attach impact point at end
		//this will be a little glow for the moment
		float hw = impactWidth/2f;
		float hh = impactLength/2f;
		
		Model impactPointmodel = ModelMaker.createRectangle(-hw,-hh,hw,hh,0, getStandardImpactMaterial());		//when setting up we get the default material	
		impactPoint = new AnimatableModelInstance(impactPointmodel);
		
		impactPoint.setInheritedRotation(false); //stop the rotation changing as the beam angle changes. For now all the games objects are aligned upwards so the impact should too
		
		//this.attachThis(impactPoint, new PosRotScale(0f,0f,-1f));
		this.attachThis(impactPoint, new PosRotScale(0f,defaultLength,0f));
	}

	
	private Material getStandardImpactMaterial() {
		
		float currentFireFrequency = ConceptGun.FireFrequency;		
		Color BeamColor = Color.RED;
		
		Material impactMat = new Material("ImpactMaterial", 			
				   new ConceptBeamImpactShader.ConceptBeamImpactAttribute(0.35f,BeamColor,currentFireFrequency,Color.WHITE),
				   new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.99f));

		
		return impactMat;
	}

	private static Model makeModel(float width,float length) {

		float hw = width/2f;		
		Model beammodel = ModelMaker.createRectangle(-hw,0,hw,length,0, getStandardBeamMaterial());		//when setting up we get the default material	
		return beammodel;
	}
	
	public void setLength(float length) {
		
		//the		
		Mesh IconsMesh = this.model.meshes.get(0);
		
		final VertexAttribute posAttr = IconsMesh.getVertexAttribute(Usage.Position);
		final int offset = posAttr.offset / 4;
		final int numComponents = posAttr.numComponents;
		final int numVertices = IconsMesh.getNumVertices();
		final int vertexSize = IconsMesh.getVertexSize() / 4;
	
		final float[] vertices = new float[numVertices * vertexSize];
		IconsMesh.getVertices(vertices);
		int idx = offset;
		
		//the offsetX and offsetY determine where the pivot of the mesh is (ie, the 0,0 point of its creation)
		float offsetX = defaultWidth/2f;
		float offsetY = 0;
		
		float w = defaultWidth-offsetX;
		float h = length-offsetY;
		
		//centerl
		//float newSizeArray[] = new float[] { -hw,-hh,0,
		//									  hw,-hh,0,
		//									  hw,hh,0,
		//									 -hw,hh,0 };
		//
		
		float newSizeArray[] = new float[] { -offsetX,-offsetY,0,
											  w,-offsetY,0,
											  w,h,0,
											 -offsetX,h,0 };
				
		//can be optimized latter by pre-calcing the size ratio and just multiply
		for (int i = 0; i < 12; i=i+3) {
			
			
			//currently just scale up a bit
			vertices[idx    ] = newSizeArray[i];
			vertices[idx + 1] = newSizeArray[i+1];
			vertices[idx + 2] = newSizeArray[i+2];
			
			idx += vertexSize;
		}
		
		
		IconsMesh.setVertices(vertices);

		Gdx.app.log(logstag," old size::"+this.getWidth()+","+this.getHeight());
		
		//recalc bounding box if theres one
		wasResized();

		Gdx.app.log(logstag," new size::"+this.getWidth()+","+this.getHeight());
		
		this.updateAtachment(impactPoint, new PosRotScale(0f,length,0f));
		
		//ensure things attached are repositioned
		updateAllAttachedObjects();
		
		

	}
	

	private static Material getStandardBeamMaterial() {
		
		float currentFireFrequency = ConceptGun.FireFrequency;
		
		Color BeamColor = Color.RED;

		Material currentBeamMaterial = new Material("BeamMaterial", 				
				               new ConceptBeamShader.ConceptBeamAttribute(0.35f,BeamColor,currentFireFrequency,Color.WHITE),
							   new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.99f));
		 
		
		return currentBeamMaterial;
	}

	
	public void beamFired(float FireFrequency){
		
		this.show();
		
		//ensure style is upto date (if this ever gets too slow it could be updated when the ammo changes instead of before firing)
		BeamColor = new Color(randomColorFromConcept());
		
		this.materials.get(0).set(new ConceptBeamShader.ConceptBeamAttribute(0.35f,BeamColor,FireFrequency,Color.WHITE));
		
		
		impactPoint.materials.get(0).set(new ConceptBeamImpactShader.ConceptBeamImpactAttribute(0.35f,BeamColor,FireFrequency,Color.WHITE));
		
		
	}
	
	
	/**
	 * Gets a random color from the ammos color selection. (most ammos have one color right now)
	 * Defaults to red
	 * @return
	 */
	private Color randomColorFromConcept() {		
		if (currentColors.size()==0){
			return Color.RED;	
		}
		int p = (int) (Math.random()*currentColors.size());


		return currentColors.get(p);

	}
	
	public void updateBeam(SSSNode currentBeamsConcept,float currentFireFrequency){
		
		//first update the color selection
		currentColors = DefaultStyles.getColorsFromNode(currentBeamsConcept); //we only use the first color

		if (currentColors==null){
			currentColors = new ArrayList<Color>();
			currentColors.add(Color.RED); //red by default
		}
		
		//next time the beam is fired the new style will be used
		
	}


	public Color getBeamColor() {
		return BeamColor;
	}
	
}
