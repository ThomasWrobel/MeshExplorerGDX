package com.lostagain.nl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Image;
import com.lostagain.nl.me.models.ModelManagment_old;
import com.lostagain.nl.me.models.ModelManagment_old.RenderOrder;
import com.lostagain.nl.shaders.NormalMapShader;

/**
 * a group of images purely to test shaders
 * @author Tom
 *
 */
public class ShaderTestPanel extends HorizontalPanel {

	public ShaderTestPanel(){

		this.getStyle().setBackgroundColor(Color.BLACK);
		
	//	this.getMaterial().set(new ZIndexAttribute(50)); //temp test

		//make rock test
		//	Image testRockImage = new Image(Gdx.files.internal("data/rock.png")); // //   diffuseMap        = new Texture(Gdx.files.internal("data/rock.png")); (rocktexture test)
		//	Texture rockNormals = new Texture(Gdx.files.internal("data/rock_n.png"));
		////	testRockImage.setShaderAttribute(new NormalMapShader.NormalMapShaderAttribute(rockNormals),false); 	
		//this.add(testRockImage);		


		//make infovour with rock texture
		//Image testImage = new Image(Gdx.files.internal("data/infovours/genericinfovour.png")); 
		//Texture rockNormals = new Texture(Gdx.files.internal("data/rock_n.png"));
		//testImage.setShaderAttribute(new NormalMapShader.NormalMapShaderAttribute(rockNormals),false); 
		//this.add(testImage);

		//---
		addNormalMapedImage("data/infovours/genericinfovour.png","data/rock_n.png");

		//rocks
		addNormalMapedImage("data/rock.png","data/rock_n.png");

		//make brick test
		addNormalMapedImage("data/normalmaptests/bricks_handpainted_col.png","data/normalmaptests/bricks_handpainted.png",0.3f);

		//more tests
		addNormalMapedImage("data/normalmaptests/flatdifuse.png","data/normalmaptests/normalmap_texture_example.png");
		addNormalMapedImage("data/normalmaptests/flatdifuse.png","data/normalmaptests/normalmap_texture_example2.png");
		addNormalMapedImage("data/normalmaptests/flatdifuse.png","data/normalmaptests/painted2.png");
		addNormalMapedImage("data/normalmaptests/flatdifuse.png","data/normalmaptests/test_normal.png");
		addNormalMapedImage("data/normalmaptests/flatdifuse.png","data/normalmaptests/test_normal2.png");
		addNormalMapedImage("data/normalmaptests/flatdifuse.png","data/normalmaptests/test_normal3.png");


	}

	public void addNormalMapedImage(String imageLoc, String normalLoc) {
		addNormalMapedImage(imageLoc,normalLoc,1);
	}

	public void addNormalMapedImage(String imageLoc, String normalLoc, float Scale) {

		Image testBrickImage = new   Image(Gdx.files.internal(imageLoc)); 
		testBrickImage.setToScale(new Vector3(Scale,Scale,Scale));

		Texture normals = new Texture(Gdx.files.internal(normalLoc));
		testBrickImage.setShaderAttribute(new NormalMapShader.NormalMapShaderAttribute(normals),false); 
		ModelManagment_old.addmodel(testBrickImage, RenderOrder.infrontStage);
		add(testBrickImage);		


		//testBrickImage.setShaderAttribute(new ZIndexAttribute(50),false); //temp test
	}

	//not implemented
	//public void addNormalMapedImageWithAO(String imageLoc, String normalLoc,String ambientLoc, float Scale) {
	//
	//}
	//

}
