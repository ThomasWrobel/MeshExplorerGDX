package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.lostagain.nl.shaders.GlowingSquareShader;

/**
 * Will approximately act like a image widget from GWT
 * 
 * Changes;
 * 1. Will let you render with a custom shader if you wish
 * 2. Different construction methods to take images from various source files
 * 
 * @author Tom
 *
 */
public class Image extends Widget {
	Texture image;
	Material ImageMaterial; 
	static String IMAGEBACKGROUND="IMAGEBACKGROUND";


	public Image(String internalFileLocation) {	
		this(Gdx.files.internal(internalFileLocation));		
	}

	public Image(FileHandle imageFileHandle) {	
		this(new Texture(imageFileHandle));		
	}
	public Image(Texture image) {		
		super(image.getWidth(), image.getHeight());		
		this.image = image;
		ImageMaterial = setupMaterial(image);
		setImage(image,false);//no need to size as we are already correct

	}


	protected Material setupMaterial(Texture image){

		//Material ImageMaterial = new Material(
		//		IMAGEBACKGROUND,
		//		new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1.0f),
		//		TextureAttribute.createDiffuse(image)
		//		);
		
		Material mat = getMaterial();
		mat.clear();
		mat.set(ColorAttribute.createDiffuse(Color.WHITE),
				new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1.0f),				 
				TextureAttribute.createDiffuse(image)
				 );

		
		return mat;
	}




	public void setImage(Texture image){
		setImage(image,true);
	}
	
	//changes the image to the specified one, resizing if needed
	public void setImage(Texture image,boolean size){
		
		Material mat = getMaterial();
		mat.set(TextureAttribute.createDiffuse(image));
		
		if (size){
			
			setSizeAs(image.getWidth(), image.getHeight());
			
		}

	}
	
	/**
	 * lets you add a specific attribute to the material used to render this image
	 * This attribute could contain a ID that your material manager uses to select a custom shader for it
	 * (for example, if you want to do bump mapping on it, or a distance field)
	 * 
	 * @param shadersAttribute
	 */
	public void setShaderAttribute(Attribute shadersAttribute,boolean clearExisting){
		//set the material to use that attribute
		if (clearExisting){
			ImageMaterial.clear();
		}
		
		ImageMaterial.set(shadersAttribute);
		
	}



	/**
	 * Do not use. Images dont support style changes right now.
	 * Might not ever, depending how styles end up being handled.
	 * This is because Images will use their own shader, or one supplied, not the standard text or background shaders giving CSS like functions
	 */
	@Override
	public Style getStyle() {
		return null;

	}


	//setVisibleRect

}
