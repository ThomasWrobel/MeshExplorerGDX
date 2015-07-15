package com.lostagain.nl.GWTish;

import java.util.HashSet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector2;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.shaders.GlowingSquareShader;
import com.lostagain.nl.shaders.GlowingSquareShader.GlowingSquareAttribute;

/**
 * This will approximate a similar function as GWTs Widget class does
 * 
 * @author Tom *
 */
public class Widget extends AnimatableModelInstance {

	final static String logstag = "GWTish.Widget";
	
	static Material DefaultWhiteBackground = new Material("Background",
			   new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.99f),
			   new GlowingSquareShader.GlowingSquareAttribute(3f,Color.BLACK,Color.WHITE,Color.RED));
	
	//Handlers
	/**
	 * A HashSet of all the size change handlers associated with this object
	 * @param object
	 */
	HashSet<Runnable> SizeChangeHandlers = new HashSet<Runnable>();

	/**
	 * Specifies where the pivot should go on the backing model for this widget
	 * TOPLEFT is effective normal for GWT like behavior, but we are keeping it flexible here for now
	 * @author Tom
	 *
	 **/
	enum MODELALIGNMENT {
		TOPLEFT,CENTER,BOTTOMRIGHT
	}
	MODELALIGNMENT alignment = MODELALIGNMENT.TOPLEFT;
	
	public Widget(Model object) {
		super(object);
	}
	
	public Widget(float sizeX,float sizeY) {
		super(generateBackground(10,10,DefaultWhiteBackground.copy(),MODELALIGNMENT.TOPLEFT)); //alignment topleft by default
		
	}
	
	/**
	 * Sets the opacity of the background
	 * @param opacity
	 */
	public void setOpacity(float opacity){		
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial("Background");
		((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type)).opacity = opacity;	
	}
	/**
	 * Sets the opacity of the background
	 * @param opacity
	 */
	public void setBackgroundColor(Color backcol){		
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial("Background");
		GlowingSquareAttribute backtexture = ((GlowingSquareShader.GlowingSquareAttribute)infoBoxsMaterial.get(GlowingSquareShader.GlowingSquareAttribute.ID));
		
		backtexture.backColor = backcol;
	}
	
	/**
	 * makes a arbitery sized background that will be expanded as widgets are added
	 * @return
	 */
	protected static Model generateBackground(float sizeX,float sizeY,Material mat,MODELALIGNMENT alignment) {    

		//we first get the offset 
		Vector2 offset =  getOffsetForSize(sizeX, sizeY,alignment);
		
		Model newModel = ModelMaker.createRectangle(-offset.x, -offset.y, sizeX-offset.x,sizeY-offset.y, 0, mat); 		
		return newModel;
	}
	
	
	//handles
	protected void fireAllSizeChangeHandlers (){
		for (Runnable handler : SizeChangeHandlers) {
			handler.run();
		}		
	}
	
	public void addOnSizeChangeHandler(Runnable onSizeChange){
		SizeChangeHandlers.add(onSizeChange);		
	}
	public void removeOnSizeChangeHandler(Runnable onSizeChange){
		SizeChangeHandlers.remove(onSizeChange);		
	}
	
	
	//the offset tells us where the top left corner will be relative to the pivot point.
	//Effectively it lets us have a custom position for the pivot by messuring everything relative to that point 
	//when creating the polygon vectexs
	private static Vector2 getOffsetForSize(float newWidth, float newHeight,MODELALIGNMENT alignment )
	{
		Vector2 offset = new Vector2(0,0);
		switch(alignment)
		
		{
		case CENTER:
			 offset.x = newWidth/2;
			 offset.y = newHeight/2;
			break;
		case TOPLEFT:
			 offset.x = 0;
			 offset.y = newHeight;
			break;
		case BOTTOMRIGHT:
			 offset.x = newWidth;
			 offset.y = 0;
			break;
		
		}
		
		return offset;
		
		
	}
	public void setSizeAs(float newWidth, float newHeight) {
		
		//we first get the offset 
		Vector2 offset =  getOffsetForSize(newWidth, newHeight,alignment);
		//the offset tells us where the top left corner will be relative to the pivot point.
		//Effectively it lets us have a custom position for the pivot by messuring everything relative to that point 
		//when creating the polygon vectexs
		
		setSizeAs(newWidth,  newHeight,offset.x,offset.y);
		
	}
	
	/**
	 * Changes this objects rect mesh background to the new specified size
	 * The internal texture will be stretched
	 * 
	 * @param newWidth
	 * @param newHeight
	 */
	public void setSizeAs(float newWidth, float newHeight,float offsetX,float offsetY) {
		
		Mesh IconsMesh = this.model.meshes.get(0);
		
		final VertexAttribute posAttr = IconsMesh.getVertexAttribute(Usage.Position);
		final int offset = posAttr.offset / 4;
		final int numComponents = posAttr.numComponents;
		final int numVertices = IconsMesh.getNumVertices();
		final int vertexSize = IconsMesh.getVertexSize() / 4;
	
		final float[] vertices = new float[numVertices * vertexSize];
		IconsMesh.getVertices(vertices);
		int idx = offset;
		
		float w = newWidth-offsetX;
		float h = newHeight-offsetY;
		
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
			
			//Gdx.app.log(logstag," new::"+comboX+","+comboY+","+comboZ);
			
			//currently just scale up a bit
			vertices[idx    ] = newSizeArray[i];
			vertices[idx + 1] = newSizeArray[i+1];
			vertices[idx + 2] = newSizeArray[i+2];
			
			idx += vertexSize;
		}
		
		
		IconsMesh.setVertices(vertices);
		
		//recalc bounding box if theres one
		wasResized();
		//ensure things attached are repositioned
		this.updateAllAttachedObjects();
		
		
		fireAllSizeChangeHandlers();
		
	}
	

}
